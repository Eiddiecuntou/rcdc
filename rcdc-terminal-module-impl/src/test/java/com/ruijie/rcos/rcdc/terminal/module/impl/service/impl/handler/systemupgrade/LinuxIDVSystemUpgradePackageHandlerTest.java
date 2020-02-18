package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseMakeBtSeedRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseNetworkDTO;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.SimpleCmdReturnValueResolver;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import mockit.*;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;
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
        String versionFilePath = rootPath + "version.properties";
        String otaListPath = rootPath + "ots.list";
        new File(isoFilePath).delete();
        new File(versionFilePath).delete();
        new File(otaListPath).delete();
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
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=IDV\nversion=1.0".getBytes());
        }

        String otaListPath = rootPath + "ots.list";
        try (FileOutputStream fos = new FileOutputStream(otaListPath)) {
            fos.write("packageMD5 /rainos-img.squashfs\nscriptMD5 /OTAPreRunFun.bash".getBytes());
        }

        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                new LinuxIDVSystemUpgradePackageHelper.OtaFileInfo("filePath", "md5");
        BaseDetailNetworkInfoResponse networkInfoResponse = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO networkDTO = new BaseNetworkDTO();
        networkDTO.setIp("0.0.0.0");
        networkInfoResponse.setNetworkDTO(networkDTO);
        SeedFileInfoDTO seedFileInfoDTO = new SeedFileInfoDTO("seedFilePath", "seedFileMD5");
        DtoResponse<SeedFileInfoDTO> seedResponse = DtoResponse.success(seedFileInfoDTO);

        new Expectations(FileOperateUtil.class) {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
                helper.handleOtaListItem(anyString, anyString, anyString);
                result = otaFileInfo;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = networkInfoResponse;
                btClientAPI.makeBtSeed((BaseMakeBtSeedRequest) any);
                result = seedResponse;
                FileOperateUtil.deleteFile((File) any);
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
     * 获取升级包信息，版本文件不存在
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoVersionFileNotExist() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
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

        handler.getPackageInfo(isoFile.getName(), isoFile.getPath());

        Assert.fail();
    }

    /**
     * 获取升级包信息，读取版本文件异常
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoVersionFileException() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        File versionFile = new File(versionFilePath);
        versionFile.createNewFile();

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
            }
        };

        new MockUp<LinuxIDVSystemUpgradePackageHandler>() {
            @Mock
            String getVersionFilePath() {
                return versionFilePath;
            }
        };

        new MockUp<Properties>() {
            @Mock
            void load(InputStream inStream) throws IOException {
                throw new IOException();
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
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=VDI\nversion=1.0".getBytes());
        }

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
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
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("version=1.0".getBytes());
        }

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
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
     * 获取升级包信息，OTA文件列表不存在
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoOtaListNotExist() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=IDV\nversion=1.0".getBytes());
        }

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
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
     * 获取升级包信息，OTA文件列表读取错误
     * @throws BusinessException 异常
     * @throws IOException 异常
     */
    @Test(expected = BusinessException.class)
    public void testGetPackageInfoOtaListException() throws BusinessException, IOException {
        String rootPath = this.getClass().getResource("/").getPath();

        String isoFilePath = rootPath + "IDVSystemPackage.iso";
        File isoFile = new File(isoFilePath);
        isoFile.createNewFile();

        String versionFilePath = rootPath + "version.properties";
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=IDV\nversion=1.0".getBytes());
        }

        String otaListPath = rootPath + "ots.list";
        try (FileOutputStream fos = new FileOutputStream(otaListPath)) {
            fos.write("packageMD5 /rainos-img.squashfs\nscriptMD5 /OTAPreRunFun.bash".getBytes());
        }

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
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

        new MockUp<BufferedReader>() {
            @Mock
            String readLine() throws IOException {
                throw new IOException();
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
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=IDV\nversion=1.0".getBytes());
        }

        String otaListPath = rootPath + "ots.list";
        try (FileOutputStream fos = new FileOutputStream(otaListPath)) {
            fos.write("packageMD5 /rainos-img\nscriptMD5 /OTAPreRunFun".getBytes());
        }

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
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
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=IDV\nversion=1.0".getBytes());
        }

        String otaListPath = rootPath + "ots.list";
        try (FileOutputStream fos = new FileOutputStream(otaListPath)) {
            fos.write("packageMD5 /rainos-img.squashfs\nscriptMD5 /OTAPreRunFun.bash".getBytes());
        }

        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                new LinuxIDVSystemUpgradePackageHelper.OtaFileInfo("filePath", "md5");
        BaseDetailNetworkInfoResponse networkInfoResponse = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO networkDTO = new BaseNetworkDTO();
        networkDTO.setIp("0.0.0.0");
        networkInfoResponse.setNetworkDTO(networkDTO);
        SeedFileInfoDTO seedFileInfoDTO = new SeedFileInfoDTO("seedFilePath", "seedFileMD5");
        DtoResponse<SeedFileInfoDTO> seedResponse = DtoResponse.success(seedFileInfoDTO);

        new Expectations() {
            {
                byteArrayOutputStream.toString();
                result = "PASS";
                defaultExecutor.execute((CommandLine)any);
                result = 0;
                shellCommandRunner.execute((SimpleCmdReturnValueResolver) any);
                result = "PASS";
                helper.handleOtaListItem(anyString, anyString, anyString);
                result = otaFileInfo;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = networkInfoResponse;
                btClientAPI.makeBtSeed((BaseMakeBtSeedRequest) any);
                result = seedResponse;
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