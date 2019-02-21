package com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler;

import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 刷机终端状态同步处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月21日
 * 
 * @author nt
 */
@Service
public class SystemUpgradeStateSynctHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeStateSynctHandler.class);

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
            syncState(upgradeTerminalList);
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
    private void syncState(List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList) throws BusinessException {
        LOGGER.info("开始刷机终端状态同步处理...");
        int count = 0;
        // 获取文件系统中的升级信息
        List<TerminalSystemUpgradeInfo> systemUpgradeInfoList =
                systemUpgradePackageService.readSystemUpgradeStateFromFile();
        if (systemUpgradeInfoList == null) {
            LOGGER.debug("无终端刷机状态信息");
            return;
        }

        for (TerminalSystemUpgradeTerminalEntity upgradeTerminal : upgradeTerminalList) {
            // 查找匹配的升级信息
            TerminalSystemUpgradeInfo matchInfo = null;
            String terminalId = upgradeTerminal.getTerminalId();
            for (Iterator<TerminalSystemUpgradeInfo> iterator = systemUpgradeInfoList.iterator(); iterator.hasNext();) {
                TerminalSystemUpgradeInfo upgradeInfo = iterator.next();
                if (terminalId.equals(upgradeInfo.getTerminalId())) {
                    LOGGER.debug("刷机终端[{}]查找到匹配状态信息", terminalId);
                    matchInfo = upgradeInfo;
                    iterator.remove();
                    break;
                }
            }
            if (matchInfo == null) {
                // 无匹配终端升级状态信息
                LOGGER.debug("刷机终端[{}]未查找到匹配状态信息", terminalId);
                continue;
            }

            // 更新刷机终端状态信息
            LOGGER.debug("更新刷机终端[{}]状态信息为：{}", terminalId, matchInfo.getState());

            systemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal.getSysUpgradeId(), terminalId,
                    matchInfo.getState());
            count++;
        }

        LOGGER.info("完成刷机终端状态同步处理，处理终端{}台", count);
    }
}
