package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;

import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
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
public class TerminalLicenseVoiUpgradeServiceImplTest {

    @Tested
    private TerminalLicenseVoiUpgradeServiceImpl licenceLicenseService;

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
    private TerminalLicenseIDVServiceImpl terminalLicenseIDVServiceImpl;

    @Injectable
    TerminalAuthorizeDAO terminalAuthorizeDAO;

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
                globalParameterAPI.findParameter(Constants.VOI_UPGRADE_TEMINAL_LICENSE_NUM);
                times = 0;
                basicInfoDAO.count();
                times = 0;
            }
        };
    }

}
