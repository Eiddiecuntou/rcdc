package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageRequest;

/**
 * 
 * Description: 终端检测记录分页请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbTerminalDetectPageRequest extends DefaultPageRequest{
    
    @NotNull
    private CbbDetectDateEnums date;

    public CbbDetectDateEnums getDate() {
        return date;
    }

    public void setDate(CbbDetectDateEnums date) {
        this.date = date;
    }
    
}
