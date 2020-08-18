package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.annotation.Range;
import org.springframework.util.Assert;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 11:25
 *
 * @author conghaifeng
 */
public class CbbOfflineLoginSettingDTO {

    @NotNull
    @Range(min = "-1")
    private Integer offlineAutoLocked;

    public CbbOfflineLoginSettingDTO(Integer offlineAutoLocked) {
        Assert.notNull(offlineAutoLocked, "offlineAutoLocked can not be null");

        this.offlineAutoLocked = offlineAutoLocked;
    }

    public Integer getOfflineAutoLocked() {
        return offlineAutoLocked;
    }

    public void setOfflineAutoLocked(Integer offlineAutoLocked) {
        this.offlineAutoLocked = offlineAutoLocked;
    }
}
