package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import org.springframework.util.Assert;

/**
 * Description: 终端操作系统枚举
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/19
 *
 * @author nting
 */
public enum CbbTerminalOsTypeEnums {

    WINDOWS,

    LINUX,

    ANDROID,

    UOS,

    NEOKYLIN,

    OTHER;

    /**
     * 获取终端对应类型
     *
     * @param platform 平台类型
     * @return 终端类型枚举对象
     */
    public static CbbTerminalOsTypeEnums convert(String platform) {
        Assert.hasText(platform, "platform can not be blank");

        for (CbbTerminalOsTypeEnums type : CbbTerminalOsTypeEnums.values()) {
            if (type.toString().equals(platform)) {
                return type;
            }
        }
        return CbbTerminalOsTypeEnums.OTHER;
    }

}
