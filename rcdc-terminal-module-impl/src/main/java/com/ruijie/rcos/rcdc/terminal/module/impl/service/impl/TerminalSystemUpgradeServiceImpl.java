package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler.TerminalOffLineException;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.SystemUpgradeFileClearHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeResponseMsgHandler;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

/**
 * 
 * Description: 终端升级服务实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeServiceImpl implements TerminalSystemUpgradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeServiceImpl.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Autowired
    private SystemUpgradeFileClearHandler upgradeFileClearHandler;

    @Autowired
    private TerminalSystemUpgradeResponseMsgHandler upgradeResponseMsgHandler;

    @Override
    public void systemUpgrade(String terminalId, TerminalSystemUpgradeMsg upgradeMsg) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(upgradeMsg, "systemUpgradeMsg 不能为空");

        DefaultRequestMessageSender sender;
        try {
            sender = sessionManager.getRequestMessageSender(terminalId);
        } catch (Exception ex) {
            LOGGER.error("terminal offline, terminaId: " + terminalId, ex);
            throw new TerminalOffLineException(ex);
        }
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.UPGRADE_TERMINAL_SYSTEM.getName(), upgradeMsg);
        try {
            final BaseMessage responseMsg = sender.syncRequest(message);
            upgradeResponseMsgHandler.handle(terminalId, responseMsg);
        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_MESSAGE_SEND_FAIL, e);
        }
    }

    @Override
    public boolean hasSystemUpgradeInProgress() {
        return hasUpgradingTask(null);
    }

    @Override
    public boolean hasSystemUpgradeInProgress(UUID upgradePackageId) {
        Assert.notNull(upgradePackageId, "upgradePackageId can not be null");
        return hasUpgradingTask(upgradePackageId);
    }

    private boolean hasUpgradingTask(UUID upgradePackageId) {
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING, CbbSystemUpgradeTaskStateEnums.CLOSING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList = null;
        if (upgradePackageId == null) {
            upgradingTaskList = terminalSystemUpgradeDAO.findByStateInOrderByCreateTimeAsc(stateList);
        } else {
            upgradingTaskList = terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackageId, stateList);
        }

        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("不存在升级中的刷机任务");
            return false;
        }
        LOGGER.info("存在升级中的刷机任务");
        return true;
    }

    @Override
    public void modifySystemUpgradeState(TerminalSystemUpgradeEntity upgradeTask) throws BusinessException {
        Assert.notNull(upgradeTask, "upgradeTask 不能为空");
        final UUID upgradeTaskId = upgradeTask.getId();
        Assert.notNull(upgradeTaskId, "upgradeTaskId 不能为空");
        final CbbSystemUpgradeTaskStateEnums state = upgradeTask.getState();
        Assert.notNull(state, "state 不能为空");

        TerminalSystemUpgradeEntity systemUpgradeEntity = getSystemUpgradeTask(upgradeTaskId);
        systemUpgradeEntity.setState(state);
        terminalSystemUpgradeDAO.save(systemUpgradeEntity);

        if (state == CbbSystemUpgradeTaskStateEnums.CLOSING) {
            // 刷机关闭则清理服务端文件
            upgradeFileClearHandler.clear(systemUpgradeEntity.getUpgradePackageId());
        }
    }

    @Override
    public TerminalSystemUpgradeEntity getSystemUpgradeTask(UUID systemUpgradeId) throws BusinessException {
        Assert.notNull(systemUpgradeId, "systemUpgradeId can not be null");

        Optional<TerminalSystemUpgradeEntity> systemUpgradeOpt = terminalSystemUpgradeDAO.findById(systemUpgradeId);
        if (!systemUpgradeOpt.isPresent()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST);
        }

        return systemUpgradeOpt.get();
    }

}
