package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 列表响应(无分页)
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月1日
 * 
 * @param <T> 列表数据对象
 * @author nt
 */
public class CbbBaseListResponse<T> extends DefaultResponse {
    
    /**
     * 数据数组
     */
    private T[] itemArr;
    

    public CbbBaseListResponse() {
        super();
    }

    public CbbBaseListResponse(T[] itemArr) {
        super();
        this.itemArr = itemArr;
    }

    public T[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(T[] itemArr) {
        this.itemArr = itemArr;
    }

    
}
