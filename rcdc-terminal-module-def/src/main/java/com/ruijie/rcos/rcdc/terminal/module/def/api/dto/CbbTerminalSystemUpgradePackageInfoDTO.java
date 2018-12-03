package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * Description: 终端系统升级包信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author "nt"
 */
public class CbbTerminalSystemUpgradePackageInfoDTO {
    
    private UUID id;

    /**
     * 刷机包名称
     */
    private String name;

    /**
     * 存储路径
     */
    private String storePath;

    /**
     * 包类型，VDI ISO、IDV ISO、OTA
     */
    private String packageType;

    /**
     * 上传时间
     */
    private Date uploadTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
    
}
