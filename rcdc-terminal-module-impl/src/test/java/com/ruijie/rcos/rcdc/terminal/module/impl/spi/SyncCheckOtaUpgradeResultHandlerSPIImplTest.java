package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckOtaUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CheckOtaUpgradeResult;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/16
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class SyncCheckOtaUpgradeResultHandlerSPIImplTest {

    @Tested
    private SyncCheckOtaUpgradeResultHandlerSPIImpl syncCheckOtaUpgradeResultHandler;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    /**
     * 测试dispatch
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("123");
        request.setRequestId("456");
        request.setData(generateJson());
        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        upgradePackageEntity.setPackageType(TerminalPlatformEnums.RK3188);
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(TerminalPlatformEnums.RK3188);
                result = upgradePackageEntity;

            }
        };
        syncCheckOtaUpgradeResultHandler.dispatch(request);
        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findFirstByPackageType(TerminalPlatformEnums.RK3188);
                times = 1;
                systemUpgradeTerminalDAO.save((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;
            }
        };

    }

    private String generateJson() {
        CheckOtaUpgradeResult checkOtaUpgradeResult = new CheckOtaUpgradeResult();
        checkOtaUpgradeResult.setCheckOtaUpgradeResult(CheckOtaUpgradeResultEnums.NEED_UPGRADE);
        checkOtaUpgradeResult.setTerminalId("123");
        return JSON.toJSONString(checkOtaUpgradeResult);
    }

}
