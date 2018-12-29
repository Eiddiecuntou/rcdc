package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description: 修改终端秘密报文
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/26
 *
 * @author Jarman
 */
public class ChangeTerminalPasswordRequest {

    private String password;

    public ChangeTerminalPasswordRequest() {
    }

    public ChangeTerminalPasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
