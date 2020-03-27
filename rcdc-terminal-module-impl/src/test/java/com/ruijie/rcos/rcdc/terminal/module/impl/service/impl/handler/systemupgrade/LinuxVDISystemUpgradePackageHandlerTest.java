package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.api.CbbTerminalSystemUpgradePackageAPIImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.api.CbbTerminalSystemUpgradePackageAPIImplTest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.util.IsoFileUtil;
import mockit.*;
import org.apache.commons.exec.DefaultExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class LinuxVDISystemUpgradePackageHandlerTest {

    @Tested
    private LinuxVDISystemUpgradePackageHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Injectable
    private TerminalSystemUpgradeService terminalSystemUpgradeService;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private NetworkAPI networkAPI;

    @Injectable
    private BtClientAPI btClientAPI;

    @Mocked
    private ShellCommandRunner shellCommandRunner;
    
    @Mocked
    private DefaultExecutor defaultExecutor;

    @Mocked
    private ByteArrayOutputStream byteArrayOutputStream;
    
    /**
     * 测试uploadUpgradeFile，文件类型错误
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeError() {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.ds");
        request.setFilePath("/aaa/bbb");
        request.setFileMD5("123");
        request.setTerminalType(CbbTerminalTypeEnums.VDI_LINUX);
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_TYPE_ERROR, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，系统升级包版本文件不存在
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileSystemUpgradePackageVersionFileNotFoundException() throws BusinessException, IOException {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("/temp");

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，系统升级包版本文件IOException
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileSystemUpgradePackageVersionIOException() throws BusinessException, IOException {
        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<AbstractSystemUpgradePackageHandler>() {
            @Mock
            void checkISOMd5(String filePath) throws BusinessException {
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("/temp");

        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，挂载升级包失败
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileMountUpgradePackageFail() throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<LinuxVDISystemUpgradePackageHandlerTest>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<AbstractSystemUpgradePackageHandler>() {
            @Mock
            void checkISOMd5(String filePath) throws BusinessException {
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                return new String[0];
            }
        };

        new Expectations(IsoFileUtil.class) {
            {
                IsoFileUtil.mountISOFile(anyString, anyString);
                result = new BusinessException("key");
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("/temp");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件不存在
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileImgNoExist() throws BusinessException, IOException {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("/temp");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件不存在
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileImgNoExist1() throws BusinessException, IOException {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<AbstractSystemUpgradePackageHandler>() {
            @Mock
            void checkISOMd5(String filePath) throws BusinessException {
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                return new String[0];
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("/temp");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，Version错误
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileVersionFail() throws BusinessException, IOException {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "";
                return fileArr;
            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，不支持的升级包
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileUnSupportUpgradePackage() throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersionTypeError";
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<AbstractSystemUpgradePackageHandler>() {
            @Mock
            void checkISOMd5(String filePath) throws BusinessException {
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，刷机包移动失败
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileMoveUpgradePackageFail() throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public Path move(Path from, Path to, CopyOption... options) throws IOException {
                throw new IOException();
            }
        };
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件名获取异常
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileGetImgNameException() throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public Path move(Path from, Path to, CopyOption... options) throws IOException {
                throw new IOException();
            }
        };
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "";
                return fileArr;
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件名获取异常
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileGetImgNameException2() throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public Path move(Path from, Path to, CopyOption... options) throws IOException {
                throw new IOException();
            }
        };
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                return new String[0];
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件名获取异常
     *
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFileGetImgNameException3() throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public Path move(Path from, Path to, CopyOption... options) throws IOException {
                throw new IOException();
            }
        };
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "";
                return fileArr;
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，
     *
     * @param util mock对象
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testUploadUpgradeFile(@Mocked FileOperateUtil util) throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };
        new MockUp<Files>() {
            @Mock
            public Path move(Path from, Path to, CopyOption... options) throws IOException {
                // for test
                return null;
            }
        };
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("/aaa/sdsds.iso");
        handler.uploadUpgradePackage(request);
        new Verifications() {
            {
                terminalSystemUpgradePackageService.saveTerminalUpgradePackage((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };
    }
    
    /**
     * 测试uploadUpgradeFile，MD5值校验抛IO异常
     *
     * @param util mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileWithCheckMD5Error(@Mocked FileOperateUtil util) throws BusinessException, IOException {
        String path = LinuxVDISystemUpgradePackageHandlerTest.class.getResource("/").getPath() + "testVersion";

        new MockUp<Files>() {
            @Mock
            public Path move(Path from, Path to, CopyOption... options) throws IOException {
                // for test
                return null;
            }
        };
        new MockUp<LinuxVDISystemUpgradePackageHandler>() {
            @Mock
            public String getVersionFilePath() {
                return path;
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public String[] list() {
                String[] fileArr = new String[1];
                fileArr[0] = "dfd";
                return fileArr;
            }
        };

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_MD5_CHECK_ERROR, e.getKey());
        }
        new Verifications() {
            {
                terminalSystemUpgradePackageService.saveTerminalUpgradePackage((TerminalUpgradeVersionFileInfo) any);
                times = 0;
            }
        };
    }
}
