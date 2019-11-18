package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.util.HashSet;
import java.util.Set;

import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/12
 *
 * @author nt
 */
public class SystemUpgradeGlobal {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUpgradeGlobal.class);

    /**
     * 最大同时刷机数
     */
    private static final int UPGRADING_MAX_NUM = 60;

    /**
     * 升级中的终端
     */
    private static final Set<String> UPGRADING_TERMINAL_SET = new HashSet<>();

    /**
     * 判断是否允许升级，允许则占据升级名额
     * 
     * @param terminalId 终端id
     * @return 是否开始升级
     */
    public static boolean checkAndHoldUpgradeQuota(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");

        synchronized (UPGRADING_TERMINAL_SET) {
            boolean isExist = UPGRADING_TERMINAL_SET.contains(terminalId);
            if (isExist) {
                return true;
            }

            if (UPGRADING_TERMINAL_SET.size() < UPGRADING_MAX_NUM) {
                UPGRADING_TERMINAL_SET.add(terminalId);
                return true;
            }
            return false;
        }
    }

    /**
     * 释放升级名额
     * 
     * @param terminalId 终端id
     */
    public static void releaseUpgradeQuota(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be blank");

        UPGRADING_TERMINAL_SET.remove(terminalId);
        LOGGER.info("系统升级限制数量：{}", (UPGRADING_MAX_NUM - UPGRADING_TERMINAL_SET.size()));
    }


    /**
     *  判断正在升级中的终端数量是否超出限制
     * 
     * @return 是否超出限制
     */
    public static boolean isUpgradingNumExceedLimit() {
        return UPGRADING_TERMINAL_SET.size() >= UPGRADING_MAX_NUM;
    }
}
