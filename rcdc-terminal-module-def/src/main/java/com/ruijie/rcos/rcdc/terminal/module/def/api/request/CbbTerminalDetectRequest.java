package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectItemEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;

/**
 * Description: 终端检测请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalDetectRequest implements Request {
    @NotNull
    private String terminalId;

    @Nullable
    private CbbDetectItemEnums[] itemArr;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public CbbDetectItemEnums[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbDetectItemEnums[] itemArr) {
        this.itemArr = itemArr;
    }
}
