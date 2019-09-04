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

    private CbbTerminalSystemUpgradePackageInfoDTO[] itemArr;

    public CbbListTerminalSystemUpgradePackageResponse(CbbTerminalSystemUpgradePackageInfoDTO[] itemArr) {
        this.itemArr = itemArr;
    }

    public CbbTerminalSystemUpgradePackageInfoDTO[] getItemArr() {
        return itemArr;
    }

    public void setItemArr(CbbTerminalSystemUpgradePackageInfoDTO[] itemArr) {
        this.itemArr = itemArr;
    }
}
