package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectItemEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.lang.Nullable;

/**
 * Description: 终端批量检测请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalBatDetectRequest implements Request {
    @NotNull
    private String[] terminalIdArr;

    @Nullable
    private CbbDetectItemEnums[] itemArr;

    public String[] getTerminalIdArr() {
        return terminalIdArr;
    }

    public void setTerminalIdArr(String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

    public CbbDetectItemEnums[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbDetectItemEnums[] itemArr) {
        this.itemArr = itemArr;
    }
}
