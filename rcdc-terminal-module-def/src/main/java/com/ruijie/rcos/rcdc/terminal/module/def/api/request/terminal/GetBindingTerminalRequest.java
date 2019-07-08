package com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal;

import java.util.UUID;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 获取绑定的终端请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月2日
 * 
 * @author nt
 */
public class GetBindingTerminalRequest implements Request {
    
    @NotNull
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
}
