package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;

/**
 * Description: 通用的应答消息体对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
public class CommonMsg {

    private StateEnums errorCode;

    private String msg;

    public StateEnums getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StateEnums errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
