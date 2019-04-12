package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 
 * Description: 检验是否允许上传升级包响应内容
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月25日
 * 
 * @author nt
 */
public class CheckAllowUploadContentVO {

    private Boolean hasError;

    private String errorMsg;

    public CheckAllowUploadContentVO(Boolean hasError, @Nullable String errorMsg) {
        Assert.notNull(hasError, "hasError can not be null");

        this.hasError = hasError;
        this.errorMsg = errorMsg;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
