package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import org.springframework.util.Assert;

/**
 * Description: 终端无线网络接入模式
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/29
 *
 * @author nt
 */
public enum CbbTerminalWirelessAuthModeEnums {

    MODE_WPA_EAP("eap"),

    MODE_WPA_PSK("wpa"),

    MODE_OPEN("open"),

    MODE_WEP("wep");

    private String name;

    CbbTerminalWirelessAuthModeEnums(String name) {
        Assert.hasText(name, "name can not be blank");
        this.name = name;
    }

    /**
     *  枚举转换
     * @param name 名称
     * @return 枚举对象
     */
    public static CbbTerminalWirelessAuthModeEnums convert(String name) {
        Assert.hasText(name, "name can not be blank");

        for (CbbTerminalWirelessAuthModeEnums value : CbbTerminalWirelessAuthModeEnums.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        throw new IllegalArgumentException("无线网络接入模式【" + name + "】未定义，不支持该接入模式");
    }

    public String getName() {
        return name;
    }
}
