package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.RcosGlobalPlatformId;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertTrue;

@RunWith(SkyEngineRunner.class)
public class SyncRcdcServerIdHandlerSPIImplTest {

    @Tested
    private SyncRcdcServerIdHandlerSPIImpl spi;

    @Injectable
    private Logger logger;

    @Injectable
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    /**
     * 测试dispatch,参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testDispatchArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> spi.dispatch(null), "CbbDispatcherRequest不能为空");
        assertTrue(true);
    }

    /**
     * 文件不存在
     */
    @Test
    public void testDispatchFileNotExist() {
        new MockUp<File>() {

            @Mock
            public boolean exists() {
                return false;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        spi.dispatch(request);

        new Verifications() {{
            messageHandlerAPI.response((CbbResponseShineMessage) any);
            times = 0;
        }};
    }

    /**
     * 文件路径为目录
     */
    @Test
    public void testDispatchFileIsDir() {
        new MockUp<File>() {

            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isFile() {
                return false;
            }
        };
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        spi.dispatch(request);

        new Verifications() {{
            messageHandlerAPI.response((CbbResponseShineMessage) any);
            times = 0;
        }};
    }

    /**
     * 文件存在，读文件内容异常
     */
    @Test
    public void testDispatchReadFileException() {
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp<FileUtils>() {
            @Mock
            public String readFileToString(File file, Charset encoding) throws IOException {
                throw new IOException("xxx");
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        spi.dispatch(request);

        new Verifications() {{
            messageHandlerAPI.response((CbbResponseShineMessage) any);
            times = 0;
        }};
    }

    /**
     * 正常读取文件并返回
     */
    @Test
    public void testDispatchReadFile() {
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp<FileUtils>() {
            @Mock
            public String readFileToString(File file, Charset encoding) throws IOException {
                RcosGlobalPlatformId platformId = new RcosGlobalPlatformId();
                platformId.setPlatformId("12324655");
                platformId.setManaged(true);
                return JSON.toJSONString(platformId);
            }
        };

        CbbDispatcherRequest request = new CbbDispatcherRequest();
        spi.dispatch(request);

        new Verifications() {{
            messageHandlerAPI.response((CbbResponseShineMessage) any);
            times = 1;
        }};
    }
}
