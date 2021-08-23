package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import org.springframework.util.Assert;

/**
 * Description: 上传Logo请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月8日
 *
 * @author huangsen
 */
public class CbbUploadLogoDTO {

    @NotBlank
    private String logoPath;

    @NotBlank
    private String logoName;

    @NotBlank
    private String logoMD5;

    public CbbUploadLogoDTO() {

    }

    public CbbUploadLogoDTO(String logoPath, String logoName, String logoMD5) {
        Assert.hasText(logoPath, "logoPath can not be blank");
        Assert.hasText(logoName, "logoName can not be blank");
        Assert.hasText(logoMD5, "logoMD5 can not be blank");
        this.logoPath = logoPath;
        this.logoName = logoName;
        this.logoMD5 = logoMD5;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public String getLogoMD5() {
        return logoMD5;
    }

    public void setLogoMD5(String logoMD5) {
        this.logoMD5 = logoMD5;
    }
}
