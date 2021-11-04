package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.List;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月24日
 * 
 * @author nt
 */
public class CbbCheckAllowUploadPackageResultDTO {

    /**
     * 是否允许上传
     */
    private Boolean allowUpload = true;

    private List<String> errorList;

    public Boolean getAllowUpload() {
        return allowUpload;
    }

    public void setAllowUpload(Boolean allowUpload) {
        this.allowUpload = allowUpload;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

}
