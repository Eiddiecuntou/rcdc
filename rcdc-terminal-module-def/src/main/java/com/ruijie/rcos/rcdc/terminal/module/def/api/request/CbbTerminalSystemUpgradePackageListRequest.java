package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import org.springframework.lang.Nullable;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * 
 * Description: 终端系统升级包列表请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class CbbTerminalSystemUpgradePackageListRequest implements Request {

    /**
     * 终端平台类型
     */
    @Nullable
    private TerminalPlatformEnums paltform;

    public TerminalPlatformEnums getPaltform() {
        return paltform;
    }

    public void setPaltform(TerminalPlatformEnums paltform) {
        this.paltform = paltform;
    }

}
