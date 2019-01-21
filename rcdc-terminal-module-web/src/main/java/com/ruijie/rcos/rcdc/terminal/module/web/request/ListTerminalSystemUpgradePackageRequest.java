package com.ruijie.rcos.rcdc.terminal.module.web.request;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 系统升级包列表请求
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月21日
 * 
 * @author nt
 */
public class ListTerminalSystemUpgradePackageRequest implements WebRequest {
    
    /**
     * 终端类型
     */
    @NotNull
    private TerminalPlatformEnums platform;

    public TerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(TerminalPlatformEnums platform) {
        this.platform = platform;
    }

}
