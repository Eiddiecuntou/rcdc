package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * Description: 终端状态
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/15
 *
 * @author nt
 */
public enum CbbTerminalComponentUpgradeResultEnums {

    /**
     * 不升级
     */
    NOT(0),

    /**
     * 不支持升级
     */
    NOT_SUPPORT(1),

    /**
     * 开始升级
     */
    START(2),

    /**
     * 升级未就绪
     */
    PREPARING(3);

    private int result;

    CbbTerminalComponentUpgradeResultEnums(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

}
