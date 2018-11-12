package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description: Shine应答的消息对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/12
 *
 * @author Jarman
 */
public class ShineMessageResponse<T> {

    /**
     * 0成功，1失败
     */
    private Integer errorCode;

    private T data;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
