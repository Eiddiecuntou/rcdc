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
    PREPARING(3),

    /**
     *  系统版本过低,不支持升级
     */
    NOT_SUPPORT_FOR_LOWER_OS_VERSION(4),

    /**
     * 终端无授权
     */
    NO_AUTH(5),

    /**
     * 无云应用终端授权数
     */
    NO_CVA_AUTH(7),

    /**
     * 服务器环境异常
     */
    ABNORMAL(-1);

    private int result;

    CbbTerminalComponentUpgradeResultEnums(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

}
