package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbOfflineAutoLockedEnums;

/**
 * Description: 修改离线登录配置报文
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 15:59
 *
 * @author conghaifeng
 */
public class ChangeOfflineLoginConfigRequest {

    private CbbOfflineAutoLockedEnums offlineAutoLocked;

    public ChangeOfflineLoginConfigRequest(CbbOfflineAutoLockedEnums offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }

    public CbbOfflineAutoLockedEnums getOfflineAutoLocked() {
        return offlineAutoLocked;
    }

    public void setOfflineAutoLocked(CbbOfflineAutoLockedEnums offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }
}
