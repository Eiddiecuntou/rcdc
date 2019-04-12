package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCloseSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbAddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QueryUpgradeableTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 终端系统升级api测试类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月28日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class CbbTerminalSystemUpgradeAPIImplTest {

    @Tested
    private CbbTerminalSystemUpgradeAPIImpl upgradeAPIImpl;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TerminalSystemUpgradeSupportService terminalSystemUpgradeSupportService;

    @Injectable
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private QuerySystemUpgradeListService querySystemUpgradeListService;

    @Injectable
    private QuerySystemUpgradeTerminalListService querySystemUpgradeTerminalListService;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private CbbTerminalSystemUpgradePackageAPI systemUpgradePackageAPI;

    @Injectable
    private QueryUpgradeableTerminalListService queryUpgradeableTerminalListDAO;

    /**
     * 测试升级包上传，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.addSystemUpgradeTask(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试升级包上传，系统升级包不存在
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddSystemUpgradeTaskUpgradePackageNotExist() {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();

        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.empty();
            }
        };
        try {
            upgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试升级包上传，升级包正在上传中
     */
    @Test
    public void testAddSystemUpgradeTaskUpgradePackageIsUploading() {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();

        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(true);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.of(upgradePackageEntity);
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalPlatformRequest) any);
                result = response;
            }
        };
        try {
            upgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING, e.getKey());
        }
    }

    /**
     * 测试升级包上传，已存在进行中的刷机任务
     */
    @Test
    public void testAddSystemUpgradeTaskHasUploadingTask() {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();

        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(false);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.of(upgradePackageEntity);
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalPlatformRequest) any);
                result = response;
                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getPackageId());
                result = true;
            }
        };
        try {
            upgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, e.getKey());
        }
    }

    /**
     * 测试升级包上传，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testAddSystemUpgradeTask() throws BusinessException {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();

        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt = Optional.of(upgradePackageEntity);

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(false);

        UUID upgradeTaskId = UUID.randomUUID();
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = upgradePackageOpt;
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalPlatformRequest) any);
                result = response;
                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getPackageId());
                result = false;
                terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackageOpt.get(), request.getTerminalIdArr());
                result = upgradeTaskId;
            }
        };
        CbbAddSystemUpgradeTaskResponse upgradeTaskResponse = upgradeAPIImpl.addSystemUpgradeTask(request);
        assertEquals(upgradeTaskId, upgradeTaskResponse.getUpgradeTaskId());
        assertEquals(upgradePackageOpt.get().getPackageName(), upgradeTaskResponse.getImgName());

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                times = 1;
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalPlatformRequest) any);
                times = 1;
                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getPackageId());
                times = 1;
                terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackageOpt.get(), request.getTerminalIdArr());
                times = 1;
                terminalSystemUpgradeSupportService.openSystemUpgradeService(upgradePackageOpt.get());
                times = 1;
            }
        };
    }

    /**
     * 测试addSystemUpgradeTerminal，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testAddSystemUpgradeTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.addSystemUpgradeTerminal(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试addSystemUpgradeTerminal，没有找到对应的终端
     */
    @Test
    public void testAddSystemUpgradeTerminalNotFoundTerminal() {
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = null;
            }
        };

        try {
            upgradeAPIImpl.addSystemUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }
    }

    /**
     * 测试addSystemUpgradeTerminal，刷机任务已关闭
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testAddSystemUpgradeTerminalUploadingTaskHasClose() throws BusinessException {
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();

        TerminalEntity terminal = new TerminalEntity();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.CLOSING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = terminal;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                result = upgradeEntity;
            }
        };

        try {
            upgradeAPIImpl.addSystemUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED, e.getKey());
        }
    }

    /**
     * 测试addSystemUpgradeTerminal，刷机任务终端已添加
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testAddSystemUpgradeTerminalUploadingTerminalExist() throws BusinessException {
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();

        TerminalEntity terminal = new TerminalEntity();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = terminal;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                result = upgradeEntity;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), terminal.getTerminalId());
                result = new TerminalSystemUpgradeTerminalEntity();
            }
        };

        try {
            upgradeAPIImpl.addSystemUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_EXIST, e.getKey());
        }
    }

    /**
     * 测试addSystemUpgradeTerminal，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testAddSystemUpgradeTerminal() throws BusinessException {
        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();

        TerminalEntity terminal = new TerminalEntity();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = terminal;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                result = upgradeEntity;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), terminal.getTerminalId());
                result = null;
            }
        };

        CbbTerminalNameResponse response = upgradeAPIImpl.addSystemUpgradeTerminal(request);
        assertEquals(terminal.getTerminalName(), response.getTerminalName());

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                times = 1;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                times = 1;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(), terminal.getTerminalId());
                times = 1;
            }
        };
    }

    /**
     * 测试listSystemUpgradeTask，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.listSystemUpgradeTask(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradeTask，
     * 
     * @param upgradeTaskPage mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testListSystemUpgradeTask(@Mocked Page<TerminalSystemUpgradeEntity> upgradeTaskPage) throws BusinessException {
        PageSearchRequest request = new PageSearchRequest();
        List<TerminalSystemUpgradeEntity> taskList = new ArrayList<>();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        taskList.add(upgradeEntity);

        new Expectations() {
            {
                querySystemUpgradeListService.pageQuery(request, TerminalSystemUpgradeEntity.class);
                result = upgradeTaskPage;
                upgradeTaskPage.getNumberOfElements();
                result = 1;
                upgradeTaskPage.getContent();
                result = taskList;
                upgradeTaskPage.getSize();
                result = 1;
                upgradeTaskPage.getTotalElements();
                result = 1;
            }
        };
        DefaultPageResponse<CbbSystemUpgradeTaskDTO> response = upgradeAPIImpl.listSystemUpgradeTask(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(upgradeEntity.getState(), response.getItemArr()[0].getUpgradeTaskState());
        assertEquals(upgradeEntity.getId(), response.getItemArr()[0].getId());

        new Verifications() {
            {
                querySystemUpgradeListService.pageQuery(request, TerminalSystemUpgradeEntity.class);
                times = 1;
                upgradeTaskPage.getNumberOfElements();
                times = 1;
                upgradeTaskPage.getContent();
                times = 1;
                upgradeTaskPage.getSize();
                times = 1;
                upgradeTaskPage.getTotalElements();
                times = 1;
            }
        };
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradeTaskTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.listSystemUpgradeTaskTerminal(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，
     * 
     * @param upgradeTaskTerminalPage mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testListSystemUpgradeTaskTerminal(@Mocked Page<TerminalSystemUpgradeEntity> upgradeTaskTerminalPage) throws BusinessException {
        PageSearchRequest request = new PageSearchRequest();
        List<TerminalSystemUpgradeTerminalEntity> taskList = new ArrayList<>();
        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();
        taskList.add(entity);

        new Expectations() {
            {
                querySystemUpgradeTerminalListService.pageQuery(request, TerminalSystemUpgradeTerminalEntity.class);
                result = upgradeTaskTerminalPage;
                upgradeTaskTerminalPage.getNumberOfElements();
                result = 1;
                upgradeTaskTerminalPage.getContent();
                result = taskList;
                upgradeTaskTerminalPage.getSize();
                result = 1;
                upgradeTaskTerminalPage.getTotalElements();
                result = 1;
            }
        };
        DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> response = upgradeAPIImpl.listSystemUpgradeTaskTerminal(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(entity.getState(), response.getItemArr()[0].getTerminalUpgradeState());
        assertEquals(entity.getId(), response.getItemArr()[0].getId());

        new Verifications() {
            {
                querySystemUpgradeTerminalListService.pageQuery(request, TerminalSystemUpgradeTerminalEntity.class);
                times = 1;
                upgradeTaskTerminalPage.getNumberOfElements();
                times = 1;
                upgradeTaskTerminalPage.getContent();
                times = 1;
                basicInfoDAO.findTerminalEntityByTerminalId(entity.getTerminalId());
                times = 1;
                upgradeTaskTerminalPage.getSize();
                times = 1;
                upgradeTaskTerminalPage.getTotalElements();
                times = 1;
            }
        };
    }

    /**
     * 测试closeSystemUpgradeTask，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCloseSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.closeSystemUpgradeTask(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试closeSystemUpgradeTask，
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testCloseSystemUpgradeTask() throws BusinessException {
        CbbCloseSystemUpgradeTaskRequest request = new CbbCloseSystemUpgradeTaskRequest();
        DefaultResponse response = upgradeAPIImpl.closeSystemUpgradeTask(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                terminalSystemUpgradeServiceTx.closeSystemUpgradeTask(request.getUpgradeTaskId());
                times = 1;
            }
        };
    }


    /**
     * 测试listSystemUpgradeTaskTerminal，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testListUpgradeableTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.listUpgradeableTerminal(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，
     * 
     * @param terminalListPage mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testListUpgradeableTerminal(@Mocked Page<ViewUpgradeableTerminalEntity> terminalListPage) throws BusinessException {
        PageSearchRequest request = new PageSearchRequest();
        List<ViewUpgradeableTerminalEntity> taskList = new ArrayList<>();
        ViewUpgradeableTerminalEntity entity = new ViewUpgradeableTerminalEntity();
        taskList.add(entity);

        new Expectations() {
            {
                queryUpgradeableTerminalListDAO.pageQuery(request, ViewUpgradeableTerminalEntity.class);
                result = terminalListPage;
                terminalListPage.getNumberOfElements();
                result = 1;
                terminalListPage.getContent();
                result = taskList;
                terminalListPage.getSize();
                result = 1;
                terminalListPage.getTotalElements();
                result = 1;
            }
        };
        DefaultPageResponse<TerminalListDTO> response = upgradeAPIImpl.listUpgradeableTerminal(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(entity.getState(), response.getItemArr()[0].getTerminalState());
        assertEquals(entity.getTerminalId(), response.getItemArr()[0].getId());

        new Verifications() {
            {
                queryUpgradeableTerminalListDAO.pageQuery(request, ViewUpgradeableTerminalEntity.class);
                times = 1;
                terminalListPage.getNumberOfElements();
                times = 1;
                terminalListPage.getContent();
                times = 1;
                terminalListPage.getSize();
                times = 1;
                terminalListPage.getTotalElements();
                times = 1;
            }
        };
    }
}
