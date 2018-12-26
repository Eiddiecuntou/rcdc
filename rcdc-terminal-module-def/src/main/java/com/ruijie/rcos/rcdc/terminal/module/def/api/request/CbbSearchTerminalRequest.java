package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageRequest;

import javax.validation.constraints.NotNull;

/**
 * Description: 搜索终端请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/4
 *
 * @author Jarman
 */
public class CbbSearchTerminalRequest extends DefaultPageRequest {

    @NotNull
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
