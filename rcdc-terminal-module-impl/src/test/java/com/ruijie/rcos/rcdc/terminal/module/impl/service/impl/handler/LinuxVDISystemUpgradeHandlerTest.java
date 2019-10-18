package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.api.CbbTerminalSystemUpgradePackageAPIImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.api.CbbTerminalSystemUpgradePackageAPIImplTest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

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
public class LinuxVDISystemUpgradeHandlerTest {

    @Tested
    private LinuxVDISystemUpgradeHandler handler;

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

    /**
     * 测试uploadUpgradeFile，文件类型错误
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileFileTypeError() {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.ds");
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
     */
    @Test
    public void testUploadUpgradeFileSystemUpgradePackageVersionFileNotFoundException() {
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
     * 测试uploadUpgradeFile，系统升级包版本文件IOException
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileSystemUpgradePackageVersionIOException() {
        new MockUp<FileInputStream>() {
            @Mock
            public void $init(String filePath) throws IOException {
                throw new IOException();
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
     */
    @Test
    public void testUploadUpgradeFileMountUpgradePackageFail() throws BusinessException {
        String path = LinuxVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<LinuxVDISystemUpgradeHandlerTest>() {
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
                return new String[0];
            }
        };

        new Expectations() {
            {
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
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
            assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_PACKAGE_FILE_ILLEGAL, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，镜像文件不存在
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileImgNoExist() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<CbbTerminalSystemUpgradePackageAPIImpl>() {
            @Mock
            public String getVersionFilePath() {
                return path;
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
     */
    @Test
    public void testUploadUpgradeFileImgNoExist1() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
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
     */
    @Test
    public void testUploadUpgradeFileVersionFail() {
        String path = CbbTerminalSystemUpgradePackageAPIImplTest.class.getResource("/").getPath() + "testVersion";
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
     * 测试uploadUpgradeFile，系统升级任务正在进行中
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileUpgrading() {
        String path = LinuxVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<LinuxVDISystemUpgradeHandler>() {
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

        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = new TerminalSystemUpgradePackageEntity();
                terminalSystemUpgradeService.hasSystemUpgradeInProgress((UUID) any);
                result = true;

            }
        };
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("sdsds.iso");
        request.setFilePath("dsdsd");
        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_IS_RUNNING, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，不支持的升级包
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileUnSupportUpgradePackage() {
        String path = LinuxVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersionTypeError";
        new MockUp<LinuxVDISystemUpgradeHandler>() {
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
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_INCORRECT, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，刷机包移动失败
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFileMoveUpgradePackageFail() {
        String path = LinuxVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public void move(File from, File to) throws IOException {
                throw new IOException();
            }
        };
        new MockUp<LinuxVDISystemUpgradeHandler>() {
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
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_UPLOAD_FILE_FAIL, e.getKey());
        }
    }

    /**
     * 测试uploadUpgradeFile，
     *
     * @param util mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testUploadUpgradeFile(@Mocked FileOperateUtil util) throws BusinessException {
        String path = LinuxVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersion";
        new MockUp<Files>() {
            @Mock
            public void move(File from, File to) throws IOException {

            }
        };
        new MockUp<LinuxVDISystemUpgradeHandler>() {
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
        handler.uploadUpgradePackage(request);
        new Verifications() {
            {
                terminalSystemUpgradePackageService.saveTerminalUpgradePackage((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };
    }
}
