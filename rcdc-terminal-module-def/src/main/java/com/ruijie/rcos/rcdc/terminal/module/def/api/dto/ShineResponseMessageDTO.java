package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/25
 *
 * @param <T> 应答的业务消息
 * @author Jarman
 */
public class ShineResponseMessageDTO<T> {

    private Integer errorCode;

    private String errorMsg;

    private T content;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
