package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackGroundUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.commkit.base.AbstractSslChannelInitializer;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@RunWith(SkyEngineRunner.class)
public class CbbTerminalBackgroundAPIImplTest {

    @Tested
    CbbTerminalBackgroundAPIImpl cbbTerminalBackgroundAPI;

    @Injectable
    private ConfigFacade configFacade;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    TerminalBackgroundService terminalBackgroundService;

    @Mocked
    private File mockFile;

    @Mocked
    private Files files;

    @Injectable
    private Logger logger;

    @Mocked
    private SkyengineFile mockSkyengineFile;

    private static final String CONFIG_FACADE = "file.busiz.dir.terminal.background";

    @Test
    public void testParamNullError() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(
                    () -> cbbTerminalBackgroundAPI.upload(null), "request must not be null");
            ThrowExceptionTester.throwIllegalArgumentException(
                    () -> cbbTerminalBackgroundAPI.getBackgroundImageInfo(null), "request must not be null");
            ThrowExceptionTester.throwIllegalArgumentException(
                    () -> cbbTerminalBackgroundAPI.initBackgroundImage(null), "request must not be null");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testUploadExistDeleteFile() throws BusinessException, IOException {
        CbbTerminalBackGroundUploadRequest request = new CbbTerminalBackGroundUploadRequest();
        request.setImageName("abc");
        request.setImagePath("123");
        killThreadLocal(CbbTerminalBackgroundAPIImpl.class.getName(), "LOGGER");
        Deencapsulation.setField(cbbTerminalBackgroundAPI, "LOGGER", logger);
        new Expectations(File.class){{
            configFacade.read(CONFIG_FACADE);
            result = FileUtils.getTempDirectoryPath();
            mockFile.setReadable(true,false);
            mockFile.setExecutable(true,false);
            mockFile.exists();
            returns(true,true,false,true,true);
            mockFile.isFile();
            returns(true,false);
            mockFile.mkdir();
            returns(false,true);
            mockSkyengineFile.delete(false);

        }};
        //文件不是文件夹，或者创建失败:
        for(int i=0;i<2;i++){
            try {
                cbbTerminalBackgroundAPI.upload(request);

                Assert.fail();
            } catch (BusinessException e) {
                System.out.println("******************************************end*********");
                Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_OPERATE_FAIL);
            }
        }
        for (int i=0;i<1;i++){
            DefaultResponse response = cbbTerminalBackgroundAPI.upload(request);
//            System.out.println("******************************************end*");
//            Assert.assertEquals(response.getStatus(), Response.Status.SUCCESS);
        }
        new Verifications(){{
            mockFile.exists();
            times =5;
        }};
    }
    @Test
    public void testUploadNoExistDeleteFile() throws BusinessException {
        CbbTerminalBackGroundUploadRequest request = new CbbTerminalBackGroundUploadRequest();
        request.setImageName("abc");
        request.setImagePath("123");
        new Expectations(File.class){{
            configFacade.read(CONFIG_FACADE);
            result = FileUtils.getTempDirectoryPath();
            mockFile.exists();
            result = true;
            mockFile.isFile();
            result = false;
            mockSkyengineFile.delete(false);
            terminalBackgroundService.syncTerminalLogo(request.getImageName());
        }};
        cbbTerminalBackgroundAPI.upload(request);
        new Verifications(){{
            terminalBackgroundService.syncTerminalLogo(request.getImageName());
            times = 1;
        }};
    }

    @Test
    public void testGetBackgroundImageInfo() {
    }

    @Test
    public void testInitBackgroundImage() {
    }
    private void killThreadLocal(String clazzName, String fieldName) {
        try {
            Field field = Class.forName(clazzName).getDeclaredField(fieldName);
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            int modifiers = modifiersField.getInt(field);
            modifiers &= ~Modifier.FINAL;
            modifiersField.setInt(field, modifiers);
            field.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("反射出錯", e);
        }
    }
}