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
     * 下载路径
     */
    private String url;

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
