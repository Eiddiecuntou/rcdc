package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

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

    @NotNull
    private JSONObject customData;

    public CbbTerminalUpgradePackageUploadRequest() {
        
    }

    public CbbTerminalUpgradePackageUploadRequest(String filePath, String fileName, String fileMD5) {
        Assert.hasText(filePath, "filePath can not be blank");
        Assert.hasText(fileName, "fileName can not be blank");
        Assert.hasText(fileMD5, "fileMD5 can not be blank");

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

    public JSONObject getCustomData() {
        return customData;
    }

    public void setCustomData(JSONObject customData) {
        this.customData = customData;
    }
}
