package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbUsbInfoSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbUsbInfoRequest;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;


/**
 * Description: 接收usb信息处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/26
 *
 * @author Jarman
 */
@DispatcherImplemetion(ReceiveTerminalEvent.USB_INFO)
public class UsbInfoHandlerImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsbInfoHandlerImpl.class);

    @Autowired
    private CbbUsbInfoSPI cbbUsbInfoSPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "DispatcherRequest不能为null");
        Assert.hasText(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");
        String data = (String) request.getData();
        LOGGER.debug("接收到的usb报文信息：{}",data);
        CbbUsbInfoRequest cbbUsbInfoRequest = JSON.parseObject(data,CbbUsbInfoRequest.class);
        cbbUsbInfoRequest.setDispatcherKey(NoticeEventEnums.RECEIVE_USB_INFO.getName());
        cbbUsbInfoSPI.receiveUsbInfo(cbbUsbInfoRequest);
    }
}
