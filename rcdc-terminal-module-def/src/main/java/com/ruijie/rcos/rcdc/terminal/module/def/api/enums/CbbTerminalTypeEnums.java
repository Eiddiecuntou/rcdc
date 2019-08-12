package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/14
 *
 * @author Jarman
 */
public enum CbbTerminalTypeEnums {

    /**
     * linux
     */
    LINUX("Linux"),

    /**
     *  windows软终端
     */
    WINDOWS("Windows"),

    /**
     * mac_os软终端
     */
    MAC_OS("MacOS"),

    /**
     *  android移动终端
     */
    ANDROID("Android Mobile"),

    IOS("iOS");

    private String name;

    CbbTerminalTypeEnums(String name) {
        this.name = name;
    }

    /**
     *  将字符串转换为对应类型
     *
     * @param typeName 类型名称
     * @return 终端类型枚举对象
     * @throws IllegalArgumentException 异常
     */
    public static CbbTerminalTypeEnums convert(String typeName) throws IllegalArgumentException {
        Assert.hasText(typeName, "typeName can not be blank");

        for (CbbTerminalTypeEnums type : CbbTerminalTypeEnums.values()) {
            if (typeName.equals(type.name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("软终端类型【" + typeName + "】未定义，不支持该类型的终端");
    }
}
