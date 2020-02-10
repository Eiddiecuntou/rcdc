package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper.SyncSystemUpgradeResultHelper;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(SkyEngineRunner.class)
public class SyncSystemUpgradeResultHandlerSPIImplTest {

    @Tested
    private SyncSystemUpgradeResultHandlerSPIImpl syncSystemUpgradeResultHandlerSPI;

    @Injectable
    SyncSystemUpgradeResultHelper upgradeResultHelper;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;


    /**
     * 测试同步终端升级状态 - 获取处理对象异常
     */
    @Test
    public void testDispatchGetHandlerHasException() throws BusinessException {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        entity.setTerminalOsType("Linux");
        entity.setPlatform(CbbTerminalPlatformEnums.VDI);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;

                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
                result = new BusinessException("123");

            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId(terminalId);
        request.setRequestId("456");
        request.setData(generateJson());

        syncSystemUpgradeResultHandlerSPI.dispatch(request);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;

                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                upgradeResultHelper.responseNotUpgrade(request);
                times = 1;

                upgradeResultHelper.dealSystemUpgradeResult(entity, (TerminalSystemUpgradeHandler) any, request);
                times = 0;
            }
        };
    }

    /**
     * 测试检查组件升级- 更新终端信息
     */
    @Test
    public void testDispatchUpdateTerminalBasicInfo(@Mocked TerminalSystemUpgradeHandler handler) throws BusinessException {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        entity.setTerminalOsType("Linux");
        entity.setPlatform(CbbTerminalPlatformEnums.VDI);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;

                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
                result = handler;

            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId(terminalId);
        request.setRequestId("456");
        request.setData(generateJson());

        syncSystemUpgradeResultHandlerSPI.dispatch(request);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;

                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                upgradeResultHelper.responseNotUpgrade(request);
                times = 0;

                upgradeResultHelper.dealSystemUpgradeResult(entity, handler, request);
                times = 1;
            }
        };
    }


    private String generateJson() {
        CbbShineTerminalBasicInfo info = new CbbShineTerminalBasicInfo();
        info.setTerminalId("123");
        info.setTerminalName("t-box2");
        info.setCpuType("intel5");
        return JSON.toJSONString(info);
    }

}
