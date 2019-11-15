package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public enum CheckSystemUpgradeResultEnums {

    NOT_NEED_UPGRADE(0),

    NEED_UPGRADE(1),

    UNSUPPORT(2);

    private int result;

    CheckSystemUpgradeResultEnums(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
