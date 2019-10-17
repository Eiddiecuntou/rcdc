package com.ruijie.rcos.rcdc.terminal.module.def.enums;

import org.springframework.util.Assert;

/**
 * Description: 终端类型枚举
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/4
 *
 * @author Jarman
 */
public enum CbbTerminalPlatformEnums {

    /**
     * VDI
     */
    VDI,

    /**
     * IDV
     */
    IDV,

    /**
     * 软终端
     */
    APP;

    /**
     * 判断平台字符串是否为平台枚举成员
     * 
     * @param platform 终端平台类型
     * @return 是否为枚举成员
     */
    public static boolean isPlatform(String platform) {
        Assert.hasText(platform, "platform can not be empty");

        for (CbbTerminalPlatformEnums platformEnum : CbbTerminalPlatformEnums.values()) {
            if (platformEnum.name().equals(platform)) {
                return true;
            }
        }
        return false;
    }

}
