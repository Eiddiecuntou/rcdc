package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.Range;

import javax.annotation.Nullable;

/**
 * Description: 校验是否允许上传升级包
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月24日
 *
 * @author nt
 */
public class CbbCheckAllowUploadPackageDTO {

    @NotNull
    @Range(min = "1")
    private Long fileSize;

    @Nullable
    private String fileName;

    @NotNull
    private CbbTerminalTypeEnums terminalType;

    public CbbCheckAllowUploadPackageDTO(Long fileSize) {
        this.fileSize = fileSize;
    }

    public CbbCheckAllowUploadPackageDTO(String fileName, Long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
