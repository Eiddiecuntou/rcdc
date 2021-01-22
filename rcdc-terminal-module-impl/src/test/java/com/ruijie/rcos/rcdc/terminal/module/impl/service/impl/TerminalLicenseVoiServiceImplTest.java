package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;

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

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

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

    /**
     * 测试getTerminalLicenseNum方法
     */
    @Test
    public void testGetTerminalLicenseNum() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "5";
            }
        };

        licenceLicenseService.getTerminalLicenseNum();
        new Verifications() {
            {
                globalParameterAPI.findParameter(licenceLicenseService.getLicenseConstansKey());
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
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 2;
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "2";
            }
        };

        int usedNum = licenceLicenseService.getUsedNum();
        Assert.assertEquals(2, usedNum);
        licenceLicenseService.getUsedNum();
        new Verifications() {
            {
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
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
            ThrowExceptionTester.throwIllegalArgumentException(() -> licenceLicenseService.updateTerminalLicenseNum(-2), "licenseNum must gt -1");
        } catch (Exception e) {
            Assert.fail();
        }

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "2";
            }
        };
        try {
            licenceLicenseService.updateTerminalLicenseNum(5);
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
            ThrowExceptionTester.throwIllegalArgumentException(() -> licenceLicenseService.updateTerminalLicenseNum(-2), "licenseNum must gt -1");
        } catch (Exception e) {
            Assert.fail();
        }

        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "5";
            }
        };
        try {
            licenceLicenseService.updateTerminalLicenseNum(2);
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
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "-1";
            }
        };
        try {
            licenceLicenseService.updateTerminalLicenseNum(-1);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.VOI_TEMINAL_LICENSE_NUM, anyString);
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
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "-1";
            }
        };
        try {
            licenceLicenseService.updateTerminalLicenseNum(5);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                terminalLicenseServiceTx.updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums.VOI, (String) any, 5);
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
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "5";
            }
        };
        try {
            licenceLicenseService.updateTerminalLicenseNum(-1);
        } catch (Exception e) {
            Assert.fail();
        }
        new Verifications() {
            {
                terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.VOI, (String) any);
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
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "5";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 4;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.auth("123", true, new CbbShineTerminalBasicInfo());
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
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "5";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 5;
            }
        };
        boolean isAuthedOrAuthSuccess = licenceLicenseService.auth("123", true, new CbbShineTerminalBasicInfo());
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
        boolean isAuthedOrAuthSuccess = licenceLicenseService.auth("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthedOrAuthSuccess);

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
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
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "-1";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 5;
            }
        };
        boolean isAuthed = licenceLicenseService.auth("123", true, new CbbShineTerminalBasicInfo());
        Assert.assertTrue(isAuthed);

    }


    /**
     * 测试decreaseCacheLicenseUsedNum方法，已授权终端接入
     */
    @Test
    public void testdecreaseCacheLicenseUsedNum() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "10";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 5;
            }
        };
        licenceLicenseService.decreaseCacheLicenseUsedNum();
        int used = licenceLicenseService.getUsedNum();
        assertEquals(used, 4);
    }

    /**
     * 测试decreaseCacheLicenseUsedNum方法，已授权终端接入
     */
    @Test
    public void testgetUsedNum() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "30";
            }
        };
        Deencapsulation.setField(licenceLicenseService, "usedNum", 100);
        int used = licenceLicenseService.getUsedNum();
        assertEquals(used, 100);
    }

    /**
     * 测试decreaseCacheLicenseUsedNum方法，已授权终端接入
     */
    @Test
    public void testgetUsedNumTemp() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "-1";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 5;
            }
        };
        Deencapsulation.setField(licenceLicenseService, "usedNum", 100);
        int used = licenceLicenseService.getUsedNum();
        assertEquals(used, 5);
    }

    /**
     * 测试decreaseCacheLicenseUsedNum方法，已授权终端接入
     */
    @Test
    public void testdecreaseCacheLicenseUsedNumTemp() {
        new Expectations() {
            {
                globalParameterAPI.findParameter(Constants.VOI_TEMINAL_LICENSE_NUM);
                result = "-1";
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 5;
                basicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                result = 4;
            }
        };
        licenceLicenseService.decreaseCacheLicenseUsedNum();
        int used = licenceLicenseService.getUsedNum();
        assertEquals(used, 4);
    }

}
