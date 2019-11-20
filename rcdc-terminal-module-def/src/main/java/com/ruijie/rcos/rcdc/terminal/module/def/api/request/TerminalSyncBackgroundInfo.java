package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time:  2019/11/18
 *
 * @author songxiang
 */
public class TerminalSyncBackgroundInfo {

    @NotNull
    private Boolean isNeedSync;

    @NotNull
    private String imagePath;

    public Boolean getIsNeedSync() {
        return isNeedSync;
    }

    public void setIsNeedSync(Boolean isNeedSync) {
        this.isNeedSync = isNeedSync;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
