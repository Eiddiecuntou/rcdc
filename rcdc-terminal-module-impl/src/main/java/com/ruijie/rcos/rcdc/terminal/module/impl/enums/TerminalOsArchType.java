package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/18 17:22
 *
 * @author TING
 */
public enum TerminalOsArchType {

    /**
     * Linux 操作系统， x86架构
     */
    LINUX_X86(CbbTerminalOsTypeEnums.LINUX, CbbCpuArchType.X86_64),

    /**
     * Linux 操作系统， arm架构
     */
    LINUX_ARM(CbbTerminalOsTypeEnums.LINUX, CbbCpuArchType.ARM),

    /**
     * windows 操作系统， x86架构
     */
    WINDOWS_X86(CbbTerminalOsTypeEnums.WINDOWS, CbbCpuArchType.X86_64),

    /**
     * windows 操作系统， ARM架构
     */
    WINDOWS_ARM(CbbTerminalOsTypeEnums.WINDOWS, CbbCpuArchType.ARM),

    /**
     * android 操作系统， ARM架构
     */
    ANDROID_ARM(CbbTerminalOsTypeEnums.ANDROID, CbbCpuArchType.ARM),

    /**
     * android 操作系统， x86架构
     */
    ANDROID_X86(CbbTerminalOsTypeEnums.ANDROID, CbbCpuArchType.X86_64),

    /**
     * UOS 操作系统，arm架构
     */
    UOS_ARM(CbbTerminalOsTypeEnums.UOS, CbbCpuArchType.ARM),


    /**
     * UOS 操作系统，x86架构
     */
    UOS_X86(CbbTerminalOsTypeEnums.UOS, CbbCpuArchType.X86_64),

    /**
     * NEOKYLIN 操作系统， arm架构
     */
    NEOKYLIN_ARM(CbbTerminalOsTypeEnums.NEOKYLIN, CbbCpuArchType.ARM),

    /**
     * NEOKYLIN 操作系统， x86架构
     */
    NEOKYLIN_X86(CbbTerminalOsTypeEnums.NEOKYLIN, CbbCpuArchType.X86_64);

    private CbbTerminalOsTypeEnums osType;

    private CbbCpuArchType cpuArch;

    TerminalOsArchType(CbbTerminalOsTypeEnums osType, CbbCpuArchType cpuArch) {
        this.osType = osType;
        this.cpuArch = cpuArch;
    }

    public CbbTerminalOsTypeEnums getOsType() {
        return osType;
    }

    public CbbCpuArchType getCpuArch() {
        return cpuArch;
    }

    /**
     * 枚举类型转换
     *
     * @param osType  操作系统类型
     * @param cpuArch cpu架构
     * @return 终端系统架构
     */
    public static TerminalOsArchType convert(CbbTerminalOsTypeEnums osType, CbbCpuArchType cpuArch) {
        Assert.notNull(osType, "osType can not be null");
        Assert.notNull(cpuArch, "cpuArch can not be null");

        for (TerminalOsArchType osArchType : TerminalOsArchType.values()) {
            if (osType == osArchType.getOsType() && cpuArch == osArchType.getCpuArch()) {
                return osArchType;
            }
        }

        throw new IllegalArgumentException("终端系统架构【" + osType + "】【" + cpuArch + "】未定义，不支持该系统架构");
    }
}
