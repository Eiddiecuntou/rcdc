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
public enum TerminalPlatformEnums {

    /**
     * VDI
     */
    VDI,

    /**
     * IDV
     */
    IDV,

    /**
     * 其他终端,需要注意此类终端，给出提示信息
     */
    OTHER,
    
    /**
     * 所有
     */
    ALL;
    
    /**
     * 判断平台字符串是否为平台枚举成员
     * @param platform
     * @return
     */
    public static boolean contains(String platform) {
        Assert.hasText(platform, "platform can not be empty");
        
        for (TerminalPlatformEnums platformEnum : TerminalPlatformEnums.values()) {
            if (platformEnum.name().equals(platform)) {
                return true;
            }
        }
        return false;
    }

}
