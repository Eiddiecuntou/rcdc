package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description:同步终端Logo请求
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/18
 *
 * @author hs
 */
public class SyncTerminalLogoRequest {

    private String logoName;

    public SyncTerminalLogoRequest(String logoName) {
        this.logoName = logoName;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }
}
