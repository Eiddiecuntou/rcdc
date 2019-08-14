package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbGetLogoPathRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbInitLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbUploadLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.base.util.StringUtils;
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
                terminalLogoService.syncTerminalLogo(anyString, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
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
                terminalLogoService.syncTerminalLogo(anyString, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
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
                return true;
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
                terminalLogoService.syncTerminalLogo(anyString, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
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
                terminalLogoService.syncTerminalLogo(anyString, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
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
        new MockUp<CbbTerminalLogoAPIImpl>() {
            @Mock
            private void saveLogo(String logoPath) {
                
            }
        };

        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = "/opt/ftp/terminal/logo/logo.png";
                terminalLogoService.syncTerminalLogo(anyString, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
            }
        };
        terminalLogoAPI.uploadLogo(request);
        new Verifications() {
            {
                terminalLogoService.syncTerminalLogo(anyString, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
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
        CbbGetLogoPathRequest request = new CbbGetLogoPathRequest();
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = "logoPath";
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
        CbbInitLogoRequest request = new CbbInitLogoRequest();
        new Expectations() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                result = "logoPath";
                globalParameterAPI.updateParameter("terminalLogo", null);
                terminalLogoService.syncTerminalLogo(StringUtils.EMPTY, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);

            }
        };
        terminalLogoAPI.initLogo(request);

        new Verifications() {
            {
                globalParameterAPI.findParameter("terminalLogo");
                times = 1;
                globalParameterAPI.updateParameter("terminalLogo", null);
                times = 1;
                terminalLogoService.syncTerminalLogo(StringUtils.EMPTY, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
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
        CbbInitLogoRequest request = new CbbInitLogoRequest();
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
                terminalLogoService.syncTerminalLogo(StringUtils.EMPTY, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
                times = 0;
            }
        };

    }


}
