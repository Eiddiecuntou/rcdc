package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import org.springframework.util.Assert;

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
     * ota
     */
    OTA("ota"),
    
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
    
    public static boolean contains(String name) {
        Assert.hasText(name, "name can not be null");
        
        for(CbbTerminalTypeEnums type : values()) {
            if(type.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
}
