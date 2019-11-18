package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackgroundUpload;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

import mockit.*;

/**
 * Description: 终端背景图片接口实现类
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/8
 *
 * @author songxiang
 */

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

    @Mocked
    private FileOperateUtil fileOperateUtil;

    private static final String CONFIG_FACADE = "file.busiz.dir.terminal.background";

    /**
     * 测试参数为空
     */
    @Test
    public void testParamNullError() {
        try {
            ThrowExceptionTester.throwIllegalArgumentException(() -> cbbTerminalBackgroundAPI.upload(null), "request must not be null");
            ThrowExceptionTester.throwIllegalArgumentException(() -> cbbTerminalBackgroundAPI.getBackgroundImageInfo(null),
                    "request must not be null");
            ThrowExceptionTester.throwIllegalArgumentException(() -> cbbTerminalBackgroundAPI.initBackgroundImage(null), "request must not be null");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * 测试上传时，需要删除文件的情况
     * 
     * @throws BusinessException 业务异常
     * @throws IOException IO异常
     */
    @Test
    public void testUploadExistDeleteFile() throws BusinessException, IOException {
        CbbTerminalBackgroundUpload request = new CbbTerminalBackgroundUpload();
        request.setImageName("abc");
        request.setImagePath("123");
        killThreadLocal(CbbTerminalBackgroundAPIImpl.class.getName(), "LOGGER");
        Deencapsulation.setField(cbbTerminalBackgroundAPI, "LOGGER", logger);
        new Expectations(File.class) {
            {
                configFacade.read(CONFIG_FACADE);
                result = FileUtils.getTempDirectoryPath();
                mockFile.exists();
                result = true;
                mockSkyengineFile.delete(false);
                Files.move((File) any, (File) any);
                returns(new Delegate<Files>() {
                    public void move(File from, File to) throws IOException {

                    }
                }, new Delegate<Files>() {
                    public void move(File from, File to) throws IOException {
                        throw new IOException("abc");
                    }
                });
            }
        };
        cbbTerminalBackgroundAPI.upload(request);
        try {
            cbbTerminalBackgroundAPI.upload(request);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_OPERATE_FAIL);
        }
        new Verifications() {
            {
                terminalBackgroundService.syncTerminalBackground(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试上传背景图片不需要删除的情况
     * 
     * @throws BusinessException 业务异常
     * @throws IOException IO异常
     */
    @Test
    public void testUploadNoExistDeleteFile() throws BusinessException, IOException {
        CbbTerminalBackgroundUpload request = new CbbTerminalBackgroundUpload();
        request.setImageName("abc");
        request.setImagePath("123");
        killThreadLocal(CbbTerminalBackgroundAPIImpl.class.getName(), "LOGGER");
        Deencapsulation.setField(cbbTerminalBackgroundAPI, "LOGGER", logger);
        new Expectations(File.class) {
            {
                configFacade.read(CONFIG_FACADE);
                result = FileUtils.getTempDirectoryPath();
                mockFile.exists();
                result = false;
                Files.move((File) any, (File) any);
                returns(new Delegate<Files>() {
                    public void move(File from, File to) throws IOException {}
                }, new Delegate<Files>() {
                    public void move(File from, File to) throws IOException {
                        throw new IOException("abc");
                    }
                });
            }
        };
        cbbTerminalBackgroundAPI.upload(request);
        try {
            cbbTerminalBackgroundAPI.upload(request);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_OPERATE_FAIL);
        }
        new Verifications() {
            {
                terminalBackgroundService.syncTerminalBackground(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试获取背景图片时，文件后缀不合法
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetBackgroundImageInfoWhenFileSuffixInvalid() throws BusinessException {
        new Expectations() {
            {
                mockFile.exists();
                result = true;
                configFacade.read(CONFIG_FACADE);
                result = FileUtils.getTempDirectoryPath();
            }
        };
        new MockUp<File>(){
            @Mock
            public String getName(){
                return "abc";
            }
        };

        DtoResponse<CbbTerminalBackgroundImageInfoDTO> dtoResponse = null;
        try {
            cbbTerminalBackgroundAPI.getBackgroundImageInfo(new DefaultRequest());
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(),BusinessKey.RCDC_FILE_INVALID_SUFFIX);
        }
        new MockUp<File>(){
            @Mock
            public String getName(){
                return "abc.";
            }
        };

        try {
            cbbTerminalBackgroundAPI.getBackgroundImageInfo(new DefaultRequest());
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(),BusinessKey.RCDC_FILE_INVALID_SUFFIX);
        }

    }

    /**

    /**
     * 测试获取背景图片不存在的情况
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetBackgroundImageInfoWhenNoExist() throws BusinessException {
        new Expectations() {
            {
                mockFile.exists();
                result = false;
            }
        };
        DtoResponse<CbbTerminalBackgroundImageInfoDTO> dtoResponse = cbbTerminalBackgroundAPI.getBackgroundImageInfo(new DefaultRequest());
        Assert.assertEquals(dtoResponse.isEmpty(), true);
    }

    /**
     * 测试获取背景图片存在的情况
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetBackgroundImageInfoWhenExist() throws BusinessException {
        new Expectations() {
            {
                mockFile.exists();
                result = true;
                configFacade.read(CONFIG_FACADE);
                result = FileUtils.getTempDirectoryPath();
            }
        };
        DtoResponse<CbbTerminalBackgroundImageInfoDTO> dtoResponse = cbbTerminalBackgroundAPI.getBackgroundImageInfo(new DefaultRequest());
        Assert.assertEquals(dtoResponse.isEmpty(), false);
    }

    /**
     * 测试初始化默认配置
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testInitBackgroundImageWhenDelete() throws BusinessException {
        new Expectations() {
            {
                mockFile.exists();
                result = true;
                mockSkyengineFile.delete(false);
            }
        };

        cbbTerminalBackgroundAPI.initBackgroundImage(new DefaultRequest());

        new Verifications() {
            {
                mockSkyengineFile.delete(false);
                times = 1;
                globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
                times = 1;
                terminalBackgroundService.syncTerminalBackground(StringUtils.EMPTY);
                times = 1;
            }
        };
    }

    /**
     * 测试初始化默认配置当不需要删除图片的情况
     * 
     * @throws BusinessException
     */
    @Test
    public void testInitBackgroundImageWhenNoDelete() throws BusinessException {
        new Expectations() {
            {
                mockFile.exists();
                result = false;

            }
        };
        DefaultResponse response = cbbTerminalBackgroundAPI.initBackgroundImage(new DefaultRequest());
        Assert.assertEquals(response.getStatus(), Response.Status.SUCCESS);
        new Verifications() {
            {
                globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
                times = 0;
                terminalBackgroundService.syncTerminalBackground(StringUtils.EMPTY);
                times = 0;
            }
        };
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
