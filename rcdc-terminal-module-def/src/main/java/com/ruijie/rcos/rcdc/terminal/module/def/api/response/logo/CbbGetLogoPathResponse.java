package com.ruijie.rcos.rcdc.terminal.module.def.api.response.logo;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 获取终端Logo路径响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月8日
 * 
 * @author huangsen
 */
public class CbbGetLogoPathResponse extends DefaultResponse {

    private String logoPath;

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
