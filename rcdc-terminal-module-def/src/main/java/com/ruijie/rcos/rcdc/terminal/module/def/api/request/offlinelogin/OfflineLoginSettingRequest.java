package com.ruijie.rcos.rcdc.terminal.module.def.api.request.offlinelogin;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.Range;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 11:25
 *
 * @author conghaifeng
 */
public class OfflineLoginSettingRequest {

    @NotNull
    @Range(min = "-1")
    private Integer offlineAutoLocked;

    public OfflineLoginSettingRequest(Integer offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }

    public Integer getOfflineAutoLocked() {
        return offlineAutoLocked;
    }

    public void setOfflineAutoLocked(Integer offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }
}
