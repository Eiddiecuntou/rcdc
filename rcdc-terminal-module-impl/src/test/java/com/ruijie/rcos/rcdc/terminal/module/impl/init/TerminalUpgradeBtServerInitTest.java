package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class TerminalUpgradeBtServerInitTest {

    @Tested
    private TerminalUpgradeBtServerInit init;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private TerminalComponentUpgradeCacheInit upgradeCacheInit;

    @Mocked
    private ShellCommandRunner runner;

    /**
     * 测试safeInit，开发环境
     * 
     * @param enviroment mock对象
     */
    @Test
    public void testSafeInitIsDevelop(@Mocked Enviroment enviroment) {

        new Expectations() {
            {
                Enviroment.isDevelop();
                result = true;
            }
        };
        init.safeInit();

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，获取本地ip失败
     * 
     * @param enviroment mock对象
     * @param inetAddress mock对象
     * @throws UnknownHostException 异常
     */
    @Test
    public void testSafeInitGetLocalAddrFail(@Mocked Enviroment enviroment, @Mocked InetAddress inetAddress) throws UnknownHostException {

        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                InetAddress.getLocalHost();
                result = new UnknownHostException();
            }
        };
        try {
            init.safeInit();
            fail();
        } catch (RuntimeException e) {
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip为空
     * 
     * @param enviroment mock对象
     * @param inetAddress mock对象
     * @throws UnknownHostException 异常
     */
    @Test
    public void testSafeInitIpIsBlank(@Mocked Enviroment enviroment, @Mocked InetAddress inetAddress) throws UnknownHostException {
        byte[] ipArr = new byte[4];
        ipArr[0] = -84;
        ipArr[1] = 12;
        ipArr[2] = 22;
        ipArr[3] = 45;
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                InetAddress.getLocalHost();
                result = inetAddress;
                inetAddress.getAddress();
                result = ipArr;
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                result = "";
            }
        };
        new MockUp<TerminalUpgradeBtServerInit>() {
            @Mock
            public void executeUpdate() {

            }
        };
        try {
            init.safeInit();
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s", "/data/web/rcdc/shell/update.py"));
                times = 0;
                upgradeCacheInit.safeInit();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip一致
     * 
     * @param enviroment mock对象
     * @param inetAddress mock对象
     * @throws UnknownHostException 异常
     */
    @Test
    public void testSafeInitIpEqualsCurrentIp(@Mocked Enviroment enviroment, @Mocked InetAddress inetAddress) throws UnknownHostException {
        byte[] ipArr = new byte[4];
        ipArr[0] = -84;
        ipArr[1] = 12;
        ipArr[2] = 22;
        ipArr[3] = 45;
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                InetAddress.getLocalHost();
                result = inetAddress;
                inetAddress.getAddress();
                result = ipArr;
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                result = "172.12.22.45";
            }
        };
        try {
            init.safeInit();
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s", "/data/web/rcdc/shell/update.py"));
                times = 0;
                upgradeCacheInit.safeInit();
                times = 1;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip不同,executeUpdate有BusinessException
     * 
     * @param enviroment mock对象
     * @param inetAddress mock对象
     * @throws UnknownHostException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitIpDifferentCurrentIpExecuteUpdateHasBusinessException(@Mocked Enviroment enviroment, @Mocked InetAddress inetAddress)
            throws UnknownHostException, BusinessException {
        byte[] ipArr = new byte[4];
        ipArr[0] = -84;
        ipArr[1] = 12;
        ipArr[2] = 22;
        ipArr[3] = 45;
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                InetAddress.getLocalHost();
                result = inetAddress;
                inetAddress.getAddress();
                result = ipArr;
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                result = "172.22.25.45";
                runner.execute((TerminalUpgradeBtServerInit.BtShareInitReturnValueResolver) any);
                result = new BusinessException("key");
            }
        };
        try {
            init.safeInit();
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s", "/data/web/rcdc/shell/update.py"));
                times = 1;
                upgradeCacheInit.safeInit();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip不同
     * 
     * @param enviroment mock对象
     * @param inetAddress mock对象
     * @throws UnknownHostException 异常
     */
    @Test
    public void testSafeInitIpDifferentCurrentIp(@Mocked Enviroment enviroment, @Mocked InetAddress inetAddress) throws UnknownHostException {
        byte[] ipArr = new byte[4];
        ipArr[0] = -84;
        ipArr[1] = 12;
        ipArr[2] = 22;
        ipArr[3] = 45;
        new Expectations() {
            {
                Enviroment.isDevelop();
                result = false;
                InetAddress.getLocalHost();
                result = inetAddress;
                inetAddress.getAddress();
                result = ipArr;
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                result = "172.22.25.45";
            }
        };
        try {
            init.safeInit();
        } catch (RuntimeException e) {
            fail();
            assertEquals("get localhost address error,", e.getMessage());
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s", "/data/web/rcdc/shell/update.py"));
                times = 1;
                upgradeCacheInit.safeInit();
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
        TerminalUpgradeBtServerInit.BtShareInitReturnValueResolver resolver = init.new BtShareInitReturnValueResolver();
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
        TerminalUpgradeBtServerInit.BtShareInitReturnValueResolver resolver = init.new BtShareInitReturnValueResolver();
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
        TerminalUpgradeBtServerInit.BtShareInitReturnValueResolver resolver = init.new BtShareInitReturnValueResolver();
        new MockUp<TerminalUpgradeBtServerInit>() {
            @Mock
            public String getLocalIP() {
                return "192.168.1.2";
            }
        };

        resolver.resolve("dsd", 0, "dsd");

        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.RCDC_SERVER_IP_GLOBAL_PARAMETER_KEY, "192.168.1.2");
                times = 1;
                upgradeCacheInit.safeInit();
                times = 1;
            }
        };
    }
}
