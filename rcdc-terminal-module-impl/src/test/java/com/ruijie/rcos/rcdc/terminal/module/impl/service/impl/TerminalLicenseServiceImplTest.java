package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/23 4:27 下午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class TerminalLicenseServiceImplTest {

    @Tested
    TerminalLicenseServiceImpl licenceLicenseService;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;


    /**
     * 测试getTerminalLicenseNum方法
     */
    @Test
    public void testGetTerminalLicenseNum() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
            }
        };
        int terminalLicenseNum = licenceLicenseService.getTerminalLicenseNum();
        System.out.println(licenceLicenseService);
        Assert.assertEquals(5, terminalLicenseNum);

        licenceLicenseService.getTerminalLicenseNum();
        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                times = 1;
            }
        };
    }

    /**
     * 测试getUsedNum方法
     */
    @Test
    public void testGetUsedNum() {
        new Expectations() {
            {
                basicInfoDAO.count();
                result = 2;
            }
        };

        int usedNum = licenceLicenseService.getUsedNum();
        Assert.assertEquals(2, usedNum);

        licenceLicenseService.getUsedNum();
        new Verifications() {
            {
                basicInfoDAO.count();
                times = 1;
            }
        };
    }

    /**
     * 测试updateTerminalLicenseNum
     */
    @Test
    public void testUpdateTerminalLicenseNum() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> licenceLicenseService.updateTerminalLicenseNum(-2),
                "licenseNum must gt -1");
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            licenceLicenseService.updateTerminalLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * 测试authedOrAuthSuccess方法，新终端接入，有剩余授权
     */
    @Test
    public void testAuthSuccess() {
        new Expectations() {
            {
                basicInfoService.isNewTerminal(withEqual("123"));
                result = true;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
                basicInfoDAO.count();
                result = 4;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.isAuthedOrAuthSuccess("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthedOrAuthSuccess);

    }

    /**
     * 测试authedOrAuthSuccess方法，新终端接入，无剩余授权
     */
    @Test
    public void testNoAuth() {
        new Expectations() {
            {
                basicInfoService.isNewTerminal(withEqual("123"));
                result = true;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
                basicInfoDAO.count();
                result = 5;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.isAuthedOrAuthSuccess("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(!isAuthedOrAuthSuccess);

    }

    /**
     * 测试authedOrAuthSuccess方法，已授权终端接入
     */
    @Test
    public void testOldTerminal() {
        new Expectations() {
            {
                basicInfoService.isNewTerminal(withEqual("123"));
                result = false;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.isAuthedOrAuthSuccess("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthedOrAuthSuccess);

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                times = 0;
                basicInfoDAO.count();
                times = 0;
            }
        };
    }

    /**
     * 测试authedOrAuthSuccess方法，授权总数为-1的情况
     */
    @Test
    public void test() {
        new Expectations() {
            {
                basicInfoService.isNewTerminal(withEqual("123"));
                result = true;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "-1";
                basicInfoDAO.count();
                result = 5;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.isAuthedOrAuthSuccess("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthedOrAuthSuccess);

    }
}