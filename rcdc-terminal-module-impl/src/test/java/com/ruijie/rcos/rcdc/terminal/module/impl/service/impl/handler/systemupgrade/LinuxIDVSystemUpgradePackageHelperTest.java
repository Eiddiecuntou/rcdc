package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.*;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/18 14:39
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class LinuxIDVSystemUpgradePackageHelperTest {

    @Tested
    private LinuxIDVSystemUpgradePackageHelper helper;

    /**
     * after
     */
    @After
    public void after() {
        String rootPath = this.getClass().getResource("/").getPath();
        String versionFilePath = rootPath + "version.properties";
        String otaListPath = rootPath + "ots.list";
        new File(versionFilePath).delete();
        new File(otaListPath).delete();
    }

    /**
     * 处理OTA文件列表项，正常流程
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testHandleOtaListItem() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String fileMD5 = "md5";
        String srcFileName = "IDVPackageHelperSrcFile.squashfs";
        String destFileName = "IDVPackageHeplerDestFile.squashfs";
        String otaListItem = fileMD5 + "  " + rootPath + srcFileName;

        new MockUp<File>() {
            @Mock
            boolean isFile() {
                return true;
            }

            @Mock
            long length() {
                return 10000L;
            }

            @Mock
            long getUsableSpace() {
                return 20000L;
            }

            @Mock
            boolean exists() {
                return true;
            }
        };

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.copyfile(anyString, anyString);
            }
        };

        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                helper.handleOtaListItem(otaListItem, rootPath, destFileName, Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);

        Assert.assertEquals(fileMD5, otaFileInfo.getMd5());
        Assert.assertFalse(otaFileInfo.getFilePath().startsWith(Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH));
    }

    /**
     * 处理OTA文件列表项，源文件不存在
     * 
     * @throws BusinessException 异常
     */
    @Test(expected = BusinessException.class)
    public void testHandleOtaListItemSrcFileNotExist() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String fileMD5 = "md5";
        String srcFileName = "IDVPackageHelperSrcFile.squashfs";
        String destFileName = "IDVPackageHeplerDestFile.squashfs";
        String otaListItem = fileMD5 + "  " + rootPath + srcFileName;

        new MockUp<File>() {
            @Mock
            boolean isFile() {
                return false;
            }
        };

        helper.handleOtaListItem(otaListItem, rootPath, destFileName, Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);

        Assert.fail();
    }

    /**
     * 处理OTA文件列表项，列表项格式错误
     * 
     * @throws BusinessException 异常
     */
    @Test(expected = BusinessException.class)
    public void testHandleOtaListItemFormatError() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String fileMD5 = "md5";
        String destFileName = "IDVPackageHeplerDestFile.squashfs";
        String otaListItem = fileMD5 + "  " + fileMD5;

        helper.handleOtaListItem(otaListItem, rootPath, destFileName, Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);

        Assert.fail();
    }

    /**
     * 处理OTA文件列表项，磁盘空间不足
     * 
     * @throws BusinessException 异常
     */
    @Test(expected = BusinessException.class)
    public void testHandleOtaListItemNoEnoughSpace() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String fileMD5 = "md5";
        String srcFileName = "IDVPackageHelperSrcFile.squashfs";
        String destFileName = "IDVPackageHeplerDestFile.squashfs";
        String otaListItem = fileMD5 + "  " + rootPath + srcFileName;

        new MockUp<File>() {
            @Mock
            boolean isFile() {
                return true;
            }

            @Mock
            long length() {
                return 20000L;
            }

            @Mock
            long getUsableSpace() {
                return 10000L;
            }
        };

        helper.handleOtaListItem(otaListItem, rootPath, destFileName, Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);

        Assert.fail();
    }

    /**
     * 处理OTA文件列表项，复制失败
     * 
     * @throws BusinessException 异常
     */
    @Test(expected = BusinessException.class)
    public void testHandleOtaListItemCopyFail() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String fileMD5 = "md5";
        String srcFileName = "IDVPackageHelperSrcFile.squashfs";
        String destFileName = "IDVPackageHeplerDestFile.squashfs";
        String otaListItem = fileMD5 + "  " + rootPath + srcFileName;

        new MockUp<File>() {
            @Mock
            boolean isFile() {
                return true;
            }

            @Mock
            long length() {
                return 10000L;
            }

            @Mock
            long getUsableSpace() {
                return 20000L;
            }

            @Mock
            boolean exists() {
                return false;
            }
        };

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.copyfile(anyString, anyString);
            }
        };

        helper.handleOtaListItem(otaListItem, rootPath, destFileName, Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH);

        Assert.fail();
    }

    /**
     * 读取版本文件，正常流程
     * 
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testGetVersionProperties() throws IOException, BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String versionFilePath = rootPath + "version.properties";
        try (FileOutputStream fos = new FileOutputStream(versionFilePath)) {
            fos.write("plat=IDV\nversion=1.0".getBytes());
        }

        Properties versionProperties = helper.getVersionProperties(versionFilePath);

        Assert.assertEquals("IDV", versionProperties.getProperty("plat"));
        Assert.assertEquals("1.0", versionProperties.getProperty("version"));
    }

    /**
     * 读取版本文件，异常
     * 
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testGetVersionPropertiesException() throws IOException, BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String versionFilePath = rootPath + "version.properties";
        File versionFile = new File(versionFilePath);
        versionFile.createNewFile();

        new MockUp<Properties>() {
            @Mock
            void load(InputStream inStream) throws IOException {
                throw new IOException();
            }
        };

        try {
            helper.getVersionProperties(versionFilePath);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_VERSION_FILE_ERROR);
        }
    }

    /**
     * 读取OTA文件列表，正常流程
     * 
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testGetOtaFilesInfo() throws IOException, BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String otaListPath = rootPath + "ots.list";
        try (FileOutputStream fos = new FileOutputStream(otaListPath)) {
            fos.write("packageMD5 /rainos-img.squashfs\nscriptMD5 /OTAPreRunFun.bash".getBytes());
        }

        List<String> otaFilesInfoList = helper.getOtaFilesInfo(otaListPath);

        Assert.assertEquals(2, otaFilesInfoList.size());
        Assert.assertTrue(otaFilesInfoList.get(0).endsWith("rainos-img.squashfs"));
        Assert.assertTrue(otaFilesInfoList.get(1).endsWith("OTAPreRunFun.bash"));
    }

    /**
     * 读取OTA文件列表，列表读取错误
     * 
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testGetOtaFilesInfoException() throws IOException, BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String otaListPath = rootPath + "ots.list";
        try (FileOutputStream fos = new FileOutputStream(otaListPath)) {
            fos.write("packageMD5 /rainos-img.squashfs\nscriptMD5 /OTAPreRunFun.bash".getBytes());
        }

        new MockUp<BufferedReader>() {
            @Mock
            String readLine() throws IOException {
                throw new IOException();
            }
        };

        try {
            helper.getOtaFilesInfo(otaListPath);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_OTA_FILE_ERROR);
        }
    }

    /**
     * 测试OtaFileInfo
     */
    @Test
    public void testOtaFileInfo() {
        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo = new LinuxIDVSystemUpgradePackageHelper.OtaFileInfo("path", "md5");
        Assert.assertEquals("path", otaFileInfo.getFilePath());
        Assert.assertEquals("md5", otaFileInfo.getMd5());

        otaFileInfo.setFilePath("filePath");
        otaFileInfo.setMd5("fileMD5");
        Assert.assertEquals("filePath", otaFileInfo.getFilePath());
        Assert.assertEquals("fileMD5", otaFileInfo.getMd5());
    }
}
