package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUpgradeableTerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.*;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.UpgradeTerminalLockManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QuerySystemUpgradeTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.QueryUpgradeableTerminalListService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeSupportService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.SystemUpgradeFileClearHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

/**
 * 
 * Description: 终端系统升级api测试类 Copyright: Copyright (c) 2018 Company: Ruijie Co.,
 * Ltd. Create Time: 2018年11月28日
 * 
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
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
    private SystemUpgradeFileClearHandler upgradeFileClearHandler;

    @Injectable
    private UpgradeTerminalLockManager lockManager;

    @Injectable
    private QueryUpgradeableTerminalListService upgradeableTerminalListService;

    /**
     * 测试升级包上传，参数为空
     * 
     * @throws Exception
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.addSystemUpgradeTask(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试升级包上传，系统升级包不存在
     * 
     * @throws Exception
     *             异常
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
    public void testAddSystemUpgradeTaskTerminalNumExceedLimit() {
        String[] terminalIdArr = new String[501];

        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();
        request.setTerminalIdArr(terminalIdArr);
        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        upgradePackageEntity.setIsDelete(false);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.of(upgradePackageEntity);
            }
        };

        try {
            upgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NUM_EXCEED_LIMIT, e.getKey());
        }

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                times = 1;
            }
        };
    }

    /**
     * 测试升级包上传，升级包正在上传中
     */
    @Test
    public void testAddSystemUpgradeTaskUpgradePackageIsUploading() {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();
        request.setTerminalIdArr(new String[] { "123", "456" });
        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        upgradePackageEntity.setIsDelete(false);
        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(true);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.of(upgradePackageEntity);
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalTypeRequest) any);
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
        request.setTerminalIdArr(new String[] { "123", "456" });

        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        upgradePackageEntity.setIsDelete(false);
        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(false);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.of(upgradePackageEntity);
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalTypeRequest) any);
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
     * 测试升级包上传，升级包文件不存在
     */
    @Test
    public void testAddSystemUpgradeTaskPackageNotExist() {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();
        request.setTerminalIdArr(new String[] { "123", "456" });

        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        upgradePackageEntity.setIsDelete(false);
        upgradePackageEntity.setFilePath("aaa");
        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(false);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = Optional.of(upgradePackageEntity);
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalTypeRequest) any);
                result = response;
                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getPackageId());
                result = false;
            }
        };

        new MockUp<File>() {
            @Mock
            public Path toPath() {
                // file toPath 返回null
                return null;
            }
        };

        new MockUp<Files>() {
            @Mock
            public boolean exists(Path path, LinkOption... options) {
                return false;
            }
        };

        try {
            upgradeAPIImpl.addSystemUpgradeTask(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_FILE_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试升级包上传，
     * 
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTask() throws BusinessException {
        CbbAddSystemUpgradeTaskRequest request = new CbbAddSystemUpgradeTaskRequest();
        request.setTerminalIdArr(new String[] { "123", "456" });
        TerminalSystemUpgradePackageEntity upgradePackageEntity = new TerminalSystemUpgradePackageEntity();
        upgradePackageEntity.setIsDelete(false);
        upgradePackageEntity.setFilePath("/opt");
        Optional<TerminalSystemUpgradePackageEntity> upgradePackageOpt = Optional.of(upgradePackageEntity);

        CbbCheckUploadingResultResponse response = new CbbCheckUploadingResultResponse();
        response.setHasLoading(false);

        UUID upgradeTaskId = UUID.randomUUID();
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                result = upgradePackageOpt;
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalTypeRequest) any);
                result = response;
                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getPackageId());
                result = false;
                terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackageOpt.get(),
                        request.getTerminalIdArr());
                result = upgradeTaskId;
            }
        };

        new MockUp<Files>() {
            @Mock
            public boolean exists(Path path, LinkOption... options) {
                return true;
            }
        };

        CbbAddSystemUpgradeTaskResponse upgradeTaskResponse = upgradeAPIImpl.addSystemUpgradeTask(request);
        assertEquals(upgradeTaskId, upgradeTaskResponse.getUpgradeTaskId());
        assertEquals(upgradePackageOpt.get().getPackageName(), upgradeTaskResponse.getImgName());

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findById(request.getPackageId());
                times = 1;
                systemUpgradePackageAPI.isUpgradeFileUploading((CbbTerminalTypeRequest) any);
                times = 1;
                terminalSystemUpgradeService.hasSystemUpgradeInProgress(request.getPackageId());
                times = 1;
                terminalSystemUpgradeServiceTx.addSystemUpgradeTask(upgradePackageOpt.get(),
                        request.getTerminalIdArr());
                times = 1;
                terminalSystemUpgradeSupportService.openSystemUpgradeService(upgradePackageOpt.get());
                times = 1;
            }
        };
    }

    /**
     * 测试addSystemUpgradeTerminal，参数为空
     * 
     * @throws Exception
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.addSystemUpgradeTerminal(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试addSystemUpgradeTerminal，没有找到对应的终端
     */
    @Test
    public void testAddSystemUpgradeTerminalNotFoundTerminal() {
        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();

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
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTerminalUploadingTaskHasClose() throws BusinessException {
        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();

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
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTerminalUploadingTerminalExist() throws BusinessException {
        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();

        TerminalEntity terminal = new TerminalEntity();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = terminal;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                result = upgradeEntity;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(),
                        terminal.getTerminalId());
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
     * 测试addSystemUpgradeTerminal，刷机终端已达到限制
     *
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTerminalTerminalNumExceedLimit() throws BusinessException {
        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();

        TerminalEntity terminal = new TerminalEntity();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = terminal;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                result = upgradeEntity;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(),
                        terminal.getTerminalId());
                result = null;
                systemUpgradeTerminalDAO.countBySysUpgradeId(upgradeEntity.getId());
                result = 500;
            }
        };

        try {
            upgradeAPIImpl.addSystemUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NUM_EXCEED_LIMIT, e.getKey());
        }
    }

    /**
     * 测试addSystemUpgradeTerminal，
     * 
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testAddSystemUpgradeTerminal() throws BusinessException {
        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();

        TerminalEntity terminal = new TerminalEntity();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
                result = terminal;
                terminalSystemUpgradeService.getSystemUpgradeTask(request.getUpgradeTaskId());
                result = upgradeEntity;
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(),
                        terminal.getTerminalId());
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
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeEntity.getId(),
                        terminal.getTerminalId());
                times = 1;
            }
        };
    }

    /**
     * 测试listSystemUpgradeTask，参数为空
     * 
     * @throws Exception
     *             异常
     */
    @Test
    public void testListSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.listSystemUpgradeTask(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradeTask，
     * 
     * @param upgradeTaskPage
     *            mock对象
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testListSystemUpgradeTask(@Mocked Page<TerminalSystemUpgradeEntity> upgradeTaskPage)
            throws BusinessException {
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
     * @throws Exception
     *             异常
     */
    @Test
    public void testListSystemUpgradeTaskTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.listSystemUpgradeTaskTerminal(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，
     * 
     * @param upgradeTaskTerminalPage
     *            mock对象
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testListSystemUpgradeTaskTerminal(@Mocked Page<TerminalSystemUpgradeEntity> upgradeTaskTerminalPage)
            throws BusinessException {
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
        DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> response = upgradeAPIImpl
                .listSystemUpgradeTaskTerminal(request);
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
     * @throws Exception
     *             异常
     */
    @Test
    public void testCloseSystemUpgradeTaskArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.closeSystemUpgradeTask(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试closeSystemUpgradeTask，
     * 
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testCloseSystemUpgradeTask() throws BusinessException {
        IdRequest request = new IdRequest();
        DefaultResponse response = upgradeAPIImpl.closeSystemUpgradeTask(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                terminalSystemUpgradeServiceTx.closeSystemUpgradeTask(request.getId());
                times = 1;
            }
        };
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，参数为空
     * 
     * @throws Exception
     *             异常
     */
    @Test
    public void testListUpgradeableTerminalArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradeAPIImpl.listUpgradeableTerminal(null),
                "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，
     *
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testListUpgradeableTerminalMatchEqualsIsNotEmpty() throws BusinessException {
        CbbUpgradeableTerminalPageSearchRequest request = new CbbUpgradeableTerminalPageSearchRequest(
                new PageWebRequest());
        MatchEqual matchEqual = new MatchEqual();
        matchEqual.setName("packageId");
        matchEqual.setValueArr(new UUID[] { UUID.randomUUID() });
        request.setMatchEqualArr(new MatchEqual[] { matchEqual });

        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setIsDelete(false);
        packageEntity.setPackageType(TerminalTypeEnums.VDI_LINUX);

        List<ViewUpgradeableTerminalEntity> entityList = new ArrayList<>();
        entityList.add(new ViewUpgradeableTerminalEntity());
        Pageable pageable = PageRequest.of(1, 10);
        Page<ViewUpgradeableTerminalEntity> upgradeableTerminalPage = new PageImpl<>(entityList, pageable, 100);

        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findById((UUID) any);
                result = Optional.of(packageEntity);

                upgradeableTerminalListService.pageQuery((PageSearchRequest) any, ViewUpgradeableTerminalEntity.class);
                result = upgradeableTerminalPage;
            }
        };
        DefaultPageResponse<CbbUpgradeableTerminalListDTO> pageResponse = upgradeAPIImpl
                .listUpgradeableTerminal(request);
        assertEquals(Status.SUCCESS, pageResponse.getStatus());
        assertEquals(pageResponse.getTotal(), upgradeableTerminalPage.getTotalElements());
        assertEquals(pageResponse.getItemArr().length, upgradeableTerminalPage.getContent().size());

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findById((UUID) any);
                times = 1;

                upgradeableTerminalListService.pageQuery((PageSearchRequest) any, ViewUpgradeableTerminalEntity.class);
                times = 1;
            }
        };
    }

    /**
     * 测试listSystemUpgradeTaskTerminal，
     *
     * @throws BusinessException
     *             异常
     */
    @Test
    public void testListUpgradeableTerminal() throws BusinessException {
        CbbUpgradeableTerminalPageSearchRequest request = new CbbUpgradeableTerminalPageSearchRequest(
                new PageWebRequest());

        List<ViewUpgradeableTerminalEntity> entityList = new ArrayList<>();
        entityList.add(new ViewUpgradeableTerminalEntity());
        Pageable pageable = PageRequest.of(1, 10);
        Page<ViewUpgradeableTerminalEntity> upgradeableTerminalPage = new PageImpl<>(entityList, pageable, 100);

        new Expectations() {
            {
                upgradeableTerminalListService.pageQuery((PageSearchRequest) any, ViewUpgradeableTerminalEntity.class);
                result = upgradeableTerminalPage;
            }
        };
        DefaultPageResponse<CbbUpgradeableTerminalListDTO> pageResponse = upgradeAPIImpl
                .listUpgradeableTerminal(request);
        assertEquals(Status.SUCCESS, pageResponse.getStatus());
        assertEquals(pageResponse.getTotal(), upgradeableTerminalPage.getTotalElements());
        assertEquals(pageResponse.getItemArr().length, upgradeableTerminalPage.getContent().size());

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findById((UUID) any);
                times = 0;

                upgradeableTerminalListService.pageQuery((PageSearchRequest) any, ViewUpgradeableTerminalEntity.class);
                times = 1;
            }
        };
    }

    /**
     * 测试根据id获取升级任务信息
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testGetTerminalUpgradeTaskById() throws BusinessException {
        TerminalSystemUpgradeEntity upgradeTaskEntity = new TerminalSystemUpgradeEntity();
        upgradeTaskEntity.setId(UUID.randomUUID());
        upgradeTaskEntity.setState(CbbSystemUpgradeTaskStateEnums.FINISH);
        upgradeTaskEntity.setPackageName("aaa");
        new Expectations() {
            {
                terminalSystemUpgradeService.getSystemUpgradeTask((UUID) any);
                result = upgradeTaskEntity;
            }
        };

        CbbGetTerminalUpgradeTaskResponse response = upgradeAPIImpl.getTerminalUpgradeTaskById(new IdRequest());
        assertEquals(upgradeTaskEntity.getId(), response.getUpgradeTask().getId());
        assertEquals(upgradeTaskEntity.getPackageName(), response.getUpgradeTask().getPackageName());
        assertEquals(upgradeTaskEntity.getState(), response.getUpgradeTask().getUpgradeTaskState());

        new Verifications() {
            {
                terminalSystemUpgradeService.getSystemUpgradeTask((UUID) any);
                times = 1;
            }
        };
    }

    /**
     * 测试根据任务id获取升级任务终端列表 - 请求的升级终端状态为null，获取到的终端列表为null
     */
    @Test
    public void testGetUpgradeTerminalByTaskIdRequestStateIsNull() {
        CbbGetTaskUpgradeTerminalRequest request = new CbbGetTaskUpgradeTerminalRequest();
        request.setId(UUID.randomUUID());
        request.setTerminalState(null);

        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = null;

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findBySysUpgradeId(request.getId());
                result = null;
            }
        };

        CbbGetTaskUpgradeTerminalResponse response = upgradeAPIImpl.getUpgradeTerminalByTaskId(request);
        assertEquals(0, response.getUpgradeTerminalList().size());

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findBySysUpgradeId(request.getId());
                times = 1;

                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(request.getId(), (CbbSystemUpgradeStateEnums) any);
                times = 0;
            }
        };
    }

    /**
     * 测试根据任务id获取升级任务终端列表 - 请求的升级终端状态不为空
     */
    @Test
    public void testGetUpgradeTerminalByTaskId() {
        CbbGetTaskUpgradeTerminalRequest request = new CbbGetTaskUpgradeTerminalRequest();
        request.setId(UUID.randomUUID());
        request.setTerminalState(CbbSystemUpgradeStateEnums.SUCCESS);

        List<TerminalSystemUpgradeTerminalEntity> upgradeTerminalList = Lists.newArrayList();
        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();
        entity.setTerminalId("123");
        entity.setId(UUID.randomUUID());
        entity.setState(CbbSystemUpgradeStateEnums.SUCCESS);
        upgradeTerminalList.add(entity);

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(request.getId(),
                        CbbSystemUpgradeStateEnums.SUCCESS);
                result = upgradeTerminalList;
            }
        };

        CbbGetTaskUpgradeTerminalResponse response = upgradeAPIImpl.getUpgradeTerminalByTaskId(request);
        assertEquals(1, response.getUpgradeTerminalList().size());
        assertEquals(entity.getTerminalId(), response.getUpgradeTerminalList().get(0).getTerminalId());
        assertEquals(entity.getId(), response.getUpgradeTerminalList().get(0).getId());
        assertEquals(entity.getState(), response.getUpgradeTerminalList().get(0).getTerminalUpgradeState());

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findBySysUpgradeId(request.getId());
                times = 0;

                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(request.getId(),
                        CbbSystemUpgradeStateEnums.SUCCESS);
                times = 1;
            }
        };
    }

    /**
     * 测试取消终端刷机 - 刷机终端不存在
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testCancelUpgradeTerminalTaskUpgradeTerminalNotExist() throws BusinessException {

        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();
        request.setUpgradeTaskId(UUID.randomUUID());
        request.setTerminalId("aaa");

        Lock lock = new ReentrantLock();

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                result = null;

                lockManager.getAndCreateLock(request.getTerminalId());
                result = lock;

            }
        };

        try {
            upgradeAPIImpl.cancelUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST, e.getKey());
        }

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                times = 1;

                terminalSystemUpgradeServiceTx
                        .modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

                lockManager.getAndCreateLock(request.getTerminalId());
                times = 2;
            }
        };
    }

    /**
     * 测试取消终端刷机 - 刷机终端状态不可取消
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testCancelUpgradeTerminalTaskUpgradeTerminalStateNotAllowCancel() throws BusinessException {

        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();
        request.setUpgradeTaskId(UUID.randomUUID());
        request.setTerminalId("aaa");

        Lock lock = new ReentrantLock();

        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setId(UUID.randomUUID());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                result = upgradeTerminal;

                lockManager.getAndCreateLock(request.getTerminalId());
                result = lock;

            }
        };

        try {
            upgradeAPIImpl.cancelUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_CANCEL, e.getKey());
        }

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                times = 1;

                terminalSystemUpgradeServiceTx
                        .modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;

                lockManager.getAndCreateLock(request.getTerminalId());
                times = 2;
            }
        };
    }

    /**
     * 测试取消终端刷机
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testCancelUpgradeTerminalTask() throws BusinessException {

        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();
        request.setUpgradeTaskId(UUID.randomUUID());
        request.setTerminalId("aaa");

        Lock lock = new ReentrantLock();

        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setId(UUID.randomUUID());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                result = upgradeTerminal;

                lockManager.getAndCreateLock(request.getTerminalId());
                result = lock;

                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);

                basicInfoDAO.getTerminalNameByTerminalId(request.getTerminalId());
                result = "bbb";

            }
        };

        CbbTerminalNameResponse response = upgradeAPIImpl.cancelUpgradeTerminal(request);
        assertEquals("bbb", response.getTerminalName());

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                times = 1;

                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);
                times = 1;

                lockManager.getAndCreateLock(request.getTerminalId());
                times = 2;

                basicInfoDAO.getTerminalNameByTerminalId(request.getTerminalId());
                times = 1;
            }
        };
    }

    /**
     * 测试重试终端刷机 - 刷机终端状态不可取消
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testRetryUpgradeTerminalTaskUpgradeTerminalStateNotAllowRetry() throws BusinessException {

        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();
        request.setUpgradeTaskId(UUID.randomUUID());
        request.setTerminalId("aaa");

        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setId(UUID.randomUUID());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.WAIT);

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                result = upgradeTerminal;

            }
        };

        try {
            upgradeAPIImpl.retryUpgradeTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_STATE_NOT_ALLOW_RETRY, e.getKey());
        }

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                times = 1;

                terminalSystemUpgradeServiceTx
                        .modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试重试终端刷机 - 服务端刷机终端状态文件不存在
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testRetryUpgradeTerminalTaskUpgradeTerminalStartFileNotExist() throws BusinessException {

        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();
        request.setUpgradeTaskId(UUID.randomUUID());
        request.setTerminalId("aaa");

        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setId(UUID.randomUUID());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.FAIL);

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                result = upgradeTerminal;

                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);

                basicInfoDAO.getTerminalNameByTerminalId(request.getTerminalId());
                result = "ccc";
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean isFile() {
                return false;
            }
        };

        CbbTerminalNameResponse response = upgradeAPIImpl.retryUpgradeTerminal(request);
        assertEquals("ccc", response.getTerminalName());

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                times = 1;

                terminalSystemUpgradeServiceTx
                        .modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;

                basicInfoDAO.getTerminalNameByTerminalId(request.getTerminalId());
                times = 1;
            }
        };
    }

    /**
     * 测试重试终端刷机 - 服务端刷机终端状态文件已存在
     *
     * @throws BusinessException
     *             业务异常
     */
    @Test
    public void testRetryUpgradeTerminalTaskUpgradeTerminalStartFileExist() throws BusinessException {

        CbbUpgradeTerminalRequest request = new CbbUpgradeTerminalRequest();
        request.setUpgradeTaskId(UUID.randomUUID());
        request.setTerminalId("aaa");

        TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
        upgradeTerminal.setId(UUID.randomUUID());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.FAIL);

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                result = upgradeTerminal;

                terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(upgradeTerminal);

                basicInfoDAO.getTerminalNameByTerminalId(request.getTerminalId());
                result = "ccc";
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        CbbTerminalNameResponse response = upgradeAPIImpl.retryUpgradeTerminal(request);
        assertEquals("ccc", response.getTerminalName());
        assertEquals(CbbSystemUpgradeStateEnums.UPGRADING, upgradeTerminal.getState());

        new Verifications() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(request.getUpgradeTaskId(),
                        request.getTerminalId());
                times = 1;

                terminalSystemUpgradeServiceTx
                        .modifySystemUpgradeTerminalState((TerminalSystemUpgradeTerminalEntity) any);
                times = 1;

                basicInfoDAO.getTerminalNameByTerminalId(request.getTerminalId());
                times = 1;
            }
        };
    }
}
