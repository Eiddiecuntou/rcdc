package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.response;

import com.ruijie.rcos.sk.webmvc.api.response.WebResponse;

/**
 * 
 * Description: 校验是否允许上传终端升级包
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月24日
 * 
 * @author nt
 */
public class CheckAllowUploadUpgradePackageWebResponse implements WebResponse {
    
    private Status status;

    private String message;

    private Object content;
    
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String[] getMsgArgArr() {
        // no use
        return null;
    }

    @Override
    public String getMsgKey() {
        // no use
        return null;
    }


}
