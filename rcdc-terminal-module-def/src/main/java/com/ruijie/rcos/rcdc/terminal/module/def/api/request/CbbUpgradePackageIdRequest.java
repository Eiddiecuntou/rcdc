package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 取消终端刷机
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class CbbUpgradePackageIdRequest implements Request {

    @NotNull
    private UUID packageId;
    
    public CbbUpgradePackageIdRequest(UUID packageId) {
        Assert.notNull(packageId, "packageId can not be null");
        this.packageId = packageId;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }
    
}
