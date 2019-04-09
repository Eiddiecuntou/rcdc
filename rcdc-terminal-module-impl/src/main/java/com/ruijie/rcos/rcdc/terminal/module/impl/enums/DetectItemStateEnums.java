package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

/**
 * 
 * Description: 检测项可用状态枚举
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月29日
 * 
 * @author nt
 */
public enum DetectItemStateEnums {

    TRUE(1),

    FALSE(0);

    private int state;

    DetectItemStateEnums(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
