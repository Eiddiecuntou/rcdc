package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @param <T> 泛型参数
 * @author nt
 */
public class CbbItemArrResponse<T> extends DefaultResponse {

    private T[] itemArr;

    public CbbItemArrResponse() {
    }

    public CbbItemArrResponse(T[] itemArr) {
        Assert.notNull(itemArr, "itemArr can not be null");
        this.itemArr = itemArr;
    }

    public T[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(T[] itemArr) {
        this.itemArr = itemArr;
    }
}
