package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;

import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description:TerminalLicenseServiceImpl测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/23 4:27 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class TerminalLicenseServiceImplTest {

    @Tested
    TerminalLicenseIDVServiceImpl licenceLicenseService;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    @Injectable
    TerminalAuthorizeDAO terminalAuthorizeDAO;


}
