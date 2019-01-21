package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.sk.base.annotation.NotEmpty;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 批量添加系统升级任务请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月22日
 *
 * @author nt
 */
public class DeleteTerminalSystemUpgradeRequest implements WebRequest {

    @NotEmpty
    private String[] idArr;

    public String[] getIdArr() {
        return idArr;
    }

    public void setIdArr(String[] idArr) {
        this.idArr = idArr;
    }

}
