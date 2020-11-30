package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;

/**
 * Description: TerminalAuthHelper
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/20
 *
 * @author nting
 */
public class TerminalAuthResult {

    private boolean needSaveTerminalInfo;

    private TerminalAuthResultEnums authResult;

    public TerminalAuthResult(boolean needSaveTerminalInfo, TerminalAuthResultEnums authResult) {
        this.needSaveTerminalInfo = needSaveTerminalInfo;
        this.authResult = authResult;
    }

    public boolean isNeedSaveTerminalInfo() {
        return needSaveTerminalInfo;
    }

    public void setNeedSaveTerminalInfo(boolean needSaveTerminalInfo) {
        this.needSaveTerminalInfo = needSaveTerminalInfo;
    }

    public TerminalAuthResultEnums getAuthResult() {
        return authResult;
    }

    public void setAuthResult(TerminalAuthResultEnums authResult) {
        this.authResult = authResult;
    }
}
