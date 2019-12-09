package com.ruijie.rcos.rcdc.terminal.module.impl.spi.response;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 16:45
 *
 * @author conghaifeng
 */
public class OfflineLoginConfig {

    private Integer offlineAutoLocked;

    public OfflineLoginConfig(Integer offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }

    public Integer getOfflineAutoLocked() {
        return offlineAutoLocked;
    }

    public void setOfflineAutoLocked(Integer offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }
}
