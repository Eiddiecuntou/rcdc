package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseNumDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalLicenseIDVServiceImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:CbbTerminalLicenseMgmtAPIImpl测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/23 9:25 上午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalLicenseMgmtAPIImplTest {

    @Tested
    CbbTerminalLicenseMgmtAPIImpl cbbTerminalLicenseMgmtAPI;

    @Injectable
    TerminalLicenseIDVServiceImpl TerminalLicenseService;

    @Injectable
    CbbTerminalLicenseFactoryProvider licenseFactoryProvider;

    @Injectable
    TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalAuthHelper terminalAuthHelper;


    @Test
    public void testCancelTerminalAuth() throws BusinessException {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setAuthed(false);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId("123");
                result = terminalEntity;
            }
        };
        cbbTerminalLicenseMgmtAPI.cancelTerminalAuth("123");
        new Verifications() {
            {
                terminalAuthHelper.processDecreaseTerminalLicense(anyString, (CbbTerminalPlatformEnums) any, false);
                times = 0;
            }
        };
    }

    @Test
    public void testCancelTerminalAuth2() throws BusinessException {
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId("123");
                result = null;
            }
        };
        cbbTerminalLicenseMgmtAPI.cancelTerminalAuth("123");
        new Verifications() {
            {
                terminalAuthHelper.processDecreaseTerminalLicense(anyString, (CbbTerminalPlatformEnums) any, false);
                times = 0;
            }
        };
    }

    @Test
    public void testCancelTerminalAuth3() throws BusinessException {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setTerminalId("123");
        terminalEntity.setAuthed(Boolean.TRUE);
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.IDV);
        terminalEntity.setAuthMode(CbbTerminalPlatformEnums.IDV);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId("123");
                result = terminalEntity;
                basicInfoDAO.save(terminalEntity);
            }
        };
        cbbTerminalLicenseMgmtAPI.cancelTerminalAuth("123");
        new Verifications() {
            {
                terminalAuthHelper.processDecreaseTerminalLicense("123", CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                times = 1;
            }
        };
    }
}
