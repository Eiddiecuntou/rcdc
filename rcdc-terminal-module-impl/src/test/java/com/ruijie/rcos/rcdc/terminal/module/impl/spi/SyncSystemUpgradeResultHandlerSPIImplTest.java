package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper.SyncSystemUpgradeResultHelper;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Injectable
    private TerminalBasicInfoService basicInfoService;

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
                basicInfoService.obtainTerminalType(entity);
                result = CbbTerminalTypeEnums.VDI_LINUX;
                handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_X86);
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

                handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_X86);
                times = 1;

                upgradeResultHelper.responseNotUpgrade(request);
                times = 1;

                upgradeResultHelper.dealSystemUpgradeResult(entity, (TerminalTypeArchType) any, (TerminalSystemUpgradeHandler) any, request);
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
        entity.setPlatform(CbbTerminalPlatformEnums.VOI);

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;
                basicInfoService.obtainTerminalType(entity);
                result = CbbTerminalTypeEnums.IDV_LINUX;
                handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_X86);
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

                handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_X86);
                times = 1;

                upgradeResultHelper.responseNotUpgrade(request);
                times = 0;

                upgradeResultHelper.dealSystemUpgradeResult(entity, (TerminalTypeArchType) any, handler, request);
                times = 1;
            }
        };
    }

    /**
     * 测试检查组件升级- CT3120终端
     */
    @Test
    public void testDispatchUpdateTerminalBasicInfoIsCT3120(@Mocked TerminalSystemUpgradeHandler handler) throws BusinessException {
        String terminalId = "123";
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId("123456");
        entity.setTerminalName("t-box3");
        entity.setCpuType("intel");
        entity.setTerminalOsType("Linux");
        entity.setPlatform(CbbTerminalPlatformEnums.VOI);
        entity.setProductId("80020101");

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;
                basicInfoService.obtainTerminalType(entity);
                result = CbbTerminalTypeEnums.IDV_LINUX;
                handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_X86);
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

                handlerFactory.getHandler(TerminalTypeArchType.LINUX_IDV_X86);
                times = 1;

                upgradeResultHelper.responseNotUpgrade(request);
                times = 0;

                upgradeResultHelper.dealSystemUpgradeResult(entity, (TerminalTypeArchType) any, handler, request);
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
