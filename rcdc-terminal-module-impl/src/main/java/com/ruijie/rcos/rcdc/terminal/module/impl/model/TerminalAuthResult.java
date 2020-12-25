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

    private boolean authed;

    private TerminalAuthResultEnums authResult;

    public TerminalAuthResult(boolean authed, TerminalAuthResultEnums authResult) {
        this.authed = authed;
        this.authResult = authResult;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed(boolean authed) {
        this.authed = authed;
    }

    public TerminalAuthResultEnums getAuthResult() {
        return authResult;
    }

    public void setAuthResult(TerminalAuthResultEnums authResult) {
        this.authResult = authResult;
    }
}
