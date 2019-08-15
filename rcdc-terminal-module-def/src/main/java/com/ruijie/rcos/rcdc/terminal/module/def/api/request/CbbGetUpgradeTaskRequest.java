package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 获取终端升级任务信息请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月19日
 * 
 * @author nt
 */
// FIXME nieting 修改使用SkyEngine的IdRequest，删除此类
public class CbbGetUpgradeTaskRequest implements Request {

    @NotNull
    private UUID upgradeTaskId;

    public CbbGetUpgradeTaskRequest(UUID upgradeTaskId) {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        this.upgradeTaskId = upgradeTaskId;
    }

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

}
