package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import org.springframework.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 
 * Description: 终端升级包名称响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CbbUpgradePackageNameResponse extends DefaultResponse {

    private String packageName;
    
    public CbbUpgradePackageNameResponse(String packageName) {
        Assert.hasText(packageName, "packageName can not be empty");
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
}
