package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;


import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AndroidUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 *
 * @author ls
 */
public class TerminalComponentInitServiceImplTest {

    @Tested
    private TerminalComponentInitServiceImpl initService;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private LinuxUpdatelistCacheInit linuxUpdatelistCacheInit;

    @Injectable
    private AndroidUpdatelistCacheInit androidUpdatelistCacheInit;

    @Mocked
    private ShellCommandRunner runner;

    @Injectable
    private CloudPlatformMgmtAPI cloudPlatformMgmtAPI;

    @Before
    public void before() {
        Deencapsulation.setField(TerminalComponentInitServiceImpl.class, "EXECUTOR_SERVICE", new MockExecutor4Test());
    }

    /**
     * 测试safeInit，开发环境
     *
     * @throws InterruptedException ex
     */
    @Test
    public void testInitAndroidVDI() throws InterruptedException {
        setEnviromentDevelop(true);
        initService.initAndroid();

        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，开发环境
     *
     * @throws InterruptedException ex
     */
    @Test
    public void testInitLinuxVDI() throws InterruptedException {
        setEnviromentDevelop(true);
        initService.initLinux();

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
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitGetLocalAddrFail() throws BusinessException, InterruptedException {
        setEnviromentDevelop(false);
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = new BusinessException("123");
            }
        };
        try {
            initService.initAndroid();
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(anyString);
                times = 0;

                globalParameterAPI.updateParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY, anyString);
                times = 0;
            }
        };

        // 获取本地IP抛异常
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = new BusinessException("key", "args");
            }
        };
        try {
            initService.initLinux();
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit，ip为空
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitIpIsBlank() throws BusinessException {
        setEnviromentDevelop(false);
        ClusterVirtualIpDTO dto = new ClusterVirtualIpDTO();
        dto.setClusterVirtualIpIp("172.12.22.45");
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = DtoResponse.success(dto);
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                result = "";
            }
        };

        try {
            initService.initLinux();
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s %s %s", "/data/web/rcdc/shell/update_component_package.py", "172.12.22.45", "linux"));
                times = 1;
            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip一致
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpEqualsCurrentIp() throws BusinessException, InterruptedException {
        setEnviromentDevelop(false);
        ClusterVirtualIpDTO dto = new ClusterVirtualIpDTO();
        dto.setClusterVirtualIpIp("172.12.22.45");

        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = DtoResponse.success(dto);
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                result = dto.getClusterVirtualIpIp();
            }
        };
        try {
            initService.initAndroid();
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateLinuxVDI.py", "172.12.22.45"));
                times = 0;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateAndroidVDI.py", "172.12.22.45"));
                times = 0;
                androidUpdatelistCacheInit.init();
                times = 1;

            }
        };
    }

    /**
     * 测试safeInit，ip和本地ip一致，upgradeTempPath不是目录
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpEqualsCurrentIpButNotDirectory() throws BusinessException, InterruptedException {
        setEnviromentDevelop(false);
        ClusterVirtualIpDTO dto = new ClusterVirtualIpDTO();
        dto.setClusterVirtualIpIp("172.12.22.45");
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
        };
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = DtoResponse.success(dto);
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                result = dto.getClusterVirtualIpIp();
            }
        };
        try {
            initService.initLinux();
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.setCommand(String.format("python %s %s %s", "/data/web/rcdc/shell/update_component_package.py", "172.12.22.45", "linux"));
                times = 1;
            }
        };
    }
    
    /**
     * 测试safeInit，ip和本地ip不同
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpDifferentCurrentIp() throws BusinessException, InterruptedException {
        setEnviromentDevelop(false);
        ClusterVirtualIpDTO dto = new ClusterVirtualIpDTO();
        dto.setClusterVirtualIpIp("172.12.22.45");
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = DtoResponse.success(dto);
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                result = "172.22.25.45";
            }
        };
        try {
            initService.initLinux();
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 1;
                runner.execute((TerminalComponentInitServiceImpl.BtShareInitReturnValueResolver) any);
                times = 1;
                linuxUpdatelistCacheInit.init();
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
        TerminalComponentInitServiceImpl.BtShareInitReturnValueResolver resolver =
                initService.new BtShareInitReturnValueResolver(CbbTerminalOsTypeEnums.LINUX);
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
        TerminalComponentInitServiceImpl.BtShareInitReturnValueResolver resolver =
                initService.new BtShareInitReturnValueResolver(CbbTerminalOsTypeEnums.LINUX);
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
        TerminalComponentInitServiceImpl.BtShareInitReturnValueResolver resolverLinux =
                initService.new BtShareInitReturnValueResolver(CbbTerminalOsTypeEnums.LINUX);
        TerminalComponentInitServiceImpl.BtShareInitReturnValueResolver resolverAndroid =
                initService.new BtShareInitReturnValueResolver(CbbTerminalOsTypeEnums.ANDROID);
        new MockUp<TerminalComponentInitServiceImpl>() {
            @Mock
            public String getLocalIP() {
                return "192.168.1.2";
            }
        };

        resolverLinux.resolve("dsd", 0, "success");
        resolverAndroid.resolve("dsd", 0, "success");

        new Verifications() {
            {
                linuxUpdatelistCacheInit.init();
                times = 1;
                androidUpdatelistCacheInit.init();
                times = 1;
            }
        };
    }
    
    private void setEnviromentDevelop(boolean isDevelop) {
        new MockUp<Enviroment>() {
            @Mock
            public boolean isDevelop() {
                return isDevelop;
            }
        };
    }
}
