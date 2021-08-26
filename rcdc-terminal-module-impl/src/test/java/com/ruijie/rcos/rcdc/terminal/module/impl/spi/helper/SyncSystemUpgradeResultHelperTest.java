package com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SystemUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.StartSystemUpgradeResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.*;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/5/24
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class SyncSystemUpgradeResultHelperTest {

    @Tested
    private SyncSystemUpgradeResultHelper helper;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeServiceTx systemUpgradeServiceTx;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Mocked
    private TerminalSystemUpgradeHandler handler;

    /**
     * testDealSystemUpgradeResultNoNeedUpgrade
     */
    @Test
    public void testDealSystemUpgradeResultNoNeedUpgrade() {

        TerminalEntity basicInfoEntity = buildTerminalEntity();

        CbbDispatcherRequest request = buildRequest(UUID.randomUUID(), CbbSystemUpgradeStateEnums.UPGRADING);

        new Expectations() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                result = false;
            }
        };

        helper.dealSystemUpgradeResult(basicInfoEntity, TerminalTypeArchType.LINUX_IDV_X86, handler, request);

        new Verifications() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                CbbResponseShineMessage responseMessage;
                messageHandlerAPI.response(responseMessage = withCapture());
                StartSystemUpgradeResult result = (StartSystemUpgradeResult) responseMessage.getContent();
                assertEquals(false, result.getEnableUpgrade());
            }
        };
    }

    /**
     * testDealSystemUpgradeResult
     */
    @Test
    public void testDealSystemUpgradeResult() {

        TerminalEntity basicInfoEntity = buildTerminalEntity();

        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        CbbDispatcherRequest request = buildRequest(upgradeEntity.getId(), CbbSystemUpgradeStateEnums.UPGRADING);


        new Expectations() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                result = true;

                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, (List) any);
                result = upgradeEntity;

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                result = null;

                systemUpgradeTerminalDAO.save((TerminalSystemUpgradeTerminalEntity) any);

                handler.checkAndHoldUpgradeQuota(request.getTerminalId());
                result = false;
            }
        };

        helper.dealSystemUpgradeResult(basicInfoEntity, TerminalTypeArchType.LINUX_IDV_X86, handler, request);

        new Verifications() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                List<CbbSystemUpgradeTaskStateEnums> stateList;
                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, stateList = withCapture());
                times = 1;
                assertEquals(1, stateList.size());
                assertEquals(CbbSystemUpgradeTaskStateEnums.UPGRADING, stateList.get(0));

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                times = 1;

                TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity;
                systemUpgradeTerminalDAO.save(upgradeTerminalEntity = withCapture());
                times = 1;
                assertEquals("123", upgradeTerminalEntity.getTerminalId());
                assertEquals(CbbSystemUpgradeStateEnums.WAIT, upgradeTerminalEntity.getState());
                assertEquals(upgradeEntity.getId(), upgradeTerminalEntity.getSysUpgradeId());

                handler.checkAndHoldUpgradeQuota(request.getTerminalId());
                times = 1;

                CbbResponseShineMessage responseMessage;
                messageHandlerAPI.response(responseMessage = withCapture());
                StartSystemUpgradeResult result = (StartSystemUpgradeResult) responseMessage.getContent();
                assertEquals(false, result.getEnableUpgrade());
            }
        };
    }

    /**
     * testDealSystemUpgradeResult
     */
    @Test
    public void testDealSystemUpgradeResultNotInUpgradeProcessing() {

        TerminalEntity basicInfoEntity = buildTerminalEntity();

        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        CbbDispatcherRequest request = buildRequest(upgradeEntity.getId(), CbbSystemUpgradeStateEnums.NO_NEED);


        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalEntity.setId(UUID.randomUUID());
        upgradeTerminalEntity.setState(CbbSystemUpgradeStateEnums.SUCCESS);

        new Expectations() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                result = true;

                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, (List) any);
                result = upgradeEntity;

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                result = upgradeTerminalEntity;
            }
        };

        helper.dealSystemUpgradeResult(basicInfoEntity, TerminalTypeArchType.LINUX_IDV_X86, handler, request);

        new Verifications() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                List<CbbSystemUpgradeTaskStateEnums> stateList;
                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, stateList = withCapture());
                times = 1;
                assertEquals(1, stateList.size());
                assertEquals(CbbSystemUpgradeTaskStateEnums.UPGRADING, stateList.get(0));

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                times = 1;

                systemUpgradeTerminalDAO.save((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

                messageHandlerAPI.response((CbbResponseShineMessage) any);
                times = 1;
            }
        };
    }

    /**
     * testDealSystemUpgradeResultWithProcessing
     */
    @Test
    public void testDealSystemUpgradeResultWithProcessing() {

        TerminalEntity basicInfoEntity = buildTerminalEntity();

        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        CbbDispatcherRequest request = buildRequest(upgradeEntity.getId(), CbbSystemUpgradeStateEnums.WAIT);

        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalEntity.setState(CbbSystemUpgradeStateEnums.UPGRADING);

        new Expectations() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                result = true;

                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, (List) any);
                result = upgradeEntity;

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                result = upgradeTerminalEntity;
            }
        };

        helper.dealSystemUpgradeResult(basicInfoEntity, TerminalTypeArchType.LINUX_IDV_X86, handler, request);

        new Verifications() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

                List<CbbSystemUpgradeTaskStateEnums> stateList;
                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, stateList = withCapture());
                times = 1;
                assertEquals(1, stateList.size());
                assertEquals(CbbSystemUpgradeTaskStateEnums.UPGRADING, stateList.get(0));

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                times = 2;
            }
        };
    }

    /**
     * testDealSystemUpgradeResultWithProcessing
     */
    @Test
    public void testDealSystemUpgradeResultWithProcessing2() {

        TerminalEntity basicInfoEntity = buildTerminalEntity();

        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        CbbDispatcherRequest request = buildRequest(upgradeEntity.getId(), CbbSystemUpgradeStateEnums.UPGRADING);

        TerminalSystemUpgradeTerminalEntity upgradeTerminalEntity = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminalEntity.setState(CbbSystemUpgradeStateEnums.UPGRADING);

        new Expectations() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                result = true;

                handler.checkAndHoldUpgradeQuota(request.getTerminalId());
                result = true;

                terminalSystemUpgradeDAO.findByPackageTypeAndCpuArchAndStateInOrderByCreateTimeAsc(CbbTerminalTypeEnums.VDI_LINUX,
                        CbbCpuArchType.X86_64, (List) any);
                result = upgradeEntity;

                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), basicInfoEntity.getTerminalId());
                result = upgradeTerminalEntity;
            }
        };

        helper.dealSystemUpgradeResult(basicInfoEntity, TerminalTypeArchType.LINUX_IDV_X86, handler, request);

        new Verifications() {
            {
                handler.isTerminalEnableUpgrade(basicInfoEntity, CbbTerminalTypeEnums.VDI_LINUX);
                times = 1;

            }
        };
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setId(UUID.randomUUID());
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        return upgradeEntity;
    }

    private TerminalEntity buildTerminalEntity() {
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setPlatform(CbbTerminalPlatformEnums.VDI);
        terminalEntity.setTerminalOsType("Linux");
        terminalEntity.setTerminalId("123");

        return terminalEntity;
    }

    private CbbDispatcherRequest buildRequest(UUID taskId, CbbSystemUpgradeStateEnums state) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        SystemUpgradeResultInfo resultInfo = new SystemUpgradeResultInfo();
        resultInfo.setTaskId(taskId);
        resultInfo.setUpgradeState(state);
        request.setData(JSON.toJSONString(resultInfo));
        request.setTerminalId("123");
        return request;
    }
}
