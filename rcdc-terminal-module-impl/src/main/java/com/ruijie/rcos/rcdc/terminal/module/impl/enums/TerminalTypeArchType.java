package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/20 16:40
 *
 * @author TING
 */
public enum TerminalTypeArchType {

    /**
     * linux vdi x86架构终端
     */
    LINUX_VDI_X86(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64),

    /**
     * linux vdi arm架构终端
     */
    LINUX_VDI_ARM(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.ARM),

    /**
     * adndroid vdi arm架构终端
     */
    ANDROID_VDI_ARM(CbbTerminalTypeEnums.VDI_ANDROID, CbbCpuArchType.ARM),

    /**
     * linux idv x86架构终端
     */
    LINUX_IDV_X86(CbbTerminalTypeEnums.IDV_LINUX, CbbCpuArchType.X86_64),

    /**
     * linux idv arm架构终端
     */
    LINUX_IDV_ARM(CbbTerminalTypeEnums.IDV_LINUX, CbbCpuArchType.ARM);

    TerminalTypeArchType(CbbTerminalTypeEnums terminalType, CbbCpuArchType archType) {
        this.terminalType = terminalType;
        this.archType = archType;
    }

    private CbbTerminalTypeEnums terminalType;

    private CbbCpuArchType archType;

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public CbbCpuArchType getArchType() {
        return archType;
    }

    /**
     * 枚举转换
     *
     * @return 枚举对象
     */
    public static TerminalTypeArchType convert(CbbTerminalTypeEnums terminalType, CbbCpuArchType archType) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(archType, "archType can not be null");

        for (TerminalTypeArchType type : TerminalTypeArchType.values()) {
            if (type.getTerminalType() == terminalType && type.getArchType() == archType) {
                return type;
            }
        }

        throw new IllegalArgumentException("终端类型架构【" + terminalType + "】【" + archType + "】未定义，不支持该终端类型架构");
    }

}
