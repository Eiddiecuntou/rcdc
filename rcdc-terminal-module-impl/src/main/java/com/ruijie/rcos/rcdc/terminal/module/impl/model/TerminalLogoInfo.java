package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.sk.base.util.StringUtils;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/6/15
 *
 * @author hs
 */
public class TerminalLogoInfo {

    private String logoPath = StringUtils.EMPTY;

    private String md5 = StringUtils.EMPTY;

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
