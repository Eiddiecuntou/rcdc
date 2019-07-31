package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import org.springframework.util.Assert;

/**
 * Description: 同步Logo请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月18日
 * 
 * @author huangsen
 */
public class SyncTerminalLogoRequest {

    private String logoName;

    public SyncTerminalLogoRequest(String logoName) {
        Assert.notNull(logoName, "logoName can not be null");
        this.logoName = logoName;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }
}
