package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * 
 * Description: 终端类型
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月1日
 * 
 * @author nt
 */
public enum CbbTerminalTypeEnums {

    /**
     * vdi
     */
    VDI("vdi"),
    
    /**
     * idv
     */
    IDV("idv"),
    
    /**
     * 所有
     */
    ALL("all");
    
    private String name;
    
    CbbTerminalTypeEnums(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}
