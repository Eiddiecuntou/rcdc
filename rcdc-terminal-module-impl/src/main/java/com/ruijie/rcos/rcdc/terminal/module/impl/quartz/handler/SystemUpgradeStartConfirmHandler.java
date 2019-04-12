package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 刷机中终端超时处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeStartConfirmHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeStateSynctHandler.class);

    private static final int START_CONFIRM_TIME_OUT = 5 * 60;

    @Autowired
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    /**
     * 开启处理刷机终端状态同步
     * 
     * @param upgradeTerminalList 刷机终端列表
     */
    public void execute(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) {
        Assert.notNull(upgradeTerminalList, "upgradeTerminalList can not be null");

        try {
            startConfirm(upgradeTerminalList);
        } catch (Exception e) {
            LOGGER.error("处理刷机终端状态同步异常", e);
        }
    }

    /**
     * 同步终端系统升级状态
     * 
     * @param upgradeTerminalList 刷机终端列表
     * @throws BusinessException 业务异常
     */
    private void startConfirm(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) throws BusinessException {
        LOGGER.info("开启刷机终端开始刷机状态同步处理...");
        List<TerminalSystemUpgradeTerminalEntity> terminalList = new ArrayList<>();
        terminalList.addAll(upgradeTerminalList);
        int count = 0;
        // 获取文件系统中的正在升级中的终端信息
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList = systemUpgradePackageService.readSystemUpgradeStartStateFromFile();
        if (systemUpgradeInfoList == null) {
            systemUpgradeInfoList = new ArrayList<>();
        }

        for (Iterator<TerminalSystemUpgradeTerminalEntity> iterator = terminalList.iterator(); iterator.hasNext();) {
            TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = (TerminalSystemUpgradeTerminalEntity) iterator.next();
            if (upgradeTerminalEntity.getState() != CbbSystemUpgradeStateEnums.UPGRADING) {
                LOGGER.debug("非进行中的刷机终端不做开始刷机状态判断");
                iterator.remove();
                continue;
            }
            // 查找匹配的终端
            String terminalId = upgradeTerminalEntity.getTerminalId();
            for (TerminalSystemUpgradeInfo upgradeInfo : systemUpgradeInfoList) {
                if (terminalId.equals(upgradeInfo.getTerminalId())) {
                    LOGGER.debug("刷机终端[{}]查找到匹配的正在升级中状态信息", terminalId);
                    iterator.remove();
                    break;
                }
            }
        }

        for (TerminalSystemUpgradeTerminalEntity noStartInfoTerminal : terminalList) {
            final boolean isTimeout = TerminalDateUtil.isTimeout(noStartInfoTerminal.getStartTime(), START_CONFIRM_TIME_OUT);
            if (isTimeout) {
                noStartInfoTerminal.setState(CbbSystemUpgradeStateEnums.FAIL);
                systemUpgradeServiceTx.modifySystemUpgradeTerminalState(noStartInfoTerminal);
                count++;
            }
        }

        LOGGER.info("完成刷机终端开始刷机状态同步，处理开始刷机超时终端{}台", count);
    }

}
