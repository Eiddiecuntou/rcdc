package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

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
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    TerminalLicenseServiceImpl licenceLicenseService;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalLicenseServiceTx terminalLicenseServiceTx;


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
        int terminalLicenseNum = licenceLicenseService.getIDVTerminalLicenseNum();
        System.out.println(licenceLicenseService);
        Assert.assertEquals(5, terminalLicenseNum);

        licenceLicenseService.getIDVTerminalLicenseNum();
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
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = 2;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "2";
            }
        };

        int usedNum = licenceLicenseService.getIDVUsedNum();
        Assert.assertEquals(2, usedNum);
        licenceLicenseService.getIDVUsedNum();
        new Verifications() {
            {
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
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
            ThrowExceptionTester.throwIllegalArgumentException(() -> licenceLicenseService.updateIDVTerminalLicenseNum(-2),
                "licenseNum must gt -1");
        } catch (Exception e) {
            Assert.fail();
        }

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "2";
            }
        };
        try {
            licenceLicenseService.updateIDVTerminalLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * 测试updateTerminalLicenseNum，异常情况：减少证书授权证书的数量
     */
    @Test
    public void testUpdateTerminalLicenseNumExcepiton() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> licenceLicenseService.updateIDVTerminalLicenseNum(-2),
                "licenseNum must gt -1");
        } catch (Exception e) {
            Assert.fail();
        }

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
            }
        };
        try {
            licenceLicenseService.updateIDVTerminalLicenseNum(2);
        } catch (BusinessException e) {
            Assert.assertTrue(e.getKey().equals("rcdc_terminal_not_allow_reduce_terminal_license_num"));
            return;


        }
        Assert.fail();
    }

    /**
     * 测试updateTerminalLicenseNum
     */
    @Test
    public void testUpdateTerminalLicenseNumEqualsOriginalLicenseNum() {

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "-1";
            }
        };
        try {
            licenceLicenseService.updateIDVTerminalLicenseNum(-1);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试updateTerminalLicenseNum，场景：-1 -> 非-1
     */
    @Test
    public void testUpdateTerminalLicenseNumMinusOne2NotMinusOne() {

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "-1";
            }
        };
        try {
            licenceLicenseService.updateIDVTerminalLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                terminalLicenseServiceTx.updateAllIDVTerminalUnauthedAndUpdateLicenseNum(5);
                times = 1;
            }
        };
    }

    /**
     * 测试updateTerminalLicenseNum，场景：非-1 -> -1
     */
    @Test
    public void testUpdateTerminalLicenseNumNotMinusOne2MinusOne() {

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
            }
        };
        try {
            licenceLicenseService.updateIDVTerminalLicenseNum(-1);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                terminalLicenseServiceTx.updateAllIDVTerminalAuthedAndUnlimitIDVTerminalAuth();
                times = 1;
            }
        };
    }

    /**
     * 测试authedOrAuthSuccess方法，新终端接入，有剩余授权
     */
    @Test
    public void testAuthSuccess() {
        new Expectations() {
            {
                basicInfoService.isAuthed(withEqual("123"));
                result = false;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = 4;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.authIDV("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthedOrAuthSuccess);

    }

    /**
     * 测试authedOrAuthSuccess方法，新终端接入，无剩余授权
     */
    @Test
    public void testNoAuth() {
        new Expectations() {
            {
                basicInfoService.isAuthed(withEqual("123"));
                result = false;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "5";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = 5;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.authIDV("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(!isAuthedOrAuthSuccess);

    }

    /**
     * 测试authedOrAuthSuccess方法，已授权终端接入
     */
    @Test
    public void testOldTerminal() {
        new Expectations() {
            {
                basicInfoService.isAuthed(withEqual("123"));
                result = true;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.authIDV("123", true, new CbbShineTerminalBasicInfo());
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
     * 测试auth方法，授权总数为-1的情况
     */
    @Test
    public void testAuth() {
        new Expectations() {
            {
                basicInfoService.isAuthed(withEqual("123"));
                result = false;
                globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
                result = "-1";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                result = 5;
            }
        };
        boolean isAuthed = licenceLicenseService.authIDV("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthed);

    }
}