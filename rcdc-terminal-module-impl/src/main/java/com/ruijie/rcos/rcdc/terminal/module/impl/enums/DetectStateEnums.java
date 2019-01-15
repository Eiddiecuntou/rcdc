package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

/**
 * 
 * Description: 终端检测状态枚举
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月26日
 * 
 * @author nt
 */
public enum DetectStateEnums {

    CHECKING("checking"),
    
    SUCCESS("success"),
    
    ERROR("error");
    
    private String name;
    
    DetectStateEnums(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}
