package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;

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

    CHECKING(BusinessKey.RCDC_TERMINAL_DETECT_STATE_CHECKING),
    
    SUCCESS(BusinessKey.RCDC_TERMINAL_DETECT_STATE_SUCCESS),
    
    ERROR(BusinessKey.RCDC_TERMINAL_DETECT_STATE_ERROR);
    
    private String name;
    
    DetectStateEnums(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}
