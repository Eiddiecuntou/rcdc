package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 批量开启终端检测请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月27日
 * 
 * @author nt
 */
public class StartBatDetectWebRequest implements WebRequest {
    
    @NotEmpty
    private String[] idArr;

    public String[] getIdArr() {
        return idArr;
    }

    public void setIdArr(String[] idArr) {
        this.idArr = idArr;
    }
    
}
