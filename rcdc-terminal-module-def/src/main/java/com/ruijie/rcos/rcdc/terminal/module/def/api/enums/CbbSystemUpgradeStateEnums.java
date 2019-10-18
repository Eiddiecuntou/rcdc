package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * Description: 终端系统升级结果状态
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/15
 *
 * @author nt
 */
public enum CbbSystemUpgradeStateEnums {

    WAIT,

    UPGRADING,

    SUCCESS,

    FAIL,

    UNDO,

    UNSUPPORTED,

    // FIXME 不用加这个状态，超时设置成失败就好
    TIMEOUT
}
