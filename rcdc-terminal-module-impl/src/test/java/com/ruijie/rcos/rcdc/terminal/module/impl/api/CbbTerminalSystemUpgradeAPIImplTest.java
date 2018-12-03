package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.fail;
import java.io.File;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TermianlSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.FilePropertyInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.CmdExecuteUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
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
    private TermianlSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

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

        // 将文件挂载炒作全部mock
        ChunkUploadFile file = new ChunkUploadFile();
        file.setFileMD5("md5");
        file.setFileName("aaa.iso");
        file.setFilePath("/usr/data");

        new Expectations() {
            {
                terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion((TerminalUpgradeVersionFileInfo) any);
            }
        };

        new MockUp<CmdExecuteUtil>() {

            @Mock
            public void executeCmd(String cmd) {

            }

        };

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {

            @Mock
            private TerminalUpgradeVersionFileInfo getVersionInfo(String verContent) {
                TerminalUpgradeVersionFileInfo info = new TerminalUpgradeVersionFileInfo();
                info.setInternalVersion("111");
                info.setPackageName("222");
                info.setPackageType(CbbTerminalTypeEnums.VDI);
                info.setExternalVersion("444");
                
                return info;
            }

            @Mock
            private FilePropertyInfo getFileProperty(String filePath) {
                FilePropertyInfo info = new FilePropertyInfo();
                info.setMd5("md5");
                info.setFileType("iso");
                return info;
            }
        };
        
        new MockUp<FileOperateUtil>() {
            
            @Mock
            public void deleteFile(final String directoryPath, final String exceptFileName) {
                
            }
            
            @Mock
            public void moveFile(final String fileName, final String filePath, final String destPath) {
                
            }
            
            @Mock
            public String getSmallFileContent(final String filePath)  {
                return "123";
            }
        };

        cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(file);

        new Verifications() {
            {

                terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };

    }


    /**
     * 测试升级包上传md5校验不正确
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileMD5Incorrect() throws BusinessException {

        // 将文件挂载炒作全部mock
        ChunkUploadFile file = new ChunkUploadFile();
        file.setFileMD5("md6");
        file.setFileName("aaa.iso");
        file.setFilePath("/usr/data");

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {

            @Mock
            private FilePropertyInfo getFileProperty(String filePath) {
                FilePropertyInfo info = new FilePropertyInfo();
                info.setMd5("md5");
                info.setFileType("iso");
                return info;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(file);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_COMPLETE_CHECK_FAIL, e.getKey());
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
        ChunkUploadFile file = new ChunkUploadFile();
        file.setFileMD5("md5");
        file.setFileName("aaa.iso");
        file.setFilePath("/usr/data");

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {

            @Mock
            private FilePropertyInfo getFileProperty(String filePath) {
                FilePropertyInfo info = new FilePropertyInfo();
                info.setMd5("md5");
                info.setFileType("aaa");
                return info;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(file);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }

    }

    /**
     * 测试升级包上传文件文件属性为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFilePropertyIsNull() throws BusinessException {

        final ChunkUploadFile file = new ChunkUploadFile();
        file.setFileMD5("md5");
        file.setFileName("aaa.iso");
        file.setFilePath("/usr/data");

        final FilePropertyInfo info = null;

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {

            @Mock
            private FilePropertyInfo getFileProperty(String filePath) {
                return info;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(file);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_COMPLETE_CHECK_FAIL, e.getKey());
        }

    }

    /**
     * 测试升级包上传文件文件属性MD5为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadUpgradeFileFilePropertyMD5IsEmpty() throws BusinessException {

        final ChunkUploadFile file = new ChunkUploadFile();
        file.setFileMD5("md5");
        file.setFileName("aaa.iso");
        file.setFilePath("/usr/data");

        final FilePropertyInfo info = new FilePropertyInfo();
        info.setFileType("aaa");

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {

            @Mock
            private FilePropertyInfo getFileProperty(String filePath) {
                return info;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(file);
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

        final ChunkUploadFile file = new ChunkUploadFile();
        file.setFileMD5("md5");
        file.setFileName("aaa.iso");
        file.setFilePath("/usr/data");

        final FilePropertyInfo info = new FilePropertyInfo();
        info.setMd5("md5");

        new MockUp<CbbTerminalSystemUpgradeAPIImpl>() {

            @Mock
            private FilePropertyInfo getFileProperty(String filePath) {
                return info;
            }
        };

        try {
            cbbTerminalSystemUpgradeAPIImpl.uploadUpgradeFile(file);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_COMPLETE_CHECK_FAIL, e.getKey());
        }

    }

}
