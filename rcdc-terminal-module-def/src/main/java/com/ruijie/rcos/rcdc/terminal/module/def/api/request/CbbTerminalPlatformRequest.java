package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import com.ruijie.rcos.sk.base.support.EqualsHashcodeSupport;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
public class CbbTerminalPlatformRequest extends EqualsHashcodeSupport implements Request {

    @NotNull
    private CbbTerminalPlatformEnums[] platformArr;

    public CbbTerminalPlatformEnums[] getPlatformArr() {
        return platformArr;
    }

    public void setPlatformArr(CbbTerminalPlatformEnums[] platformArr) {
        this.platformArr = platformArr;
    }
}
