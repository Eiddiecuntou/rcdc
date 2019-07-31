package com.ruijie.rcos.rcdc.terminal.module.def.api.response.logo;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description:终端Logo预览响应
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/8
 *
 * @author hs
 */
public class CbbPreviewLogoResponse extends DefaultResponse {

    private String logoPath;

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
