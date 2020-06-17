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

    private String logoPath;

    private String md5;

    public SyncTerminalLogoRequest(String logoPath, String md5) {
        Assert.notNull(logoPath, "logoPath can not be null");
        Assert.notNull(md5, "md5 can not be null");
        this.logoPath = logoPath;
        this.md5 = md5;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
