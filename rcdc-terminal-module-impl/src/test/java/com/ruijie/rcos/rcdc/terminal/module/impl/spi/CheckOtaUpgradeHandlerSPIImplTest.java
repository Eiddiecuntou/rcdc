package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CheckOtaUpgradeInfo;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/16
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class CheckOtaUpgradeHandlerSPIImplTest {

    @Tested
    private CheckOtaUpgradeHandlerSPIImpl checkOtaUpgradeHandler;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 测试dispatch
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("123");
        request.setRequestId("456");
        request.setData(generateJson());
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        upgradePackage.setUpgradeMode(CbbSystemUpgradeModeEnums.AUTO);
        upgradePackage.setIsDelete(false);
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
                result = upgradePackage;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
            }
        };
        checkOtaUpgradeHandler.dispatch(request);
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
                times = 1;
                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;

            }
        };

    }

    private String generateJson() {
        CheckOtaUpgradeInfo checkOtaUpgradeInfo = new CheckOtaUpgradeInfo();
        checkOtaUpgradeInfo.setOtaVersion("1.1.0");
        checkOtaUpgradeInfo.setTerminalId("123");
        return JSON.toJSONString(checkOtaUpgradeInfo);
    }
}
