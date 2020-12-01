package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

/**
 * Description: 终端业务配置
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/18
 *
 * @author Jarman
 */
public class CbbTerminalBizConfigDTO {

    private CbbTerminalWorkModeEnums[] terminalWorkModeArr;

    private CbbTerminalPlatformEnums terminalPlatform;

    public CbbTerminalWorkModeEnums[] getTerminalWorkModeArr() {
        return terminalWorkModeArr;
    }

    public void setTerminalWorkModeArr(CbbTerminalWorkModeEnums[] terminalWorkModeArr) {
        this.terminalWorkModeArr = terminalWorkModeArr;
    }

    public CbbTerminalPlatformEnums getTerminalPlatform() {
        return terminalPlatform;
    }

    public void setTerminalPlatform(CbbTerminalPlatformEnums terminalPlatform) {
        this.terminalPlatform = terminalPlatform;
    }
}
