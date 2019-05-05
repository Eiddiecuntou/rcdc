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

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * 构建消息对象
     * @param softwareVersion 平台系统版本信息
     * @return 返回SoftwareVersionResponseContent
     */
    public static SoftwareVersionResponseContent build(String softwareVersion) {
        Assert.hasText(softwareVersion, "softwareVersion can not be null");

        SoftwareVersionResponseContent response = new SoftwareVersionResponseContent();
        response.setSoftwareVersion(softwareVersion);
        return response;
    }

}
