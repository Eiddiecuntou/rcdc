package com.ruijie.rcos.rcdc.terminal.module.def.enums;

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
     * linux_vdi
     */
    VDI_LINUX("VDI", "Linux"),

    /**
     * 安卓vdi
     */
    VDI_ANDROID("VDI", "Android"),

    /**
     * windows_vdi
     */
    VDI_WINDOWS("VDI", "Windows"),

    /**
     * linux_idv
     */
    IDV_LINUX("IDV", "Linux"),

    /**
     * windows软终端
     */
    APP_WINDOWS("APP", "Windows"),

    /**
     * 安卓移动终端
     */
    APP_ANDROID("APP", "Android"),

    /**
     * 苹果系统软终端
     */
    APP_MACOS("APP", "macOS"),

    /**
     * 苹果手机移动终端
     */
    APP_IOS("APP", "iOS"),

    /**
     * linux软终端
     */
    APP_LINUX("APP", "Linux");

    private String platform;

    private String osType;

    CbbTerminalTypeEnums(String platform, String osType) {
        this.platform = platform;
        this.osType = osType;
    }

    /**
     * 获取终端对应类型
     *
     * @param platform 平台类型
     * @param osType 操作系统类型
     * @return 终端类型枚举对象
     */
    public static CbbTerminalTypeEnums convert(String platform, String osType) {
        Assert.hasText(platform, "platform can not be blank");
        Assert.hasText(osType, "osType can not be blank");

        for (CbbTerminalTypeEnums type : CbbTerminalTypeEnums.values()) {
            if (platform.equals(type.platform) && osType.equals(type.osType)) {
                return type;
            }
        }

        throw new IllegalArgumentException("终端类型【" + platform + "】【" + osType + "】未定义，不支持该类型的终端");
    }

    public String getPlatform() {
        return platform;
    }

    public String getOsType() {
        return osType;
    }
}
