package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import org.springframework.util.Assert;

/**
 * 
 * Description: 检测列表请求时间枚举
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月27日
 * 
 * @author nt
 */
public enum CbbDetectDateEnums {

    TODAY,

    YESTERDAY;

    /**
     * 判断日期字符串是否为枚举成员
     * @param date
     * @return
     */
    public static boolean contains(String date) {
        Assert.hasText(date, "platform can not be empty");
        
        for (CbbDetectDateEnums dateEnums : CbbDetectDateEnums.values()) {
            if (dateEnums.name().equals(date)) {
                return true;
            }
        }
        return false;
    }
    
}
