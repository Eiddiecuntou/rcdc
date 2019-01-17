package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.fail;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.FileCopyUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.CmdExecuteUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
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
    private CbbTerminalSystemUpgradeAPIImpl cbbTerminalSystemUpgradeAPIImpl;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private SystemUpgradeTaskManager systemUpgradeTaskManager;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;
    
 
    /**
     * 测试升级包上传
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFile() throws BusinessException {

        // 将文件挂载操作全部mock
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");

        new Expectations() {
            {
                terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion((TerminalUpgradeVersionFileInfo) any);
            }
        };

        new MockUp<CmdExecuteUtil>() {
            @Mock
            public void executeCmd(String cmd) {}
        };

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            private TerminalUpgradeVersionFileInfo getVersionInfo() {
                TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
                versionInfo.setImgName("package");
                versionInfo.setPackageType(TerminalPlatformEnums.IDV_LINUX_HARDWARE);
                versionInfo.setVersion("interVer");
                return versionInfo;
            }
        };
        new MockUp<FileOperateUtil>() {
            @Mock
            public void deleteFile(final String directoryPath, final String exceptFileName) {}
        };
        new MockUp<FileCopyUtils>() {
            @Mock
            public int copy(File in, File out) {
                return 10;
            }
        };
        cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
        new Verifications() {
            {
                terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };

    }

    /**
     * 测试升级包上传文件类型校验不正确
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileTaskIsUpgrading() throws BusinessException {

        // 将文件挂载炒作全部mock
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.ios");
        request.setFilePath("/usr/data");

        new Expectations() {
            {
                systemUpgradeTaskManager.countUpgradingNum();
                result = 1;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, e.getKey());
        }

    }

    /**
     * 测试升级包上传文件类型校验不正确
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeIncorrect() throws BusinessException {

        // 将文件挂载炒作全部mock
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.ios");
        request.setFilePath("/usr/data");

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }

    }


    /**
     * 测试升级包上传文件文件属性MD5为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileMD5CheckFail() throws BusinessException {

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa.iso");
        request.setFilePath("/usr/data");

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            public boolean isComplete(String md5) {
                return false;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_COMPLETE_CHECK_FAIL, e.getKey());
        }

    }

    /**
     * 测试升级包上传文件文件属性为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeIsNull() throws BusinessException {

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileMD5("md5");
        request.setFileName("aaa");
        request.setFilePath("/usr/data");

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(request);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }

    }

    @Test
    public void testListSystemUpgradePackage() throws BusinessException {
        CbbTerminalSystemUpgradePackageListRequest request = new CbbTerminalSystemUpgradePackageListRequest();
        request.setPaltform(TerminalPlatformEnums.IDV_LINUX_HARDWARE);
        List<TerminalSystemUpgradePackageEntity> packageList = buildPackageList();

        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                result = packageList;
            }
        };

        cbbTerminalSystemUpgradeAPIImpl.listSystemUpgradePackage(request);

        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                times = 1;
            }
        };

    }

    @Test
    public void testListSystemUpgradePackageFindEmpty() throws BusinessException {
        CbbTerminalSystemUpgradePackageListRequest request = new CbbTerminalSystemUpgradePackageListRequest();
        request.setPaltform(TerminalPlatformEnums.ALL);

        new Expectations() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                result = Collections.emptyList();
            }
        };

        cbbTerminalSystemUpgradeAPIImpl.listSystemUpgradePackage(request);

        new Verifications() {
            {
                termianlSystemUpgradePackageDAO.findByPackageType((TerminalPlatformEnums) any);
                times = 1;
            }
        };
    }
    
    @Test
    public void testAddSystemUpgradeTask() throws BusinessException {

        CbbAddTerminalSystemUpgradeTaskRequest request = new CbbAddTerminalSystemUpgradeTaskRequest();
        request.setPlatform(TerminalPlatformEnums.VDI_LINUX_HARDWARE);
        request.setTerminalId("11");
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        TerminalEntity terminal = new TerminalEntity();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                result = entity;
                
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = terminal;
            }
        };
        
        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {
            @Mock
            public void addTask(TerminalSystemUpgradePackageEntity upgradePackage, TerminalEntity terminal) {
            }

        };
        

        DefaultResponse resp = cbbTerminalSystemUpgradeAPIImpl.addSystemUpgradeTask(request);
        Assert.assertEquals(resp.getStatus(), Response.Status.SUCCESS);
    }


    private List<TerminalSystemUpgradePackageEntity> buildPackageList() {
        List<TerminalSystemUpgradePackageEntity> packageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
            packageList.add(entity);
        }
        return packageList;
    }

    private List<TerminalEntity> buildTerminalList() {
        List<TerminalEntity> terminalList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TerminalEntity entity = new TerminalEntity();
            terminalList.add(entity);
        }
        return terminalList;
    }


}
