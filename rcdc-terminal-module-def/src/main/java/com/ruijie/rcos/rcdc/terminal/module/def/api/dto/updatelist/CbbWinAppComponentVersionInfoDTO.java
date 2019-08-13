package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

/**
 * 
 * Description: windows软终端组件升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月21日
 * 
 * @author nt
 */
public class CbbWinAppComponentVersionInfoDTO extends CbbCommonComponentVersionInfoDTO {

    /**
     * 是否完整升级包
     */
    private Boolean isComplete;

    /**
     * 组件包文件名
     */
    private String completePackageName;

    /**
     * 下载路径
     */
    private String completePackageUrl;

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

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
