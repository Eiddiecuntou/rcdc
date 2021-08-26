package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.base.zip.ZipUtil;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/7
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AndroidVDISystemUpgradePackageHelperTest {

    @Tested
    private AndroidVDISystemUpgradePackageHelper helper;

    /**
     * 测试获取系统升级包信息
     */
    @Test
    public void testUnzipPackage() throws BusinessException, IOException {

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.createFileDirectory((File) any);

            }

        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };

        new Expectations(Files.class) {
            {
                Files.move((Path) any, (Path) any);
            }
        };

        UUID id = UUID.randomUUID();
        new MockUp<UUID>() {
            @Mock
            public UUID randomUUID() {
                return id;
            }
        };

        String filePath = "/aa/123.zip";
        String savePackageName = id.toString() + ".zip";

        String resultFilePath = helper.unZipPackage(filePath, savePackageName);

        String savePackagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName;
        assertEquals(savePackagePath, resultFilePath);

        new Verifications() {
            {
                File unzipFile;
                FileOperateUtil.createFileDirectory(unzipFile = withCapture());
                times = 1;
                assertEquals(unzipFile, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));

                File unzipFile2;
                File zipFile;
                ZipUtil.unzipFile(zipFile = withCapture(), unzipFile2 = withCapture());
                times = 1;
                assertEquals(unzipFile2, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));
                assertEquals(zipFile, new File(filePath));

                Path rainrcdFilePath;
                Path savePackageFilePath;
                Files.move(rainrcdFilePath = withCapture(), savePackageFilePath = withCapture());
                times = 1;
                assertEquals(rainrcdFilePath.toFile(), new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP));
                assertEquals(savePackageFilePath.toFile(), new File(savePackagePath));
            }
        };
    }


    /**
     * 测试获取系统升级包信息 - 解压升级包出现异常
     */
    @Test
    public void testUnzipPackageUnZipHasException() throws BusinessException, IOException {

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.createFileDirectory((File) any);

            }

        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
                result = new IOException("123");
            }
        };

        UUID id = UUID.randomUUID();
        new MockUp<UUID>() {
            @Mock
            public UUID randomUUID() {
                return id;
            }
        };

        String filePath = "/aa/123.zip";
        String savePackageName = id.toString() + ".zip";

        try {
            helper.unZipPackage(filePath, savePackageName);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }

        new Verifications() {
            {
                File unzipFile;
                FileOperateUtil.createFileDirectory(unzipFile = withCapture());
                times = 1;
                assertEquals(unzipFile, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));

                File unzipFile2;
                File zipFile;
                ZipUtil.unzipFile(zipFile = withCapture(), unzipFile2 = withCapture());
                times = 1;
                assertEquals(unzipFile2, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));
                assertEquals(zipFile, new File(filePath));
            }
        };
    }

    /**
     * 测试获取系统升级包信息 - 移动升级包出现异常
     */
    @Test
    public void testUnzipPackageMoveFileHasException() throws BusinessException, IOException {

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.createFileDirectory((File) any);

            }

        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };

        new Expectations(Files.class) {
            {
                Files.move((Path) any, (Path) any);
                result = new IOException("234");
            }
        };

        UUID id = UUID.randomUUID();
        new MockUp<UUID>() {
            @Mock
            public UUID randomUUID() {
                return id;
            }
        };

        String filePath = "/aa/123.zip";
        String savePackageName = id.toString() + ".zip";
        String savePackagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName;

        try {
            helper.unZipPackage(filePath, savePackageName);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_MOVE_FAIL, e.getKey());
        }

        new Verifications() {
            {
                File unzipFile;
                FileOperateUtil.createFileDirectory(unzipFile = withCapture());
                times = 1;
                assertEquals(unzipFile, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));

                File unzipFile2;
                File zipFile;
                ZipUtil.unzipFile(zipFile = withCapture(), unzipFile2 = withCapture());
                times = 1;
                assertEquals(unzipFile2, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));
                assertEquals(zipFile, new File(filePath));

                Path rainrcdFilePath;
                Path savePackageFilePath;
                Files.move(rainrcdFilePath = withCapture(), savePackageFilePath = withCapture());
                times = 1;
                assertEquals(rainrcdFilePath.toFile(), new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE_ZIP));
                assertEquals(savePackageFilePath.toFile(), new File(savePackagePath));
            }
        };
    }

    /**
     * 测试校验版本 - 加载配置文件异常
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckVersionInfoLoadPropertiesError() throws BusinessException {
        String path = "/abc/sss";

        String packagePath = "/aaa/bbb.zip";
        try {
            helper.checkVersionInfo(packagePath, path);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }
    }

    /**
     * 测试获取系统升级包信息 - 移动升级包出现异常
     */
    @Test
    public void testUnzipPackageMoveFilePackageIsIllegal() throws BusinessException, IOException {

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.createFileDirectory((File) any);

            }

        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }
        };

        UUID id = UUID.randomUUID();
        new MockUp<UUID>() {
            @Mock
            public UUID randomUUID() {
                return id;
            }
        };

        String filePath = "/aa/123.zip";
        String savePackageName = id.toString() + ".zip";
        String savePackagePath = Constants.TERMINAL_UPGRADE_OTA_PACKAGE + savePackageName;

        try {
            helper.unZipPackage(filePath, savePackageName);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_ILLEGAL, e.getKey());
        }

        new Verifications() {
            {
                File unzipFile;
                FileOperateUtil.createFileDirectory(unzipFile = withCapture());
                times = 1;
                assertEquals(unzipFile, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));

                File unzipFile2;
                File zipFile;
                ZipUtil.unzipFile(zipFile = withCapture(), unzipFile2 = withCapture());
                times = 1;
                assertEquals(unzipFile2, new File(Constants.TERMINAL_UPGRADE_OTA_PACKAGE));
                assertEquals(zipFile, new File(filePath));
            }
        };
    }

    /**
     * 测试校验版本 - 平台异常
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckVersionInfoPlatError() throws BusinessException {
        String path = this.getClass().getResource("/testAndroidVersionError1.properties").getPath();

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) {
                return "abc123".getBytes();
            }
        };

        new MockUp<StringUtils>() {
            @Mock
            public String bytes2Hex(byte[] bytes) {
                return "abc";
            }
        };

        String packagePath = "/aaa/bbb.zip";
        try {
            helper.checkVersionInfo(packagePath, path);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_HAS_ERROR, e.getKey());
        }

    }

    /**
     * 测试校验版本 - MD5错误
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckVersionInfoMD5Error() throws BusinessException {
        String path = this.getClass().getResource("/testAndroidVersion.properties").getPath();

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) {
                return "abc123".getBytes();
            }
        };

        new MockUp<StringUtils>() {
            @Mock
            public String bytes2Hex(byte[] bytes) {
                return "bcsd";
            }
        };

        String packagePath = "/aaa/bbb.zip";
        try {
            helper.checkVersionInfo(packagePath, path);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_PACKAGE_HAS_ERROR, e.getKey());
        }

    }

    /**
     * 测试校验版本 - MD5计算异常
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckVersionInfoGetMD5Error() throws BusinessException {
        String path = this.getClass().getResource("/testAndroidVersion.properties").getPath();

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) throws IOException {
                throw new IOException("123");
            }
        };

        new MockUp<StringUtils>() {
            @Mock
            public String bytes2Hex(byte[] bytes) {
                return "bcsd";
            }
        };

        String packagePath = "/aaa/bbb.zip";
        try {
            helper.checkVersionInfo(packagePath, path);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }

    }
}
