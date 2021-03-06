package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundSaveDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalBackgroundInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.io.IoUtil;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.lang.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    @Injectable
    private FileInputStream fileInput;

    @Injectable
    private FileOutputStream fileOutput;

    private static final String REQUEST_DATA = "{'isDefaultImage':false,detailInfo:{'md5':'123','imageName':'123.png'" +
            ",'imagePath':'abc/background.png','filePath':'/opt/ftp/terminal/background/background.png'}}";


    /**
     * 测试上传时，需要删除文件的情况
     * 
     * @throws BusinessException 业务异常
     * @throws IOException IO异常
     */
    @Test
    public void testUploadExistDeleteFile() throws BusinessException, IOException {
        CbbTerminalBackgroundSaveDTO request = new CbbTerminalBackgroundSaveDTO();
        request.setImageName("abc.png");
        request.setImagePath("123");
        killThreadLocal(CbbTerminalBackgroundAPIImpl.class.getName(), "LOGGER");
        Deencapsulation.setField(cbbTerminalBackgroundAPI, "LOGGER", logger);
        new Expectations(Files.class) {
            {
                mockFile.exists();
                result = true;
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
            }
        };
        new MockUp<IoUtil>() {
            @Mock
            public void copy(@Nullable final InputStream input, @Nullable final OutputStream output) throws IOException {
                throw new IOException("abc");
            }

            @Mock
            public FileOutputStream toFileOutputStream(@Nullable final File to) throws FileNotFoundException {
                return fileOutput;
            }

            @Mock
            public FileInputStream toFileInputStream(@Nullable final File from) throws FileNotFoundException {
                return fileInput;
            }
        };
        try {
            cbbTerminalBackgroundAPI.saveBackgroundImageConfig(request);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), "abc");
        }
    }

    /**
     * 
     * /**
     * 测试获取背景图片不存在的情况
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetBackgroundImageInfoWhenNoExist() throws BusinessException {

        new Expectations() {
            {

                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = null;
            }
        };
        CbbTerminalBackgroundImageInfoDTO dto = cbbTerminalBackgroundAPI.getBackgroundImageInfo();
        Assert.assertEquals(dto.getImageName(), null);

        new Expectations() {
            {

                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
                mockFile.exists();
                result = false;
            }
        };

        CbbTerminalBackgroundImageInfoDTO dto2 = cbbTerminalBackgroundAPI.getBackgroundImageInfo();
        Assert.assertEquals(dto2.getImageName(), null);
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
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
            }
        };
        CbbTerminalBackgroundImageInfoDTO dto = cbbTerminalBackgroundAPI.getBackgroundImageInfo();
        Assert.assertTrue(dto != null);
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
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
            }
        };

        cbbTerminalBackgroundAPI.initBackgroundImage();

        new Verifications() {
            {
                mockSkyengineFile.delete(false);
                times = 1;
                globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
                times = 1;
                terminalBackgroundService.syncTerminalBackground((TerminalBackgroundInfo) any);
                times = 1;
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
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
        // 当文件不存在的时候:
        new Expectations() {
            {
                mockFile.exists();
                result = false;
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
                globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
            }
        };
        cbbTerminalBackgroundAPI.initBackgroundImage();

        new Verifications() {
            {
                globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
                times = 1;
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                times = 1;
                terminalBackgroundService.syncTerminalBackground((TerminalBackgroundInfo) any);
                times = 1;
            }
        };
        // 当数据库中不存在时，不需要同步
        new Expectations() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = null;
            }
        };
        cbbTerminalBackgroundAPI.initBackgroundImage();

        new Verifications() {
            {
                globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
                times = 0;
                terminalBackgroundService.syncTerminalBackground((TerminalBackgroundInfo) any);
                times = 0;
            }
        };
    }

    /**
     * 测试上传时，需要删除文件的情况,文件名后缀异常
     *
     * @throws BusinessException 业务异常
     * @throws IOException IO异常
     */
    @Test
    public void testUploadExistDeleteFileWhenFileSuffixException() throws BusinessException, IOException {
        CbbTerminalBackgroundSaveDTO request1 = new CbbTerminalBackgroundSaveDTO();
        request1.setImageName("abc.");
        request1.setImagePath("123");
        CbbTerminalBackgroundSaveDTO request2 = new CbbTerminalBackgroundSaveDTO();
        request2.setImageName("abc");
        request2.setImagePath("123");
        killThreadLocal(CbbTerminalBackgroundAPIImpl.class.getName(), "LOGGER");
        Deencapsulation.setField(cbbTerminalBackgroundAPI, "LOGGER", logger);
        new Expectations(Files.class) {
            {
                mockFile.exists();
                result = true;
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                result = REQUEST_DATA;
            }
        };
        try {
            cbbTerminalBackgroundAPI.saveBackgroundImageConfig(request1);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_INVALID_SUFFIX);
        }
        try {
            cbbTerminalBackgroundAPI.saveBackgroundImageConfig(request2);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_INVALID_SUFFIX);
        }


        new Verifications() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                times = 2;
            }
        };
    }

    /**
     * 测试上传文件的时候，文件后缀错误
     * 
     * @throws IOException IO异常
     * @throws BusinessException 业务异常
     */
    @Test
    public void testUploadNoNeedDeleteWhenFileSuffixError() throws IOException, BusinessException {
        CbbTerminalBackgroundSaveDTO request1 = new CbbTerminalBackgroundSaveDTO();
        request1.setImageName("abc.");
        request1.setImagePath("123");
        CbbTerminalBackgroundSaveDTO request2 = new CbbTerminalBackgroundSaveDTO();
        request2.setImageName("abc");
        request2.setImagePath("123");
        killThreadLocal(CbbTerminalBackgroundAPIImpl.class.getName(), "LOGGER");
        Deencapsulation.setField(cbbTerminalBackgroundAPI, "LOGGER", logger);
        new Expectations(Files.class) {
            {
                // 前两次返回空，不需要删除图片，后两次文件不存在。
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                returns(null, REQUEST_DATA, null, REQUEST_DATA);
                mockFile.exists();
                result = false;
            }
        };

        for (int i = 0; i < 2; i++) {
            try {
                cbbTerminalBackgroundAPI.saveBackgroundImageConfig(request1);
                Assert.fail();
            } catch (BusinessException e) {
                Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_INVALID_SUFFIX);
            }
        }
        for (int i = 0; i < 2; i++) {
            try {
                cbbTerminalBackgroundAPI.saveBackgroundImageConfig(request2);
                Assert.fail();
            } catch (BusinessException e) {
                Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_FILE_INVALID_SUFFIX);
            }
        }
        new Verifications() {
            {
                globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
                times = 4;
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
