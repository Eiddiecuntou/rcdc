package com.ruijie.rcos.rcdc.terminal.module.impl.init;


import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseNetworkDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AndroidVDIUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxVDIUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class LinuxLinuxVDITerminalComponentUpgradeInitTest {

    @Tested
    private LinuxVDITerminalComponentUpgradeInit init;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private LinuxVDIUpdatelistCacheInit linuxVDIUpdatelistCacheInit;

    @Injectable
    private AndroidVDIUpdatelistCacheInit androidVDIUpdatelistCacheInit;

    @Mocked
    private ShellCommandRunner runner;

    @Injectable
    private NetworkAPI networkAPI;

    /**
     * 测试safeInit，开发环境
     * 
     * @param enviroment mock对象
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIsDevelop(@Mocked Enviroment enviroment) throws InterruptedException {

        new Expectations() {
            {
                Enviroment.isDevelop();
                result = true;
            }
        };
        init.safeInit();

        Thread.sleep(1000);
        
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，获取本地ip失败
     * 
     * @param enviroment mock对象
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitGetLocalAddrFail(@Mocked Enviroment enviroment) throws BusinessException, InterruptedException {
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO dto = new BaseNetworkDTO();
        dto.setIp("172.12.22.45");
        response.setNetworkDTO(dto);
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = new BusinessException("key");
            }
        };
        init.safeInit();

        Thread.sleep(1000);
        
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip为空
     * 
     * @param enviroment mock对象
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitIpIsBlank(@Mocked Enviroment enviroment) throws BusinessException {
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO dto = new BaseNetworkDTO();
        dto.setIp("172.12.22.45");
        response.setNetworkDTO(dto);
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = response;
                globalParameterAPI.findParameter(anyString);
                result = "";
            }
        };

        try {
            init.safeInit();

            Thread.sleep(1000);
            
        } catch (Exception e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateLinuxVDI.py", "172.12.22.45"));
                times = 1;
                linuxVDIUpdatelistCacheInit.init();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip一致
     * 
     * @param enviroment mock对象
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpEqualsCurrentIp(@Mocked Enviroment enviroment) throws BusinessException, InterruptedException {
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO dto = new BaseNetworkDTO();
        dto.setIp("172.12.22.45");
        response.setNetworkDTO(dto);
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = response;
                globalParameterAPI.findParameter(anyString);
                result = "172.12.22.45";
            }
        };
        try {
            init.safeInit();
            Thread.sleep(1000);
            
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;
                linuxVDIUpdatelistCacheInit.init();
                times = 1;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip不同,executeUpdate有BusinessException
     * 
     * @param enviroment mock对象
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpDifferentCurrentIpExecuteUpdateHasBusinessException(@Mocked Enviroment enviroment)
            throws BusinessException, InterruptedException {
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO dto = new BaseNetworkDTO();
        dto.setIp("172.0.0.0");
        response.setNetworkDTO(dto);
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = response;
                globalParameterAPI.findParameter(anyString);
                result = "172.22.25.45";
                runner.execute((LinuxVDITerminalComponentUpgradeInit.BtShareInitReturnValueResolver) any);
                result = new BusinessException("key");
            }
        };
        try {
            init.safeInit();
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }
        
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;

                runner.execute((LinuxVDITerminalComponentUpgradeInit.BtShareInitReturnValueResolver) any);
                times = 2;
                linuxVDIUpdatelistCacheInit.init();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip不同
     * 
     * @param enviroment mock对象
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpDifferentCurrentIp(@Mocked Enviroment enviroment) throws BusinessException, InterruptedException {
        BaseDetailNetworkInfoResponse response = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO dto = new BaseNetworkDTO();
        dto.setIp("172.12.22.45");
        response.setNetworkDTO(dto);
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = response;
                globalParameterAPI.findParameter(anyString);
                result = "172.22.25.45";
            }
        };
        try {
            init.safeInit();
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }
        
        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 1;

                runner.execute((LinuxVDITerminalComponentUpgradeInit.BtShareInitReturnValueResolver) any);
                times = 2;
                linuxVDIUpdatelistCacheInit.init();
                times = 0;
            }
        };
    }

    /**
     * 测试BtShareInitReturnValueResolver的resolve方法,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testBtShareInitReturnValueResolverArgumentIsNull() throws Exception {
        LinuxVDITerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolver = init.new BtShareInitReturnValueResolver(TerminalTypeEnums.VDI_LINUX);
        ThrowExceptionTester.throwIllegalArgumentException(() -> resolver.resolve("", 1, "dsd"), "command can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> resolver.resolve("sdsd", null, "dsd"), "existValue can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> resolver.resolve("sdsd", 1, ""), "outStr can not be null");
        assertTrue(true);
    }

    /**
     * 测试BtShareInitReturnValueResolver的resolve方法,exitValue不为0
     */
    @Test
    public void testBtShareInitReturnValueResolverExitValueNotZero() {
        LinuxVDITerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolver = init.new BtShareInitReturnValueResolver(TerminalTypeEnums.VDI_LINUX);
        try {
            resolver.resolve("dsd", 1, "dsd");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_SYSTEM_CMD_EXECUTE_FAIL, e.getKey());
        }
    }

    /**
     * 测试BtShareInitReturnValueResolver的resolve方法,
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testBtShareInitReturnValueResolver() throws BusinessException {
        LinuxVDITerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolver = init.new BtShareInitReturnValueResolver(TerminalTypeEnums.VDI_LINUX);
        new MockUp<LinuxVDITerminalComponentUpgradeInit>() {
            @Mock
            public String getLocalIP() {
                return "192.168.1.2";
            }
        };

        resolver.resolve("dsd", 0, "dsd");

        new Verifications() {
            {
                globalParameterAPI.updateParameter(anyString, "192.168.1.2");
                times = 1;
                linuxVDIUpdatelistCacheInit.init();
                times = 1;
            }
        };
    }
}
