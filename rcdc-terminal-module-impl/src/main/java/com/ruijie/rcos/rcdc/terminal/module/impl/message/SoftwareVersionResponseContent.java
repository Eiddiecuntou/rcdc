package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/4/16
 *
 * @author nt
 */
public class SoftwareVersionResponseContent {

    private String softwareVersion;

    public SoftwareVersionResponseContent(String softwareVersion) {
        Assert.hasText(softwareVersion, "softwareVersion can not be null");
        this.softwareVersion = softwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

}
