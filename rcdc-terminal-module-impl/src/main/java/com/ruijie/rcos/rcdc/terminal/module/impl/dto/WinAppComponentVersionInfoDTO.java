package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

/**
 * 
 * Description: windows软终端组件升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月21日
 * 
 * @author nt
 */
public class WinAppComponentVersionInfoDTO extends BaseComponentVersionInfoDTO {

    /**
     * 组件包文件名
     */
    private String completePackageName;

    /**
     * 下载路径
     */
    private String completePackageUrl;

    public String getCompletePackageUrl() {
        return completePackageUrl;
    }

    public void setCompletePackageUrl(String completePackageUrl) {
        this.completePackageUrl = completePackageUrl;
    }

    public String getCompletePackageName() {
        return completePackageName;
    }

    public void setCompletePackageName(String completePackageName) {
        this.completePackageName = completePackageName;
    }
}
