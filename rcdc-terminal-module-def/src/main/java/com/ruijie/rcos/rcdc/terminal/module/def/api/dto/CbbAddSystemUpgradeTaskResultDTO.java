package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import java.util.UUID;

/**
 * 
 * Description: 添加系统刷机任务响应
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月15日
 * 
 * @author nt
 */
public class CbbAddSystemUpgradeTaskResultDTO {

    private UUID upgradeTaskId;

    private String imgName;

    public UUID getUpgradeTaskId() {
        return upgradeTaskId;
    }

    public void setUpgradeTaskId(UUID upgradeTaskId) {
        this.upgradeTaskId = upgradeTaskId;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

}
