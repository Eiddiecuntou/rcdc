package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.Range;

/**
 * 
 * Description: 校验是否允许上传升级包
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月24日
 * 
 * @author nt
 */
public class CbbCheckAllowUploadPackageRequest {

    @NotNull
    @Range(min = "1")
    private Long fileSize;

    @NotNull
    private CbbTerminalTypeEnums terminalType;

    public CbbCheckAllowUploadPackageRequest(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public CbbTerminalTypeEnums getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(CbbTerminalTypeEnums terminalType) {
        this.terminalType = terminalType;
    }
}
