package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import javax.persistence.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import java.util.Date;
import java.util.UUID;

/**
 * Description: 终端系统升级包实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
@Entity
@Table(name = "t_termianl_system_upgrade_package")
public class TermianlSystemUpgradePackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private CbbTerminalTypeEnums packageType;

    /**
     * 上传时间
     */
    private Date uploadTime;
    
    /**
     * 内部版本号
     */
    private String internalVersion;
    
    /**
     * 外部版本号
     */
    private String externalVersion;
    
    @Version
    private int version;

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

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

    public CbbTerminalTypeEnums getPackageType() {
        return packageType;
    }

    public void setPackageType(CbbTerminalTypeEnums packageType) {
        this.packageType = packageType;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getInternalVersion() {
        return internalVersion;
    }

    public void setInternalVersion(String internalVersion) {
        this.internalVersion = internalVersion;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }
    
    
}