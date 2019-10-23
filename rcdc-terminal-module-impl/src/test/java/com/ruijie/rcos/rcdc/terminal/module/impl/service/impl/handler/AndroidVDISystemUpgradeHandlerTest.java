package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseNetworkDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalOtaUpgradeScheduleService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.api.util.ZipUtil;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.base.util.StringUtils;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
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
public class AndroidVDISystemUpgradeHandlerTest {

    @Tested
    private AndroidVDISystemUpgradeHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageService terminalSystemUpgradePackageService;

    @Injectable
    private NetworkAPI networkAPI;

    @Injectable
    private TerminalOtaUpgradeScheduleService terminalOtaUpgradeScheduleService;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    /**
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageArgsIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> handler.uploadUpgradePackage(null), "request can not be null");
        Assert.assertTrue(true);
    }

    /**
     * 测试uploadUpgradePackage，version文件不存在
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageVersionNotExist() throws Exception {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("RG-RainOS_V3.0.32_A.zip");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upgradeMode", "AUTO");
        request.setCustomData(jsonObject);
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO networkDTO = new BaseNetworkDTO();
        networkDTO.setIp("172.28.109.7");
        response.setNetworkDTO(networkDTO);

        new MockUp<File>() {

            @Mock
            public boolean exists() {
                return false;
            }

            @Mock
            public boolean renameTo(File dest) {
                return true;
            }

        };

        new MockUp<AndroidVDISystemUpgradeHandler>() {

            @Mock
            private String generateFileMD5(String filePath) {
                return "3b20fe7c2aaff10b54312e1c868b4542";
            }

        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
            }
        };
        new Expectations(Files.class) {
            {
                Files.move((Path) any,(Path) any);
            }
        };

        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }

    }

    /**
     * 测试uploadUpgradePackage， 路径存在，读取version文件失败
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageLoadVersionFail() throws Exception {
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("RG-RainOS_V3.0.32_A.zip");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upgradeMode", "AUTO");
        request.setCustomData(jsonObject);
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO networkDTO = new BaseNetworkDTO();
        networkDTO.setIp("172.28.109.7");
        response.setNetworkDTO(networkDTO);

        new MockUp<File>() {

            @Mock
            public boolean exists() {
                return false;
            }

        };

        new MockUp<AndroidVDISystemUpgradeHandler>() {

            @Mock
            private String generateFileMD5(String filePath) {
                return "3b20fe7c2aaff10b54312e1c868b4542";
            }

            @Mock
            private void deleteVersionAndZip() {

            }

        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
                result = new IOException();
            }
        };

        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }

    }

    /**
     * 测试uploadUpgradePackage， 计算MD5失败
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageComputeMD5Fail() throws Exception {
        String path = AndroidVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersion";
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("RG-RainOS_V3.0.32_A.zip");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upgradeMode", "AUTO");
        request.setCustomData(jsonObject);
        final String savePath = "{\"seed_path\":\"/opt/ftp/terminal/ota/seed/123.zip.torrent\"}";
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO networkDTO = new BaseNetworkDTO();
        networkDTO.setIp("172.28.109.7");
        response.setNetworkDTO(networkDTO);
        byte[] bytesArr = new byte[]{'d',(byte)0xff,-1,(byte)255,(byte)0x80,(byte) 128,-128};
        new MockUp<AndroidVDISystemUpgradeHandler>() {

            @Mock
            public String getVersionFilePath() {
                return path;
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
                Files.move((Path) any,(Path) any);
            }
        };

        new MockUp<AndroidVDISystemUpgradeHandler>() {

            @Mock
            private void checkOtaUpgradePackage(String platType, String fileMD5, String packagePath) {

            }

            @Mock
            private void deleteVersionAndZip() {

            }
        };

        new MockUp<ShellCommandRunner>() {

            @Mock
            public String execute() {
                return savePath;
            }
        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
            }
        };
        new Expectations() {
            {
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = response;
            }
        };

        new Expectations(Md5Builder.class) {
            {
                Md5Builder.computeFileMd5((File) any);
                result = bytesArr;
            }
        };

        new Expectations(StringUtils.class) {
            {
                StringUtils.bytes2Hex(bytesArr);
                result = new IOException();
            }
        };

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.deleteFile((File) any);
            }
        };

        try {
            handler.uploadUpgradePackage(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_OTA_UPGRADE_COMPUTE_SEED_FILE_MD5_FAIL, e.getKey());
        }

    }

    /**
     * 测试uploadUpgradePackage， 成功
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadUpgradePackageSuccess() throws Exception {
        String path = AndroidVDISystemUpgradeHandlerTest.class.getResource("/").getPath() + "testVersion";
        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFileName("RG-RainOS_V3.0.32_A.zip");
        request.setFilePath("/temp");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upgradeMode", "AUTO");
        request.setCustomData(jsonObject);
        final String savePath = "{\"seed_path\":\"/opt/ftp/terminal/ota/seed/123.zip.torrent\"}";
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO networkDTO = new BaseNetworkDTO();
        networkDTO.setIp("172.28.109.7");
        response.setNetworkDTO(networkDTO);
        byte[] bytesArr = new byte[]{'d',(byte)0xff,-1,(byte)255,(byte)0x80,(byte) 128,-128};
        new MockUp<AndroidVDISystemUpgradeHandler>() {

            @Mock
            public String getVersionFilePath() {
                return path;
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
                Files.move((Path) any,(Path) any);
            }
        };


        new MockUp<AndroidVDISystemUpgradeHandler>() {

            @Mock
            private void checkOtaUpgradePackage(String platType, String fileMD5, String packagePath) {

            }

            @Mock
            private void deleteVersionAndZip() {

            }
        };

        new MockUp<ShellCommandRunner>() {

            @Mock
            public String execute() {
                return savePath;
            }
        };

        new MockUp<ShellCommandRunner>() {

            @Mock
            public String execute() {
                return "seed";
            }
        };

        new Expectations(ZipUtil.class) {
            {
                ZipUtil.unzipFile((File) any, (File) any);
            }
        };
        new Expectations() {
            {
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = response;
                terminalSystemUpgradePackageService.saveTerminalUpgradePackage((TerminalUpgradeVersionFileInfo) any);

            }
        };

        new Expectations(Md5Builder.class) {
            {
                Md5Builder.computeFileMd5((File) any);
                result = bytesArr;
            }
        };

        new Expectations(StringUtils.class) {
            {
                StringUtils.bytes2Hex(bytesArr);
                result = "3b20fe7c2aaff10b54312e1c868b4542";
            }
        };

        handler.uploadUpgradePackage(request);
        new Verifications() {
            {
                terminalSystemUpgradePackageService.saveTerminalUpgradePackage((TerminalUpgradeVersionFileInfo) any);
                times = 1;
            }
        };
    }


}
