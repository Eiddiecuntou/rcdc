package com.ruijie.rcos.rcdc.terminal.module.def.enums;

/**
 * Description: VOI终端启动方式
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/5
 *
 * @author jarman
 */
public enum CbbTerminalStartMode {

    AUTO("auto"),

    TC("tc"),

    UEFI("uefi");

    CbbTerminalStartMode(String mode) {
        this.mode = mode;
    }

    String mode;

    public String getMode() {
        return mode;
    }
}
