package com.ruijie.rcos.rcdc.terminal.module.def.enums;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/18 11:04
 *
 * @author TING
 */
public enum CbbCpuArchType {

    X86_64("x86_64"),

    ARM("aarch64"),

    MIPS("mips"),

    OTHER("other");

    private String archName;

    CbbCpuArchType(String archName) {
        this.archName = archName;
    }

    public String getArchName() {
        return archName;
    }


    /**
     *  枚举类型转换
     *
     * @param archName 架构名称
     * @return 枚举对象
     */
    public static CbbCpuArchType convert(String archName) {
        Assert.hasText(archName, "archName can not be blank");

        for (CbbCpuArchType arch :  CbbCpuArchType.values()) {
            if (arch.getArchName().equals(archName.trim())) {
                return arch;
            }
        }

        return OTHER;
    }

}
