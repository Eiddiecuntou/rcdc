package com.ruijie.rcos.rcdc.terminal.module.def.api.response.offlinelogin;

import org.springframework.util.Assert;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/2 14:30
 *
 * @author conghaifeng
 */
public class OfflineLoginSettingResponse {

    private String offlineAutoLocked;

    public OfflineLoginSettingResponse(String offlineAutoLocked) {
        Assert.notNull(offlineAutoLocked, "offlineAutoLocked can not be null");
        this.offlineAutoLocked = offlineAutoLocked;
    }

    public String getOfflineAutoLocked() {
        return offlineAutoLocked;
    }

    public void setOfflineAutoLocked(String offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }
}
