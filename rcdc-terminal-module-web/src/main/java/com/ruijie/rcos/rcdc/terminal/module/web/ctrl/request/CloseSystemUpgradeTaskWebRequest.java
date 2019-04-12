package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 关闭刷机任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月16日
 * 
 * @author nt
 */
public class CloseSystemUpgradeTaskWebRequest implements WebRequest {

    @NotNull
    private UUID upgradeTaskId;

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

}
