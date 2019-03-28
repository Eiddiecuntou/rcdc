package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 开启等待中的刷机终端处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeStartWaitingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeStartWaitingHandler.class);

    /**
     * 最大同时刷机数
     */
    private static final int UPGRADING_MAX_NUM = 50;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeService systemUpgradeService;
    
    @Autowired
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Autowired
    private TerminalSystemUpgradePackageService systemUpgradePackageService;

    /**
     * 执行处理开始等待中的刷机终端
     * 
     * @param upgradeTerminalList 刷机终端列表
     * @param upgradePackageId 刷机包id
     */
    public void execute(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList, UUID upgradePackageId) {
        Assert.notNull(upgradeTerminalList, "upgradeTerminalList can not be null");
        Assert.notNull(upgradePackageId, "upgradePackageId can not be null");

        try {
            startWaiting(upgradeTerminalList, upgradePackageId);
        } catch (BusinessException e) {
            LOGGER.error("处理刷机终端状态同步失败", e);
        }
    }

    /**
     * 开始等待中的刷机终端
     * 
     * @param upgradeTerminalList 刷机终端列表
     * @param upgradePackageId 刷机包id
     * @throws BusinessException 业务异常
     */
    private void startWaiting(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList, UUID upgradePackageId)
            throws BusinessException {
        LOGGER.info("开始等待中的终端刷机处理...");
        int count = 0;
        final TerminalSystemUpgradePackageEntity upgradePackage =
                systemUpgradePackageService.getSystemUpgradePackage(upgradePackageId);

        // 获取正在刷机中的终端数量
        int upgradingNum = systemUpgradeTerminalDAO.countByState(CbbSystemUpgradeStateEnums.UPGRADING);

        // 构造刷机指令信息
        TerminalSystemUpgradeMsg upgradeMsg =
                new TerminalSystemUpgradeMsg(upgradePackage.getImgName(), upgradePackage.getPackageVersion());
        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : upgradeTerminalList) {
            if (startTerminalUpgrade(upgradingNum, upgradeTerminal, upgradeMsg)) {
                upgradingNum++;
                count++;
            }
        }

        LOGGER.info("完成开始等待中的终端刷机处理，处理开始等待中的终端{}台", count);
    }

    private boolean startTerminalUpgrade(int upgradingNum, TerminalSystemUpgradeTerminalEntity upgradeTerminal,
            TerminalSystemUpgradeMsg upgradeMsg) throws BusinessException {
        if (upgradingNum >= UPGRADING_MAX_NUM) {
            return false;
        }

        if (upgradeTerminal.getState() != CbbSystemUpgradeStateEnums.WAIT) {
            return false;
        }

        // 下发系统刷机指令
        String terminalId = upgradeTerminal.getTerminalId();
        try {
            LOGGER.info("开始向终端[{}]发送刷机指令: {}", terminalId, upgradeMsg.toString());
            systemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            LOGGER.info("向终端[{}]发送刷机指令成功", terminalId);
        } catch (TerminalOffLineException te) {
            LOGGER.info("终端[ " + terminalId + "]离线", te);
            setTerminalUpgradeFail(upgradeTerminal);
            return true;
        } catch (BusinessException e) {
            LOGGER.info("向终端[ " + terminalId + "]发送刷机指令失败", e);
            return false;
        }
        
        modifyTerminalUpgrading(upgradeTerminal);
        return true;
    }
    
    private void setTerminalUpgradeFail(TerminalSystemUpgradeTerminalEntity upgradeTerminal) throws BusinessException {
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.FAIL);
        systemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
    }

    private void modifyTerminalUpgrading(TerminalSystemUpgradeTerminalEntity upgradeTerminal) throws BusinessException {
        // 更新开始刷机时间
        systemUpgradeServiceTx.startTerminalUpgrade(upgradeTerminal);
    }

}
