package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbUsbInfoSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbUsbInfoRequest;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/26
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class UsbInfoHandlerSPIImplTest {

    @Tested
    private UsbInfoHandlerSPIImpl usbInfoHandlerSPI;

    @Injectable
    private CbbUsbInfoSPI cbbUsbInfoSPI;

    @Test
    public void testDispatch() {
        String terminalId = "123";
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setDispatcherKey(ReceiveTerminalEvent.USB_INFO);
        request.setTerminalId(terminalId);
        request.setData(warpCbbUsbInfoRequest());
        try {
            usbInfoHandlerSPI.dispatch(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {{
            cbbUsbInfoSPI.receiveUsbInfo((CbbUsbInfoRequest) any);
            times = 1;
        }};

    }

    private String warpCbbUsbInfoRequest() {
        CbbUsbInfoRequest request = new CbbUsbInfoRequest();
        request.setDispatcherKey("test");
        request.setBcdDevice("bcddevice");
        request.setIdProduct("idproduct");

        return JSON.toJSONString(request);
    }
}