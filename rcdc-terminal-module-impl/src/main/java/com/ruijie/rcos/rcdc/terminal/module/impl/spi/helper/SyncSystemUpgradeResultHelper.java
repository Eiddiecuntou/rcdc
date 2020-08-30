package com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.compatible.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SystemUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.StartSystemUpgradeResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Description: 终端检查升级，同时需要保存终端基本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/4
 *
 * @author nt
 */
@Service
public class SyncSystemUpgradeResultHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSystemUpgradeResultHelper.class);

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Autowired
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 处理终端系统升级状态消息
     *
     * @param basicInfoEntity 终端信息
     * @param handler 系统升级处理对象
     * @param request 请求信息
     */
    public void dealSystemUpgradeResult(TerminalEntity basicInfoEntity, TerminalSystemUpgradeHandler handler, CbbDispatcherRequest request) {
        Assert.notNull(basicInfoEntity, "basicInfoEntity can not be null");
        Assert.notNull(handler, "handler can not be null");
        Assert.notNull(request, "request can not be null");
        Assert.notNull(basicInfoEntity.getTerminalId(), "terminalId can not be null");
        Assert.notNull(basicInfoEntity.getTerminalOsType(), "osType can not be null");

        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(basicInfoEntity.getPlatform().name(), basicInfoEntity.getTerminalOsType());

        boolean enableUpgrade = handler.isTerminalEnableUpgrade(basicInfoEntity, terminalType);

        if (!enableUpgrade) {
            // 终端不可升级
            responseNotUpgrade(request);
            return;
        }

        TerminalSystemUpgradeEntity upgradingTask = obtainTerminalSystemUpgradingTask(terminalType);
        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = saveUpgradeTerminalIfNotExist(basicInfoEntity, upgradingTask);

        SystemUpgradeResultInfo upgradeResultInfo = convertJsonData(request);

        // 状态为升级中需校验是否能够升级，且升级终端记录是否存在，不存在则添加
        if (upgradeResultInfo.getUpgradeState() == CbbSystemUpgradeStateEnums.UPGRADING) {
            upgradeStart(request, upgradingTask, upgradeResultInfo, handler);
            return;
        }

        // 更新状态为最终态，判断是否终端实际状态为中间态，不是则不需更新
        if (isUpgradeTerminalInProcessing(upgradeTerminalEntity)) {
            // 更新为其他最终态（非升级中）需释放所占的升级位置
            updateTerminalUpgradeStatus(basicInfoEntity.getTerminalId(), upgradingTask, upgradeResultInfo);
            handler.releaseUpgradeQuota(basicInfoEntity.getTerminalId());
        }

        responseTerminal(request, new Object());
    }

    private boolean isUpgradeTerminalInProcessing(TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity) {
        return upgradeTerminalEntity.getState() == CbbSystemUpgradeStateEnums.UPGRADING
                || upgradeTerminalEntity.getState() == CbbSystemUpgradeStateEnums.WAIT;
    }

    /**
     * 响应不需升级
     *
     * @param request 请求信息
     */
    public void responseNotUpgrade(CbbDispatcherRequest request) {
        Assert.notNull(request, "cbbDispatcherRequest can not be null");

        StartSystemUpgradeResult result = new StartSystemUpgradeResult();
        result.setEnableUpgrade(false);
        responseTerminal(request, result);
    }

    private TerminalSystemUpgradeTerminalEntity saveUpgradeTerminalIfNotExist(TerminalEntity basicInfoEntity,
            TerminalSystemUpgradeEntity upgradingTask) {

        TerminalSystemUpgradeTerminalEntity existUpgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradingTask.getId(), basicInfoEntity.getTerminalId());
        if (existUpgradeTerminal != null) {
            // 升级任务已存在升级终端
            return existUpgradeTerminal;
        }

        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalEntity.setTerminalId(basicInfoEntity.getTerminalId());
        upgradeTerminalEntity.setSysUpgradeId(upgradingTask.getId());
        upgradeTerminalEntity.setState(CbbSystemUpgradeStateEnums.WAIT);
        upgradeTerminalEntity.setCreateTime(new Date());

        return systemUpgradeTerminalDAO.save(upgradeTerminalEntity);
    }

    private TerminalSystemUpgradeEntity obtainTerminalSystemUpgradingTask(CbbTerminalTypeEnums terminalType) {

        List<CbbSystemUpgradeTaskStateEnums> stateList =
                Arrays.asList(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        List<TerminalSystemUpgradeEntity> upgradingTaskList =
                terminalSystemUpgradeDAO.findByPackageTypeAndStateInOrderByCreateTimeAsc(terminalType, stateList);
        Assert.notEmpty(upgradingTaskList, "upgradingTask can not be null");

        // 同一类型的升级中任务仅会存在一个
        return upgradingTaskList.get(0);
    }

    private void upgradeStart(CbbDispatcherRequest request, TerminalSystemUpgradeEntity upgradingTask, SystemUpgradeResultInfo upgradeResultInfo,
            TerminalSystemUpgradeHandler handler) {
        boolean allowUpgrade = handler.checkAndHoldUpgradeQuota(request.getTerminalId());

        if (allowUpgrade) {
            updateTerminalUpgradeStatus(request.getTerminalId(), upgradingTask, upgradeResultInfo);
        }

        StartSystemUpgradeResult result = new StartSystemUpgradeResult();
        result.setEnableUpgrade(allowUpgrade);
        responseTerminal(request, result);
    }

    private SystemUpgradeResultInfo convertJsonData(CbbDispatcherRequest request) {
        LOGGER.info("终端上报升级状态信息 ： {}", request.getData());
        String jsonData = String.valueOf(request.getData());
        SystemUpgradeResultInfo upgradeResultInfo = JSON.parseObject(jsonData, SystemUpgradeResultInfo.class);
        return upgradeResultInfo;
    }

    private void updateTerminalUpgradeStatus(String terminalId, TerminalSystemUpgradeEntity upgradingTask,
            SystemUpgradeResultInfo upgradeResultInfo) {
        Assert.notNull(upgradeResultInfo, "upgradeResultInfo can not be null");
        Assert.notNull(upgradeResultInfo.getUpgradeState(), "upgradeResultInfo.getOtaVersion() can not be null");

        LOGGER.info("upgrading task info : {}", JSON.toJSONString(upgradingTask));
        if (!upgradingTask.getId().equals(upgradeResultInfo.getTaskId())) {
            LOGGER.info("没有升级中的系统升级任务或升级任务id不一致，不更新终端状态");
            return;
        }

        TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradingTask.getId(), terminalId);
        Assert.notNull(upgradeTerminal, "upgradeTerminal can not be null");

        CbbSystemUpgradeStateEnums state = upgradeResultInfo.getUpgradeState();
        updateUpgradeTerminal(upgradeTerminal, state);

    }

    private void updateUpgradeTerminal(TerminalSystemUpgradeTerminalEntity upgradeTerminal, CbbSystemUpgradeStateEnums state) {
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
        LOGGER.debug("响应终端上报升级状态信息 ： {}", responseMessage.toString());
        messageHandlerAPI.response(responseMessage);
    }

}
