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
public class CbbListTerminalSystemUpgradePackageResponse extends DefaultResponse {

    private CbbTerminalSystemUpgradePackageInfoDTO[] packageArr;

    public CbbListTerminalSystemUpgradePackageResponse(CbbTerminalSystemUpgradePackageInfoDTO[] packageArr) {
        this.packageArr = packageArr;
    }

    public CbbTerminalSystemUpgradePackageInfoDTO[] getPackageArr() {
        return packageArr;
    }

    public void setPackageArr(CbbTerminalSystemUpgradePackageInfoDTO[] packageArr) {
        this.packageArr = packageArr;
    }
}
