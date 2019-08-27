package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/15
 *
 * @author nt
 */
public class CbbUpgradePackageResponse extends DefaultResponse {

    private CbbTerminalSystemUpgradePackageInfoDTO packageInfo;

    public CbbUpgradePackageResponse(CbbTerminalSystemUpgradePackageInfoDTO packageInfo) {
        this.packageInfo = packageInfo;
    }

    public CbbTerminalSystemUpgradePackageInfoDTO getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(CbbTerminalSystemUpgradePackageInfoDTO packageInfo) {
        this.packageInfo = packageInfo;
    }
}
