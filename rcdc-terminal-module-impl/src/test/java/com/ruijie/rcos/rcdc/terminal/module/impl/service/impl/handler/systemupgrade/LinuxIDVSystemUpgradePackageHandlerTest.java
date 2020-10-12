package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.google.common.collect.Lists;
import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import mockit.*;
import org.apache.commons.exec.DefaultExecutor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/17 20:11
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class LinuxIDVSystemUpgradePackageHandlerTest {

    @Tested
    private LinuxIDVSystemUpgradePackageHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private LinuxIDVSystemUpgradePackageHelper helper;

    @Injectable
    private NetworkAPI networkAPI;

    @Injectable
    private BtClientAPI btClientAPI;
    
    @Injectable
    private BtClientService btClientService;

    @Mocked
    private ShellCommandRunner shellCommandRunner;

    @Mocked
    private DefaultExecutor defaultExecutor;

    @Mocked
    private ByteArrayOutputStream byteArrayOutputStream;

    @After
    public void after() {
        String rootPath = this.getClass().getResource("/").getPath();
        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        new File(isoFilePath).delete();
    }

    /**
     * 系统升级包上传前处理方法
     */
    @Test
    public void testPreUploadPackage() {
        handler.preUploadPackage();
    }

    /**
     * 系统升级包上传后处理方法
     */
    @Test
    public void testPostUploadPackage() {
        handler.postUploadPackage();
    }
    
    /**
     * 获取systemUpgradePackageService
     */
    @Test
    public void testGetSystemUpgradePackageService() {
        TerminalSystemUpgradePackageService systemUpgradePackageService = handler.getSystemUpgradePackageService();
        Assert.assertEquals(terminalSystemUpgradePackageService, systemUpgradePackageService);
    }

    /**
     * 获取Linux IDV升级包存放路径
     */
    @Test
    public void testGetUpgradePackageFileDir() {
        String fileDir = handler.getUpgradePackageFileDir();
        Assert.assertEquals(Constants.TERMINAL_UPGRADE_OTA_LINUX_IDV_AND_ANDROID_VDI_DIR, fileDir);
    }

    /**
     * 获取升级包信息，正常流程
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testGetPackageInfo() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        String otaListPath = rootPath + "ots.list";

        Properties prop = new Properties();
        prop.setProperty("plat", "IDV");
        prop.setProperty("version", "1.0");

        List<String> otaFileInfoList = Lists.newArrayList();
        otaFileInfoList.add("packageMD5 /rainos-img.squashfs");
        otaFileInfoList.add("scriptMD5 /OTAPreRunFun.bash");

        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                new LinuxIDVSystemUpgradePackageHelper.OtaFileInfo("filePath", "md5");

        SeedFileInfoDTO seedFileInfoDTO = new SeedFileInfoDTO("seedFilePath", "seedFileMD5");

        new Expectations(FileOperateUtil.class) {
            {
                helper.getVersionProperties(versionFilePath);
                result = prop;
                helper.getOtaFilesInfo(otaListPath);
                result = otaFileInfoList;
                helper.handleOtaListItem(anyString, anyString, anyString);
                result = otaFileInfo;
                btClientService.makeBtSeed(anyString, anyString);
                result = seedFileInfoDTO;
                FileOperateUtil.deleteFile((File) any);
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }

            @Mock
            String getOtaListPath() {
                return otaListPath;
            }
        };

        new MockUp<File>() {
            @Mock
            boolean isDirectory() {
                return true;
            }

            @Mock
            boolean mkdirs() {
                return true;
            }
        };

        TerminalUpgradeVersionFileInfo packageInfo = handler.getPackageInfo(isoFile.getName(), isoFile.getPath());
        Assert.assertEquals("md5", packageInfo.getFileMD5());
        Assert.assertEquals("md5", packageInfo.getOtaScriptMD5());
        Assert.assertEquals("filePath", packageInfo.getFilePath());
        Assert.assertEquals("filePath", packageInfo.getOtaScriptPath());
        Assert.assertEquals("seedFilePath", packageInfo.getSeedLink());
        Assert.assertEquals("seedFileMD5", packageInfo.getSeedMD5());
    }

    /**
     * 获取升级包信息，镜像格式错误
     * @throws BusinessException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoIsoFormatError() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.exe";
        File isoFile = new File(isoFilePath);

        handler.getPackageInfo(isoFile.getName(), isoFile.getPath());

        Assert.fail();
    }

    /**
     * 获取升级包信息，升级包类型错误
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoPackageTypeNotMatch() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";

        Properties prop = new Properties();
        prop.setProperty("plat", "VDI");
        prop.setProperty("version", "1.0");

        new Expectations() {
            {
                helper.getVersionProperties(versionFilePath);
                result = prop;
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }
        };

        new MockUp<File>() {
            @Mock
            boolean isDirectory() {
                return true;
            }
        };

        handler.getPackageInfo(isoFile.getName(), isoFile.getPath());

        Assert.fail();
    }

    /**
     * 获取升级包信息，升级包类型为空
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoPackageTypeNull() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";

        Properties prop = new Properties();
        prop.setProperty("version", "1.0");

        new Expectations() {
            {
                helper.getVersionProperties(versionFilePath);
                result = prop;
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }
        };

        new MockUp<File>() {
            @Mock
            boolean isDirectory() {
                return true;
            }
        };

        handler.getPackageInfo(isoFile.getName(), isoFile.getPath());

        Assert.fail();
    }

    /**
     * 获取升级包信息，OTA文件列表内容错误
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoOtaListContentError() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        String otaListPath = rootPath + "ots.list";

        Properties prop = new Properties();
        prop.setProperty("plat", "IDV");
        prop.setProperty("version", "1.0");

        List<String> otaFileInfoList = Lists.newArrayList();
        otaFileInfoList.add("packageMD5 /rainos-img");
        otaFileInfoList.add("scriptMD5 /OTAPreRunFun");

        new Expectations() {
            {
                helper.getVersionProperties(versionFilePath);
                result = prop;
                helper.getOtaFilesInfo(otaListPath);
                result = otaFileInfoList;
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }

            @Mock
            String getOtaListPath() {
                return otaListPath;
            }
        };

        new MockUp<File>() {
            @Mock
            boolean isDirectory() {
                return true;
            }
        };

        handler.getPackageInfo(isoFile.getName(), isoFile.getPath());

        Assert.fail();
    }

    /**
     * 获取升级包信息，OTA文件列表项数量错误
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPackageInfoOtaListItemQualityError() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        String otaListPath = rootPath + "ots.list";

        Properties prop = new Properties();
        prop.setProperty("plat", "IDV");
        prop.setProperty("version", "1.0");

        List<String> otaFileInfoList = Lists.newArrayList();
        otaFileInfoList.add("packageMD5 /rainos-img");
        otaFileInfoList.add("scriptMD5 /OTAPreRunFun");
        otaFileInfoList.add("scriptMD5 /OTAPreRunFun");

        new Expectations() {
            {
                helper.getVersionProperties(versionFilePath);
                result = prop;
                helper.getOtaFilesInfo(otaListPath);
                result = otaFileInfoList;
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }

            @Mock
            String getOtaListPath() {
                return otaListPath;
            }
        };

        new MockUp<File>() {
            @Mock
            boolean isDirectory() {
                return true;
            }
        };

        handler.getPackageInfo(isoFile.getName(), isoFile.getPath());

        Assert.fail();
    }

    /**
     * 获取升级包信息，正常流程，出厂包
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test
    public void testGetPackageInfoInitPackage() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        String otaListPath = rootPath + "ots.list";

        Properties prop = new Properties();
        prop.setProperty("plat", "IDV");
        prop.setProperty("version", "1.0");

        List<String> otaFileInfoList = Lists.newArrayList();
        otaFileInfoList.add("packageMD5 /rainos-img.squashfs");
        otaFileInfoList.add("scriptMD5 /OTAPreRunFun.bash");

        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                new LinuxIDVSystemUpgradePackageHelper.OtaFileInfo("filePath", "md5");
        SeedFileInfoDTO seedFileInfoDTO = new SeedFileInfoDTO("seedFilePath", "seedFileMD5");

        new Expectations() {
            {
                helper.handleOtaListItem(anyString, anyString, anyString);
                result = otaFileInfo;
                helper.getVersionProperties(versionFilePath);
                result = prop;
                helper.getOtaFilesInfo(otaListPath);
                result = otaFileInfoList;
                btClientService.makeBtSeed(anyString, anyString);
                result = seedFileInfoDTO;
            }
        };

        new MockUp<ByteArrayOutputStream>() {
            @Mock
            public String toString() {
                return "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }

            @Mock
            String getOtaListPath() {
                return otaListPath;
            }
        };

        new MockUp<File>() {
            @Mock
            boolean isDirectory() {
                return false;
            }

            @Mock
            boolean mkdirs() {
                return true;
            }
        };

        TerminalUpgradeVersionFileInfo packageInfo = handler.getPackageInfo(isoFile.getName(),
                Constants.TERMINAL_UPGRADE_LINUX_IDV_OTA);

        Assert.assertEquals("md5", packageInfo.getFileMD5());
        Assert.assertEquals("md5", packageInfo.getOtaScriptMD5());
        Assert.assertEquals("filePath", packageInfo.getFilePath());
        Assert.assertEquals("filePath", packageInfo.getOtaScriptPath());
        Assert.assertEquals("seedFilePath", packageInfo.getSeedLink());
        Assert.assertEquals("seedFileMD5", packageInfo.getSeedMD5());
    }
}