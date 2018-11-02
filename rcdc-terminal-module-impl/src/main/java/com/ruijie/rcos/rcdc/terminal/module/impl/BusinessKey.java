package com.ruijie.rcos.rcdc.terminal.module.impl;

/**
 * Description: 国际化key全局变量接口，与国际化文件中的key对应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public interface BusinessKey {
    /**
     * 没有找到对应的终端
     */
    String RCDC_TERMINAL_NOT_FOUND_TERMINAL = "rcdc_terminal_not_found_terminal";
    /**
     * 终端断开连接，处于离线状态
     */
    String RCDC_TERMINAL_OFFLINE = "rcdc_terminal_offline";
}
