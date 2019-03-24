package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.Range;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 校验是否允许上传升级包
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月24日
 * 
 * @author nt
 */
public class CbbCheckAllowUploadPackageRequest implements Request {

    @NotNull
    @Range(min = "1")
    private Long fileSize;
    
    public CbbCheckAllowUploadPackageRequest(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
}
