package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Description:TerminalLicenseVoiServiceImpl测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/22 4:27 下午
 *
 * @author lin
 */
@RunWith(SkyEngineRunner.class)
public class TerminalLicenseVoiServiceImplTest {

    @Tested
    private TerminalLicenseVoiServiceImpl licenceLicenseService;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalLicenseVoiUpgradeServiceImpl terminalLicenseVOIUpgradeServiceImpl;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    TerminalAuthorizeDAO terminalAuthorizeDAO;



}
