package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo;

import java.util.UUID;

/**
 * 
 * Description: 创建刷机任务响应内容
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月15日
 * 
 * @author nt
 */
public class CreateSystemUpgradeTaskContentVO {

    private UUID upgradeTaskId;

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

}
