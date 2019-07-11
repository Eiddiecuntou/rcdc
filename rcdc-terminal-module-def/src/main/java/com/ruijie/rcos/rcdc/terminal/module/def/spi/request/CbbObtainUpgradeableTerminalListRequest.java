package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/6/17
 *
 * @author nt
 */
public class CbbObtainUpgradeableTerminalListRequest implements Request {

    @NotNull
    private PageSearchRequest pageSearchRequest;

    public PageSearchRequest getPageSearchRequest() {
        return pageSearchRequest;
    }

    public void setPageSearchRequest(PageSearchRequest pageSearchRequest) {
        this.pageSearchRequest = pageSearchRequest;
    }
}
