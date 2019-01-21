package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;

/**
 * 
 * Description: 终端系统升级包上传请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月4日
 * 
 * @author nt
 */
public class CbbTerminalUpgradePackageUploadRequest implements Request {

    @NotBlank
    private String filePath;
    
    @NotBlank
    private String fileName;
    
    @NotBlank
    private String fileMD5;
    
    public CbbTerminalUpgradePackageUploadRequest() {
    }

    public CbbTerminalUpgradePackageUploadRequest(String filePath, String fileName, String fileMD5) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileMD5 = fileMD5;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }
    
}
