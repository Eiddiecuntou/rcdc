package com.ruijie.rcos.rcdc.terminal.module.def.api.request.offlinelogin;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbOfflineAutoLockedEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 11:25
 *
 * @author conghaifeng
 */
public class OfflineLoginSettingRequest implements Request {

    @NotNull
    private CbbOfflineAutoLockedEnums offlineAutoLocked;

    public OfflineLoginSettingRequest(CbbOfflineAutoLockedEnums offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }

    public CbbOfflineAutoLockedEnums getOfflineAutoLocked() {
        return offlineAutoLocked;
    }

    public void setOfflineAutoLocked(CbbOfflineAutoLockedEnums offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }
}
