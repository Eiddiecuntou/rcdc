package com.ruijie.rcos.rcdc.terminal.module.impl.spi.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @author nt
 */
public class TerminalUpgradeResult {

    private Integer result;

    private Object updatelist;

    private Integer systemUpgradeCode;

    private Object systemUpgradeInfo;

    private CbbTerminalWorkModeEnums[] terminalWorkModeArr;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Object getUpdatelist() {
        return updatelist;
    }

    public void setUpdatelist(Object updatelist) {
        this.updatelist = updatelist;
    }

    public Integer getSystemUpgradeCode() {
        return systemUpgradeCode;
    }

    public void setSystemUpgradeCode(Integer systemUpgradeCode) {
        this.systemUpgradeCode = systemUpgradeCode;
    }

    public Object getSystemUpgradeInfo() {
        return systemUpgradeInfo;
    }

    public void setSystemUpgradeInfo(Object systemUpgradeInfo) {
        this.systemUpgradeInfo = systemUpgradeInfo;
    }

    public CbbTerminalWorkModeEnums[] getTerminalWorkModeArr() {
        return terminalWorkModeArr;
    }

    public void setTerminalWorkModeArr(CbbTerminalWorkModeEnums[] terminalWorkModeArr) {
        this.terminalWorkModeArr = terminalWorkModeArr;
    }
}
