package com.ruijie.rcos.rcdc.terminal.module.impl.init;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import org.junit.Test;
import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.AndroidVDIUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxIDVUpdatelistCacheInit;
import com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist.LinuxVDIUpdatelistCacheInit;
import com.ruijie.rcos.sk.base.env.Enviroment;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.shell.ShellCommandRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 *
 * @author ls
 */
public class TerminalComponentUpgradeInitTest {

    @Tested
    private TerminalComponentUpgradeInit init;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private LinuxVDIUpdatelistCacheInit linuxVDIUpdatelistCacheInit;

    @Injectable
    private AndroidVDIUpdatelistCacheInit androidVDIUpdatelistCacheInit;

    @Mocked
    private ShellCommandRunner runner;

    @Injectable
    private CloudPlatformMgmtAPI cloudPlatformMgmtAPI;

    @Injectable
    private LinuxIDVUpdatelistCacheInit linuxIDVUpdatelistCacheInit;

    /**
     * 测试safeInit，开发环境
     *
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIsDevelop() throws InterruptedException {
        setEnviromentDevelop(true);
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
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitGetLocalAddrFail() throws BusinessException, InterruptedException {
        setEnviromentDevelop(false);
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = null;
            }
        };
        try {
            init.safeInit();
            Thread.sleep(1000);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
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
            init.safeInit();
            Thread.sleep(1000);
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
            init.safeInit();
            Thread.sleep(1000);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 3;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateLinuxVDI.py", "172.12.22.45"));
                times = 1;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateAndroidVDI.py", "172.12.22.45"));
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
            init.safeInit();
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 3;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateLinuxVDI.py", "172.12.22.45"));
                times = 0;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateAndroidVDI.py", "172.12.22.45"));
                times = 0;
                linuxVDIUpdatelistCacheInit.init();
                times = 1;
                androidVDIUpdatelistCacheInit.init();
                times = 1;
                linuxIDVUpdatelistCacheInit.init();
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
            init.safeInit();
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 3;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateLinuxVDI.py", "172.12.22.45"));
                times = 1;
                runner.setCommand(String.format("python %s %s", "/data/web/rcdc/shell/updateAndroidVDI.py", "172.12.22.45"));
                times = 1;
            }
        };
    }
    
    /**
     * 测试safeInit，ip和本地ip不同,executeUpdate有BusinessException
     *
     * @throws BusinessException 异常
     * @throws InterruptedException ex
     */
    @Test
    public void testSafeInitIpDifferentCurrentIpExecuteUpdateHasBusinessException()
            throws BusinessException, InterruptedException {
        setEnviromentDevelop(false);
        ClusterVirtualIpDTO dto = new ClusterVirtualIpDTO();
        dto.setClusterVirtualIpIp("172.12.22.45");
        new Expectations() {
            {
                cloudPlatformMgmtAPI.getClusterVirtualIp((Request) any);
                result = DtoResponse.success(dto);
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                result = "172.22.25.45";
                runner.execute((TerminalComponentUpgradeInit.BtShareInitReturnValueResolver) any);
                result = new BusinessException("key");
            }
        };
        try {
            init.safeInit();
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 3;

                runner.execute((TerminalComponentUpgradeInit.BtShareInitReturnValueResolver) any);
                times = 3;
                linuxVDIUpdatelistCacheInit.init();
                times = 0;
                androidVDIUpdatelistCacheInit.init();
                times = 0;
                linuxIDVUpdatelistCacheInit.init();
                times = 0;
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
            init.safeInit();
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            fail();
        }

        new Verifications() {
            {
                globalParameterAPI.findParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY);
                times = 3;
                runner.execute((TerminalComponentUpgradeInit.BtShareInitReturnValueResolver) any);
                times = 3;
                linuxVDIUpdatelistCacheInit.init();
                times = 0;
                androidVDIUpdatelistCacheInit.init();
                times = 0;
                linuxIDVUpdatelistCacheInit.init();
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
        TerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolver =
                init.new BtShareInitReturnValueResolver(CbbTerminalTypeEnums.VDI_LINUX);
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
        TerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolver =
                init.new BtShareInitReturnValueResolver(CbbTerminalTypeEnums.VDI_LINUX);
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
        TerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolverLinuxVDI =
                init.new BtShareInitReturnValueResolver(CbbTerminalTypeEnums.VDI_LINUX);
        TerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolverAndroidVDI =
                init.new BtShareInitReturnValueResolver(CbbTerminalTypeEnums.VDI_ANDROID);
        TerminalComponentUpgradeInit.BtShareInitReturnValueResolver resolverLinuxIDV =
                init.new BtShareInitReturnValueResolver(CbbTerminalTypeEnums.IDV_LINUX);
        new MockUp<TerminalComponentUpgradeInit>() {
            @Mock
            public String getLocalIP() {
                return "192.168.1.2";
            }
        };

        resolverLinuxVDI.resolve("dsd", 0, "success");
        resolverAndroidVDI.resolve("dsd", 0, "success");
        resolverLinuxIDV.resolve("aa", 0, "success");

        new Verifications() {
            {
                globalParameterAPI.updateParameter(Constants.RCDC_CLUSTER_VIRTUAL_IP_GLOBAL_PARAMETER_KEY, "192.168.1.2");
                times = 3;
                linuxVDIUpdatelistCacheInit.init();
                times = 1;
                androidVDIUpdatelistCacheInit.init();
                times = 1;
                linuxIDVUpdatelistCacheInit.init();
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
