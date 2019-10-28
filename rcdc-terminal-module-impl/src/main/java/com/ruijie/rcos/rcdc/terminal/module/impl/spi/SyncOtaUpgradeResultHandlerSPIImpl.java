package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.OtaUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.util.Date;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/12
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.OTA_UPGRADE_RESULT)
public class SyncOtaUpgradeResultHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncOtaUpgradeResultHandlerSPIImpl.class);

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeService systemUpgradeService;

    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");
        // 保存终端基本信息
        String terminalId = request.getTerminalId();
        OtaUpgradeResultInfo otaUpgradeResultInfo = convertJsondata(request);
        basicInfoService.saveBasicInfo(terminalId, otaUpgradeResultInfo.getBasicInfo());
        updateTerminalUpgradeStatus(otaUpgradeResultInfo);
    }

    private OtaUpgradeResultInfo convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        OtaUpgradeResultInfo otaUpgradeResultInfo = JSON.parseObject(jsonData, OtaUpgradeResultInfo.class);
        return otaUpgradeResultInfo;
    }

    private void updateTerminalUpgradeStatus(OtaUpgradeResultInfo otaUpgradeResultInfo) {
        Assert.notNull(otaUpgradeResultInfo, "otaUpgradeResultInfo can not be null");
        Assert.notNull(otaUpgradeResultInfo.getOtaVersion(), "otaUpgradeResultInfo.getOtaVersion() can not be null");
        Assert.notNull(otaUpgradeResultInfo.getBasicInfo(), "otaUpgradeResultInfo.getBasicInfo() can not be null");
        String terminalId = otaUpgradeResultInfo.getBasicInfo().getTerminalId();
        List<TerminalSystemUpgradeEntity> upgradingTaskList =
            systemUpgradeService.getSystemUpgradeTaskByTerminalType(CbbTerminalTypeEnums.VDI_ANDROID);
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("没有OTA升级任务，不更新终端状态");
            return;
        }

        TerminalSystemUpgradeEntity upgradeTask = upgradingTaskList.get(0);
        TerminalSystemUpgradeTerminalEntity upgradeTerminal =
            systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTask.getId(), terminalId);

        CbbSystemUpgradeStateEnums state = otaUpgradeResultInfo.getUpgradeResult();
        String terminalOtaVersion = otaUpgradeResultInfo.getOtaVersion();
        String rcdcOtaVersion = upgradeTask.getPackageVersion();
        if (upgradeTerminal == null) {
            createOtaTerminal(terminalId, upgradeTask, state);
        } else {
            updateOtaTerminal(upgradeTerminal, state, terminalOtaVersion, rcdcOtaVersion);
        }
    }

    private void updateOtaTerminal(TerminalSystemUpgradeTerminalEntity upgradeTerminal, CbbSystemUpgradeStateEnums state, String terminalOtaVersion,
        String rcdcOtaVersion) {
        if (state == CbbSystemUpgradeStateEnums.UPGRADING) {
            upgradeTerminal.setState(state);
            upgradeTerminal.setCreateTime(new Date());
        }

        if (state == CbbSystemUpgradeStateEnums.FAIL) {
            upgradeTerminal.setState(state);
        }

        if (state == CbbSystemUpgradeStateEnums.SUCCESS && terminalOtaVersion.equals(rcdcOtaVersion)) {
            upgradeTerminal.setState(state);
        }
        saveOtaTerminal(upgradeTerminal);
    }

    private void createOtaTerminal(String terminalId, TerminalSystemUpgradeEntity upgradeTask, CbbSystemUpgradeStateEnums state) {
        TerminalSystemUpgradeTerminalEntity upgradeTerminal;
        if (state == CbbSystemUpgradeStateEnums.UPGRADING || state == CbbSystemUpgradeStateEnums.UNSUPPORTED) {
            upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
            upgradeTerminal.setSysUpgradeId(upgradeTask.getId());
            upgradeTerminal.setTerminalId(terminalId);
            // 上报需要升级结果，在列表中，更新时间和状态
            upgradeTerminal.setCreateTime(new Date());
            upgradeTerminal.setState(state);
            saveOtaTerminal(upgradeTerminal);
        }
    }

    private void saveOtaTerminal(TerminalSystemUpgradeTerminalEntity upgradeTerminal) {
        systemUpgradeTerminalDAO.save(upgradeTerminal);
        try {
            systemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
        } catch (BusinessException e) {
            LOGGER.error("同步终端状态失败", e);
        }
    }

}
