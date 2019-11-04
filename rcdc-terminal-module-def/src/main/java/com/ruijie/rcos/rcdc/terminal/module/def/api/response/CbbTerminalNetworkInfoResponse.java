package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/1
 *
 * @author nt
 */
public class CbbTerminalNetworkInfoResponse extends DefaultResponse {

    private CbbTerminalNetworkInfoDTO[] itemArr;

    public CbbTerminalNetworkInfoResponse(CbbTerminalNetworkInfoDTO[] itemArr) {
        Assert.notNull(itemArr, "itemArr can not be null");
        this.itemArr = itemArr;
    }

    public CbbTerminalNetworkInfoDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbTerminalNetworkInfoDTO[] itemArr) {
        this.itemArr = itemArr;
    }
}
