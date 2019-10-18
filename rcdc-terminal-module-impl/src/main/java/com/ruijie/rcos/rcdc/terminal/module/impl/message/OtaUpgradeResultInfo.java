package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public class OtaUpgradeResultInfo {

    // FIXME 这个注解使持久化使用的，你这个对象会存到数据库吗？
    @Enumerated(EnumType.STRING)
    private CbbSystemUpgradeStateEnums upgradeResult;

    private String failMsg;

    private String otaVersion;

    private ShineTerminalBasicInfo basicInfo;

    public CbbSystemUpgradeStateEnums getUpgradeResult() {
        return upgradeResult;
    }

    public void setUpgradeResult(CbbSystemUpgradeStateEnums upgradeResult) {
        this.upgradeResult = upgradeResult;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public String getOtaVersion() {
        return otaVersion;
    }

    public void setOtaVersion(String otaVersion) {
        this.otaVersion = otaVersion;
    }

    public ShineTerminalBasicInfo getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(ShineTerminalBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }
}
