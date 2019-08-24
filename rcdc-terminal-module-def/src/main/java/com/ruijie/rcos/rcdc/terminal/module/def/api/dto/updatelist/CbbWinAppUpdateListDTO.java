package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist;

/**
 * 
 * Description: windows软终端组件升级版本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月14日
 * 
 * @author nt
 */
public class CbbWinAppUpdateListDTO extends CbbCommonUpdatelistDTO<CbbWinAppComponentVersionInfoDTO> {

    private String name;

    private String md5;

    private String completePackageName;

    private String completePackageUrl;

    private String platform;

    public CbbWinAppUpdateListDTO() {
    }

    public CbbWinAppUpdateListDTO(String version, Integer componentSize) {
        super(version, componentSize);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCompletePackageName() {
        return completePackageName;
    }

    public void setCompletePackageName(String completePackageName) {
        this.completePackageName = completePackageName;
    }

    public String getCompletePackageUrl() {
        return completePackageUrl;
    }

    public void setCompletePackageUrl(String completePackageUrl) {
        this.completePackageUrl = completePackageUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
