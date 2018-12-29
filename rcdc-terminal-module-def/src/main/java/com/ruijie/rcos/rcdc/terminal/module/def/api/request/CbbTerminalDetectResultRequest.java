package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 终端检测结果请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbTerminalDetectResultRequest implements Request {
    
    @NotNull
    private CbbDetectDateEnums detectDate;

    public CbbDetectDateEnums getDetectDate() {
        return detectDate;
    }

    public void setDetectDate(CbbDetectDateEnums detectDate) {
        this.detectDate = detectDate;
    }
    
    

}
