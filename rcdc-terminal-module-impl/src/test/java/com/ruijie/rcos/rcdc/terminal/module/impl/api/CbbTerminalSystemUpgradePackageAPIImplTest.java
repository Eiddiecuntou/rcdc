package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalTypeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckUploadingResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.LinuxVDISystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class CbbTerminalSystemUpgradePackageAPIImplTest {

    @Tested
    private CbbTerminalSystemUpgradePackageAPIImpl upgradePackageAPIImpl;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Mocked
    private ShellCommandRunner shellCommandRunner;

    @Injectable
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    /**
     * 测试isUpgradeFileUploading，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testIsUpgradeFileUploadingArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.isUpgradeFileUploading(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试isUpgradeFileUploading，
     */
    @Test
    public void testIsUpgradeFileUploading() {
        Set<TerminalTypeEnums> uploadingSet =
                Deencapsulation.getField(CbbTerminalSystemUpgradePackageAPIImpl.class, "SYS_UPGRADE_PACKAGE_UPLOADING");
        uploadingSet.add(TerminalTypeEnums.VDI_LINUX);
        CbbTerminalTypeRequest request = new CbbTerminalTypeRequest();
        request.setTerminalType(TerminalTypeEnums.VDI_LINUX);
        CbbCheckUploadingResultResponse response = upgradePackageAPIImpl.isUpgradeFileUploading(request);
        assertTrue(response.isHasLoading());
        uploadingSet.clear();
    }

    /**
     * uploadUpgradePackage，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.uploadUpgradePackage(null), "request can not be null");
        assertTrue(true);
    }


    /**
     *测试uploadUpgradePackage，有升级包在上传
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageIsUploading() throws Exception {
        TerminalSystemUpgradeHandler handler = new LinuxVDISystemUpgradeHandler();
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("123.iso");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platType", "VDI");
        jsonObject.put("osType", "Linux");
        request.setCustomData(jsonObject);
        Set<TerminalTypeEnums> upgradePackageUploadnigSet = Deencapsulation.getField(upgradePackageAPIImpl, "SYS_UPGRADE_PACKAGE_UPLOADING");
        upgradePackageUploadnigSet.add(TerminalTypeEnums.VDI_LINUX);
        try {
            upgradePackageAPIImpl.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_IS_UPLOADING, e.getKey());
        }
        upgradePackageUploadnigSet.clear();

    }

    /**
     * 测试uploadUpgradePackage，没有升级包在上传
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackage() throws Exception {

        TerminalSystemUpgradeHandler handler = new LinuxVDISystemUpgradeHandler();
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("123.iso");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platType", "VDI");
        jsonObject.put("osType", "Linux");
        request.setCustomData(jsonObject);
        new Expectations() {
            {
                handlerFactory.getHandler((TerminalTypeEnums) any);
                result = handler;
            }
        };
        new MockUp<LinuxVDISystemUpgradeHandler>() {

            @Mock
            public void uploadUpgradePackage(CbbTerminalUpgradePackageUploadRequest request) {

            }
        };
        upgradePackageAPIImpl.uploadUpgradePackage(request);
        new Verifications() {
            {
                handlerFactory.getHandler((TerminalTypeEnums) any);
                times = 1;
                handler.uploadUpgradePackage(request);
                times = 1;
            }
        };

    }


    /**
     * 测试listSystemUpgradePackage，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testListSystemUpgradePackageArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> upgradePackageAPIImpl.listSystemUpgradePackage(null), "request can not be null");
        assertTrue(true);
    }

}
