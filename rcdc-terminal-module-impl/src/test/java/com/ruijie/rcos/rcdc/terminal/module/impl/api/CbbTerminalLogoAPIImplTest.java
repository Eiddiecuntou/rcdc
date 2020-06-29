package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbUploadLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalLogoInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author hs
 */
@RunWith(JMockit.class)
public class CbbTerminalLogoAPIImplTest {

    @Tested
    CbbTerminalLogoAPIImpl terminalLogoAPI;

    @Injectable
    ConfigFacade configFacade;

    @Injectable
    GlobalParameterAPI globalParameterAPI;

    @Injectable
    TerminalLogoService terminalLogoService;

    /**
     * 测试uploadLogo，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadLogoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalLogoAPI.uploadLogo(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试uploadLogo，Logo路径和Logo都不存在
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadLogoPathIsNull() throws Exception {
        CbbUploadLogoRequest request = new CbbUploadLogoRequest("logoPath", "logoName", "logoMD5");
        new MockUp<File>() {
            @Mock
            boolean exists() {
                return false;
            }

            @Mock
            boolean mkdir() {
                return true;
            }

            @Mock
            boolean setReadable() {
                return true;
            }

            @Mock
            boolean setExecutable() {
                return true;
            }
        };
        new Expectations(Files.class) {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = null;
                configFacade.read("file.busiz.dir.terminal.logo");
                result = "/opt/ftp/terminal/logo/";
                Files.move((Path) any, (Path) any);
                globalParameterAPI.updateParameter("terminalLogo", anyString);
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
            }
        };
        terminalLogoAPI.uploadLogo(request);
        new Verifications() {
            {
                configFacade.read("file.busiz.dir.terminal.logo");
                times = 1;
                Files.move((Path) any, (Path) any);
                times = 1;
                globalParameterAPI.updateParameter("terminalLogo", anyString);
                times = 1;
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 1;
            }
        };

    }

    /**
     * 测试uploadLogo，Logo不存在，Logo文件路径存在
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadLogoFileNotExist() throws Exception {
        CbbUploadLogoRequest request = new CbbUploadLogoRequest("logoPath", "logoName", "logoMD5");
        new MockUp<File>() {
            @Mock
            boolean exists() {
                return false;
            }

            @Mock
            boolean mkdir() {
                return false;
            }

        };
        new Expectations(Files.class) {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = null;
                configFacade.read("file.busiz.dir.terminal.logo");
                result = "/opt/ftp/terminal/logo/";
                Files.move((Path) any, (Path) any);
                globalParameterAPI.updateParameter("terminalLogo", anyString);
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
            }
        };
        terminalLogoAPI.uploadLogo(request);
        new Verifications() {
            {
                configFacade.read("file.busiz.dir.terminal.logo");
                times = 1;
                Files.move((Path) any, (Path) any);
                times = 1;
                globalParameterAPI.updateParameter("terminalLogo", anyString);
                times = 1;
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 1;
            }
        };

    }

    /**
     * 测试uploadLogo，Exception
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadLogoException() throws Exception {
        CbbUploadLogoRequest request = new CbbUploadLogoRequest("logoPath", "logoName", "logoMD5");
        new MockUp<File>() {
            @Mock
            boolean exist() {
                return true;
            }

            @Mock
            boolean mkdir() {
                return true;
            }

            @Mock
            boolean setReadable() {
                return true;
            }

            @Mock
            boolean setExecutable() {
                return true;
            }
        };
        new Expectations(Files.class) {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = null;
                configFacade.read("file.busiz.dir.terminal.logo");
                result = "/opt/ftp/terminal/logo/";
                Files.move((Path) any, (Path) any);
                result = new IOException();
            }
        };
        try {
            terminalLogoAPI.uploadLogo(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_UPLOAD_LOGO_FAIL, e.getKey());
        }
        new Verifications() {
            {
                configFacade.read("file.busiz.dir.terminal.logo");
                times = 1;
                Files.move((Path) any, (Path) any);
                times = 1;
            }
        };

    }

    /**
     * 测试uploadLogo，Logo文件存在
     *
     * @throws Exception 异常
     */
    @Test
    public void testUploadLogoFileExist() throws Exception {
        CbbUploadLogoRequest request = new CbbUploadLogoRequest("logoPath", "logoName", "logoMD5");
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        String logoInfo = JSON.toJSONString(terminalLogoInfo);
        new MockUp<CbbTerminalLogoAPIImpl>() {
            @Mock
            private String saveLogo(String logoPath) {
                return "/logo/logo.png";
            }
        };

        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = logoInfo;
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
            }
        };
        terminalLogoAPI.uploadLogo(request);
        new Verifications() {
            {
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 1;
            }
        };

    }


    /**
     * 测试getLogoPath
     *
     * @throws Exception 异常
     */
    @Test
    public void testGetLogoPath() throws Exception {
        DefaultRequest request = new DefaultRequest();
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        String logoInfo = JSON.toJSONString(terminalLogoInfo);
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = logoInfo;
            }
        };
        terminalLogoAPI.getLogoPath(request);

        new Verifications() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                times = 1;
            }
        };

    }

    /**
     * 测试initLogo  logoPath Not Null
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitLogoPathIsNotNull() throws Exception {
        DefaultRequest request = new DefaultRequest();
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        String logoInfo = JSON.toJSONString(terminalLogoInfo);
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = logoInfo;
                globalParameterAPI.updateParameter("terminalLogo", null);
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);

            }
        };
        terminalLogoAPI.initLogo(request);

        new Verifications() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                times = 1;
                globalParameterAPI.updateParameter("terminalLogo", null);
                times = 1;
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 1;
            }
        };

    }

    /**
     * 测试initLogo  logoPath Is Null
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitLogoPathIsNull() throws Exception {
        DefaultRequest request = new DefaultRequest();
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = null;
            }
        };
        terminalLogoAPI.initLogo(request);

        new Verifications() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                times = 1;
                globalParameterAPI.updateParameter("terminalLogo", null);
                times = 0;
                terminalLogoService.syncTerminalLogo(new TerminalLogoInfo(), SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 0;
            }
        };

    }

    /**
     * 测试initLogo  logo Exists
     *
     * @param skyengineFile 文件
     *
     * @throws Exception 异常
     */
    @Test
    public void testInitLogoWithLogoExists(@Mocked SkyengineFile skyengineFile) throws Exception {
        DefaultRequest request = new DefaultRequest();
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath("/logo/logo.png");
        terminalLogoInfo.setMd5("123456");
        String logoInfo = JSON.toJSONString(terminalLogoInfo);
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = logoInfo;
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };
        terminalLogoAPI.initLogo(request);

        new Verifications() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                times = 1;
                globalParameterAPI.updateParameter("terminalLogo", null);
                times = 1;
                terminalLogoService.syncTerminalLogo((TerminalLogoInfo) any, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 1;
            }
        };

    }


}
