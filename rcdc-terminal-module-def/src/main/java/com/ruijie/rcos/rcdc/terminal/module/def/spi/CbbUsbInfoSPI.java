package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbUsbInfoRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherInterface;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/26
 *
 * @author Jarman
 */
@DispatcherInterface
public interface CbbUsbInfoSPI {

    /**
     * 接收usb信息
     * @param request 请求参数
     */
    void receiveUsbInfo(CbbUsbInfoRequest request);
}
