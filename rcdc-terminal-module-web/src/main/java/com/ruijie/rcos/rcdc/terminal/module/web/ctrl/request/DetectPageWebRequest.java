package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;

/**
 * 
 * Description: 终端检测分页列表请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月27日
 * 
 * @author nt
 */
public class DetectPageWebRequest extends PageWebRequest {
    
    @NotNull
    private CbbDetectDateEnums date;

    public CbbDetectDateEnums getDate() {
        return date;
    }

    public void setDate(CbbDetectDateEnums date) {
        this.date = date;
    }
    
}
