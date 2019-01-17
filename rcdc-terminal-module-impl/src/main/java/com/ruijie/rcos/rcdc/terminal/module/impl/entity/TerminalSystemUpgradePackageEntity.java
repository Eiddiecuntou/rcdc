package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;

/**
 * Description: 终端系统升级包实体类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
@Entity
@Table(name = "t_cbb_sys_upgrade_package")
public class TerminalSystemUpgradePackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     *镜像名称
     */
    private String ImgName;


    /**
     * 包类型，VDI ISO、IDV ISO、OTA
     */
    @Enumerated(EnumType.STRING)
    private TerminalPlatformEnums packageType;

    /**
     * 上传时间
     */
    private Date uploadTime;
    
    /**
     * 升级包版本号
     */
    private String packageVersion;
    
    
    @Version
    private int version;


    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }


    public String getImgName() {
        return ImgName;
    }


    public void setImgName(String imgName) {
        ImgName = imgName;
    }


    public TerminalPlatformEnums getPackageType() {
        return packageType;
    }


    public void setPackageType(TerminalPlatformEnums packageType) {
        this.packageType = packageType;
    }


    public Date getUploadTime() {
        return uploadTime;
    }


    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }


    public String getPackageVersion() {
        return packageVersion;
    }


    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }


    public int getVersion() {
        return version;
    }


    public void setVersion(int version) {
        this.version = version;
    }

}