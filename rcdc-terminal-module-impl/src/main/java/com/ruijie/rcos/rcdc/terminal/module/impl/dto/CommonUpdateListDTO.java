package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

/**
 * Description: 通用升级updatelistDTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/7
 *
 * @author nt
 */
public class CommonUpdateListDTO extends BaseUpdateListDTO<CommonComponentVersionInfoDTO> {

    private String baseVersion;

    private String osLimit;

    /**
     * 组件升级包目录名
     */
    private String componentPackageDirName;

    /**
     * 组件升级包目录种子路径
     */
    private String componentPackageDirTorrentUrl;

    /**
     * 组件升级包目录种子md5
     */
    private String componentPackageDirTorrentMd5;


    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getOsLimit() {
        return osLimit;
    }

    public void setOsLimit(String osLimit) {
        this.osLimit = osLimit;
    }

    public String getComponentPackageDirName() {
        return componentPackageDirName;
    }

    public void setComponentPackageDirName(String componentPackageDirName) {
        this.componentPackageDirName = componentPackageDirName;
    }

    public String getComponentPackageDirTorrentUrl() {
        return componentPackageDirTorrentUrl;
    }

    public void setComponentPackageDirTorrentUrl(String componentPackageDirTorrentUrl) {
        this.componentPackageDirTorrentUrl = componentPackageDirTorrentUrl;
    }

    public String getComponentPackageDirTorrentMd5() {
        return componentPackageDirTorrentMd5;
    }

    public void setComponentPackageDirTorrentMd5(String componentPackageDirTorrentMd5) {
        this.componentPackageDirTorrentMd5 = componentPackageDirTorrentMd5;
    }

}
