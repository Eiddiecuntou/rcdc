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

    CHECKING("检测中"),
    
    SUCCESS("检测成功"),
    
    ERROR("检测失败");
    
    private String name;
    
    DetectStateEnums(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}
