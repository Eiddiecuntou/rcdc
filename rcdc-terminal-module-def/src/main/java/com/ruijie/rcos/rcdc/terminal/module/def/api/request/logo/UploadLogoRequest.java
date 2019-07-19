package com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/8
 *
 * @author hs
 */
public class UploadLogoRequest implements Request {

    @NotBlank
    private String logoPath;

    @NotBlank
    private String logoName;

    @NotBlank
    private String logoMD5;

    public UploadLogoRequest() {
    }

    public UploadLogoRequest(String logoPath, String logoName, String logoMD5) {
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
