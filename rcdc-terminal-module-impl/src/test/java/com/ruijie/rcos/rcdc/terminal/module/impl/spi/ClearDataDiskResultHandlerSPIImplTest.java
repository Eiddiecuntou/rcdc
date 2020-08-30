package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/1 16:24
 *
 * @author conghaifeng
 */
@RunWith(SkyEngineRunner.class)
public class ClearDataDiskResultHandlerSPIImplTest {

    @Tested
    private ClearDataDiskResultHandlerSPIImpl handlerSPI;

    @Injectable
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    @Mocked
    private LocaleI18nResolver localeI18nResolver;

    /**
     *测试dispatch,正常流程
     */
    @Test
    public void testDispatch() {
        CbbDispatcherRequest cbbDispatcherRequest = new CbbDispatcherRequest();
        cbbDispatcherRequest.setData("{\"clearResult\":true}");
        cbbDispatcherRequest.setTerminalId("terminalId");
        handlerSPI.dispatch(cbbDispatcherRequest);
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 1;
            }
        };
    }

    /**
     *测试dispatch,shine返回值为空
     */
    @Test
    public void testDispatchWithNullResponse() {
        CbbDispatcherRequest cbbDispatcherRequest = new CbbDispatcherRequest();
        cbbDispatcherRequest.setData("{\"clearResult\":null}");
        cbbDispatcherRequest.setTerminalId("terminalId");
        handlerSPI.dispatch(cbbDispatcherRequest);
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 0;
            }
        };
    }

    /**
     *测试dispatch,清空数据盘失败
     */
    @Test
    public void testDispatchWhileClearFail() {
        CbbDispatcherRequest cbbDispatcherRequest = new CbbDispatcherRequest();
        cbbDispatcherRequest.setData("{\"clearResult\":false}");
        cbbDispatcherRequest.setTerminalId("terminalId");
        handlerSPI.dispatch(cbbDispatcherRequest);
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 1;
            }
        };
    }
}