package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckOtaUpgradeResultEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public class CheckOtaUpgradeResult {

    private String terminalId;

    private CheckOtaUpgradeResultEnums checkOtaUpgradeResult;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public CheckOtaUpgradeResultEnums getCheckOtaUpgradeResult() {
        return checkOtaUpgradeResult;
    }

    public void setCheckOtaUpgradeResult(CheckOtaUpgradeResultEnums checkOtaUpgradeResult) {
        this.checkOtaUpgradeResult = checkOtaUpgradeResult;
    }
}


