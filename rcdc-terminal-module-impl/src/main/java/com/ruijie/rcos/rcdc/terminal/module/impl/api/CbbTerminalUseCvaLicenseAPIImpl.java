package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalUseCvaLicenseAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/26 18:13
 *
 * @author yanlin
 */
public class CbbTerminalUseCvaLicenseAPIImpl implements CbbTerminalUseCvaLicenseAPI {

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Override
    public int obtainTerminalUseCvaLicenseNum() {
        return terminalAuthorizeDAO.countByAuthModeAndAuthedAndLicenseType(CbbTerminalPlatformEnums.IDV, Boolean.TRUE,
            CbbTerminalLicenseTypeEnums.CVA);
    }
}
