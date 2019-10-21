package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.OtaUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/15
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class SyncOtaUpgradeResultHandlerSPIImplTest {

    @Tested
    private SyncOtaUpgradeResultHandlerSPIImpl syncOtaUpgradeResultHandler;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    /**
     * 测试 dispatch
     *
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("123");
        request.setRequestId("456");
        request.setData(generateJson());
        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setId(UUID.randomUUID());
        packageEntity.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        packageEntity.setIsDelete(false);
        List<TerminalSystemUpgradeEntity> systemUpgradeList = new ArrayList<>();
        TerminalSystemUpgradeEntity systemUpgrade = new TerminalSystemUpgradeEntity();
        systemUpgrade.setId(UUID.randomUUID());
        systemUpgradeList.add(systemUpgrade);
        TerminalSystemUpgradeTerminalEntity terminalEntity = new TerminalSystemUpgradeTerminalEntity();
        terminalEntity.setTerminalId("123");
        new Expectations() {
            {
                basicInfoService.saveBasicInfo(anyString, (ShineTerminalBasicInfo) any);
                termianlSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
                result = packageEntity;
                systemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List< CbbSystemUpgradeTaskStateEnums >) any);
                result = systemUpgradeList;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId((UUID) any, anyString);
                result = terminalEntity;

            }
        };

        syncOtaUpgradeResultHandler.dispatch(request);
        new Verifications() {
            {
                basicInfoService.saveBasicInfo(anyString, (ShineTerminalBasicInfo) any);
                times = 1;
                termianlSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
                times = 1;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId((UUID) any, anyString);
                times = 1;
                systemUpgradeTerminalDAO.save((TerminalSystemUpgradeTerminalEntity) any);

            }
        };

    }

    private String generateJson() {
        OtaUpgradeResultInfo upgradeResultInfo = new OtaUpgradeResultInfo();
        upgradeResultInfo.setOtaVersion("1.1.0");
        upgradeResultInfo.setUpgradeResult(CbbSystemUpgradeStateEnums.SUCCESS);
        ShineTerminalBasicInfo terminalBasicInfo = new ShineTerminalBasicInfo();
        terminalBasicInfo.setTerminalId("123");
        terminalBasicInfo.setTerminalName("android-vdi");
        terminalBasicInfo.setCpuType("intel5");
        upgradeResultInfo.setBasicInfo(terminalBasicInfo);
        return JSON.toJSONString(upgradeResultInfo);
    }


}
