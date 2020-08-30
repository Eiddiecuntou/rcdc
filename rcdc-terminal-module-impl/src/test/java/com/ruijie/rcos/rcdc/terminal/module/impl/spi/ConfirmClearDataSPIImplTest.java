package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import org.junit.Test;

import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.codec.compatible.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/18 17:29
 *
 * @author conghaifeng
 */
public class ConfirmClearDataSPIImplTest {

    @Tested
    private ConfirmClearDataSPIImpl confirmClearDataSPI;

    @Injectable
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;
    
    /**
     *测试dispatch，确认数据盘清空
     */
    @Test
    public void testDispatchWhileConfirmClear(@Mocked LocaleI18nResolver localeI18nResolver) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData("{\"enableClear\":true}");
        request.setTerminalId("terminalId");
        confirmClearDataSPI.dispatch(request);
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 1;
            }
        };
    }

    /**
     *测试dispatch，取消数据盘清空
     */
    @Test
    public void testDispatchWhileCancelClear(@Mocked LocaleI18nResolver localeI18nResolver) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData("{\"enableClear\":false}");
        request.setTerminalId("terminalId");
        confirmClearDataSPI.dispatch(request);
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 1;
            }
        };
    }

    /**
     *测试dispatch，shine返回的报文为空
     */
    @Test
    public void testDispatchWhileParseObjectIsNull(@Mocked LocaleI18nResolver localeI18nResolver) {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setData("{\"enableClear\":null}");
        request.setTerminalId("terminalId");
        confirmClearDataSPI.dispatch(request);
        new Verifications() {
            {
                baseSystemLogMgmtAPI.createSystemLog((BaseCreateSystemLogRequest) any);
                times = 0;
            }
        };
    }
    

}