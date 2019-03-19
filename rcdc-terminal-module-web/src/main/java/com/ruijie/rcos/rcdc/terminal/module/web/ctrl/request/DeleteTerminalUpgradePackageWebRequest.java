package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 删除刷机包
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月18日
 * 
 * @author nt
 */
public class DeleteTerminalUpgradePackageWebRequest implements WebRequest {

    @NotEmpty
    private UUID[] idArr;

    public UUID[] getIdArr() {
        return idArr;
    }

    public void setIdArr(UUID[] idArr) {
        this.idArr = idArr;
    }

}
