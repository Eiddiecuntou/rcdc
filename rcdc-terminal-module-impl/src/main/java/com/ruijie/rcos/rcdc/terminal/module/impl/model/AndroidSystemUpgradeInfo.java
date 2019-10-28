package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/11
 *
 * @author hs
 */
public class AndroidSystemUpgradeInfo {

    private String otaVersion;

    private String otaSeedLink;

    private String otaSeedMD5;

    private String otaMD5;

    private CbbSystemUpgradeModeEnums upgradeModeEnums;

    public String getOtaVersion() {
        return otaVersion;
    }

    public void setOtaVersion(String otaVersion) {
        this.otaVersion = otaVersion;
    }

    public String getOtaSeedLink() {
        return otaSeedLink;
    }

    public void setOtaSeedLink(String otaSeedLink) {
        this.otaSeedLink = otaSeedLink;
    }

    public String getOtaSeedMD5() {
        return otaSeedMD5;
    }

    public void setOtaSeedMD5(String otaSeedMD5) {
        this.otaSeedMD5 = otaSeedMD5;
    }

    public String getOtaMD5() {
        return otaMD5;
    }

    public void setOtaMD5(String otaMD5) {
        this.otaMD5 = otaMD5;
    }

    public CbbSystemUpgradeModeEnums getUpgradeModeEnums() {
        return upgradeModeEnums;
    }

    public void setUpgradeModeEnums(CbbSystemUpgradeModeEnums upgradeModeEnums) {
        this.upgradeModeEnums = upgradeModeEnums;
    }
}
