package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 关闭刷机任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月16日
 * 
 * @author nt
 */
// FIXME nieting 修改使用SkyEngine的IdRequest，删除此类`
public class CbbCloseSystemUpgradeTaskRequest implements Request {

    @NotNull
    private UUID upgradeTaskId;

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }
}
