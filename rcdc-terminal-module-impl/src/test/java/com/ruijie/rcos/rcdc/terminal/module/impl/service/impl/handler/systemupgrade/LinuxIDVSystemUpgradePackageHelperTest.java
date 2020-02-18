package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

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
     * 处理OTA文件列表项，正常流程
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
                helper.handleOtaListItem(otaListItem, rootPath, destFileName);

        Assert.assertEquals(fileMD5, otaFileInfo.getMd5());
        Assert.assertFalse(otaFileInfo.getFilePath().startsWith(Constants.TERMINAL_UPGRADE_LINUX_IDV_ISO_MOUNT_PATH));
    }

    /**
     * 处理OTA文件列表项，源文件不存在
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

        helper.handleOtaListItem(otaListItem, rootPath, destFileName);

        Assert.fail();
    }

    /**
     * 处理OTA文件列表项，列表项格式错误
     * @throws BusinessException 异常
     */
    @Test(expected = BusinessException.class)
    public void testHandleOtaListItemFormatError() throws BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        String fileMD5 = "md5";
        String destFileName = "IDVPackageHeplerDestFile.squashfs";
        String otaListItem = fileMD5 + "  " + fileMD5;

        helper.handleOtaListItem(otaListItem, rootPath, destFileName);

        Assert.fail();
    }

    /**
     * 处理OTA文件列表项，磁盘空间不足
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

        helper.handleOtaListItem(otaListItem, rootPath, destFileName);

        Assert.fail();
    }

    /**
     * 处理OTA文件列表项，复制失败
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

        helper.handleOtaListItem(otaListItem, rootPath, destFileName);

        Assert.fail();
    }

    /**
     * 测试OtaFileInfo
     */
    @Test
    public void testOtaFileInfo() {
        LinuxIDVSystemUpgradePackageHelper.OtaFileInfo otaFileInfo =
                new LinuxIDVSystemUpgradePackageHelper.OtaFileInfo("path", "md5");
        Assert.assertEquals("path", otaFileInfo.getFilePath());
        Assert.assertEquals("md5", otaFileInfo.getMd5());

        otaFileInfo.setFilePath("filePath");
        otaFileInfo.setMd5("fileMD5");
        Assert.assertEquals("filePath", otaFileInfo.getFilePath());
        Assert.assertEquals("fileMD5", otaFileInfo.getMd5());
    }
}