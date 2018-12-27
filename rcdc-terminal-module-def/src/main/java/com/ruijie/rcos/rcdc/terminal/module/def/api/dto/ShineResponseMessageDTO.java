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

    private Integer code;

    private T content;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
