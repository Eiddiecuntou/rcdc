package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.SystemUpgradeCheckResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.SystemUpgradeGlobal;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.StartSystemUpgradeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SystemUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/12
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.REPORT_SYSTEM_UPGRADE_RESULT)
public class SyncSystemUpgradeResultHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSystemUpgradeResultHandlerSPIImpl.class);

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Autowired
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");

        String terminalId = request.getTerminalId();
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(basicInfoEntity.getPlatform().name(), basicInfoEntity.getTerminalOsType());

        SystemUpgradeResultInfo upgradeResultInfo = convertJsonData(request);

        if (upgradeResultInfo.getUpgradeState() == CbbSystemUpgradeStateEnums.UPGRADING) {
            upgradeStart(request, terminalType, upgradeResultInfo);
            return;
        }

        updateTerminalUpgradeStatus(terminalId, terminalType, upgradeResultInfo);
        SystemUpgradeGlobal.releaseUpgradeQuota(terminalId);
        responseTerminal(request, new Object());
    }

    private void upgradeStart(CbbDispatcherRequest request, CbbTerminalTypeEnums terminalType, SystemUpgradeResultInfo upgradeResultInfo) {
        TerminalSystemUpgradeHandler handler;
        try {
            handler = handlerFactory.getHandler(terminalType);
        } catch (BusinessException e) {
            LOGGER.error("终端类型[" + terminalType.name() + "]获取系统升级处理对象失败", e);
            return;
        }
        boolean allowUpgrade = handler.checkAndHoldUpgradeQuota(request.getTerminalId());

        if (allowUpgrade) {
            updateTerminalUpgradeStatus(request.getTerminalId(), terminalType, upgradeResultInfo);
        }

        StartSystemUpgradeResult result = new StartSystemUpgradeResult();
        result.setEnableUpgrade(allowUpgrade);
        responseTerminal(request, result);
    }

    private SystemUpgradeResultInfo convertJsonData(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        SystemUpgradeResultInfo upgradeResultInfo = JSON.parseObject(jsonData, SystemUpgradeResultInfo.class);
        return upgradeResultInfo;
    }

    private void updateTerminalUpgradeStatus(String terminalId, CbbTerminalTypeEnums terminalType, SystemUpgradeResultInfo upgradeResultInfo) {
        Assert.notNull(upgradeResultInfo, "upgradeResultInfo can not be null");
        Assert.notNull(upgradeResultInfo.getUpgradeState(), "upgradeResultInfo.getOtaVersion() can not be null");

        if (upgradeResultInfo.getTaskId() == null) {
            // 防止终端初始上报，无任务id消息不处理
            LOGGER.warn("终端升级任务id为空，不处理该消息");
            return;
        }

        TerminalSystemUpgradePackageEntity upgradePackage = terminalSystemUpgradePackageDAO.findFirstByPackageType(terminalType);
        if (upgradePackage == null || upgradePackage.getIsDelete()) {
            LOGGER.warn("终端类型[{}]的系统升级包不存在或已删除", terminalType.name());
            return;
        }

        List<CbbSystemUpgradeTaskStateEnums> stateList =
                Arrays.asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList =
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId(), stateList);
        if (CollectionUtils.isEmpty(upgradingTaskList) || upgradeResultInfo.getTaskId() == upgradingTaskList.get(0).getId()) {
            LOGGER.info("没有升级中的系统升级任务或升级任务id不一致，不更新终端状态");
            return;
        }

        TerminalSystemUpgradeEntity upgradeTask = upgradingTaskList.get(0);
        TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTask.getId(), terminalId);
        if (upgradeTerminal == null) {
            LOGGER.warn("终端[{}]未在升级任务中", terminalId);
            return;
        }

        CbbSystemUpgradeStateEnums state = upgradeResultInfo.getUpgradeState();
        updateOtaTerminal(upgradeTerminal, state);

    }

    private void updateOtaTerminal(TerminalSystemUpgradeTerminalEntity upgradeTerminal, CbbSystemUpgradeStateEnums state) {
        if (state == CbbSystemUpgradeStateEnums.UPGRADING) {
            upgradeTerminal.setStartTime(new Date());
        }

        upgradeTerminal.setState(state);
        try {
            systemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
        } catch (BusinessException e) {
            LOGGER.error("同步终端[" + upgradeTerminal.getTerminalId() + "]系统升级状态[" + state.name() + "]失败", e);
        }
    }

    private void responseTerminal(CbbDispatcherRequest request, Object object) {
        CbbResponseShineMessage responseMessage = MessageUtils.buildResponseMessage(request, object);
        messageHandlerAPI.response(responseMessage);
    }

}
