package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import java.util.UUID;

/**
 * Description: 开启OTA升级任务请求参数
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/22
 *
 * @author hs
 */
public class CbbStartOtaUpgradeTaskRequest implements Request {

    @NotNull
    private UUID packageId;

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }
}
