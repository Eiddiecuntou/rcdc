package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月18日
 * 
 * @author nt
 */
public class MatchEqual {
    @NotNull
    private String name;

    @NotNull
    private Object[] valueArr;

    public MatchEqual() {

    }

    public MatchEqual(String name, Object[] valueArr) {
        this.name = name;
        this.valueArr = valueArr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getValueArr() {
        return valueArr;
    }

    public void setValueArr(Object[] valueArr) {
        this.valueArr = valueArr;
    }
}
