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
import com.ruijie.rcos.rcdc.terminal.module.impl.api.AsyncRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.CbbTerminalSystemUpgradeRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.SystemUpgradeFileClearHandler;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
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
    private CbbTerminalSystemUpgradeRequestCallBack callback;

    @Override
    public void systemUpgrade(String terminalId, TerminalSystemUpgradeMsg upgradeMsg) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(upgradeMsg, "systemUpgradeMsg 不能为空");

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.UPGRADE_TERMINAL_SYSTEM.getName(),
                upgradeMsg);
        sender.asyncRequest(message, new AsyncRequestCallBack(terminalId, callback));
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
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays.asList(new CbbSystemUpgradeTaskStateEnums[] {
            CbbSystemUpgradeTaskStateEnums.UPGRADING, CbbSystemUpgradeTaskStateEnums.CLOSING});
        final List<TerminalSystemUpgradeEntity> upgradingTaskList = terminalSystemUpgradeDAO
                .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackageId, stateList);
        if (CollectionUtils.isEmpty(upgradingTaskList)) {
            LOGGER.info("不存在升级中的刷机任务");
            return false;
        }
        LOGGER.info("存在升级中的刷机任务");
        return true;
    }

    @Override
    public void modifySystemUpgradeState(UUID systemUpgradeId, CbbSystemUpgradeTaskStateEnums state)
            throws BusinessException {
        Assert.notNull(systemUpgradeId, "systemUpgradeId 不能为空");
        Assert.notNull(state, "state 不能为空");

        TerminalSystemUpgradeEntity systemUpgradeEntity = getSystemUpgradeTask(systemUpgradeId);
        systemUpgradeEntity.setState(state);
        terminalSystemUpgradeDAO.save(systemUpgradeEntity);

        if (state == CbbSystemUpgradeTaskStateEnums.FINISH) {
            // 刷机完成清理服务端文件
            upgradeFileClearHandler.clear(systemUpgradeEntity.getId(), systemUpgradeEntity.getUpgradePackageId());
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
