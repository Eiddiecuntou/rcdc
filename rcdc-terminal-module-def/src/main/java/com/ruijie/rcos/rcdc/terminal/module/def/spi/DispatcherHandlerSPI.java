package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.DispatcherRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherInterface;


/**
 * Description: 消息分发处理器SPI接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@DispatcherInterface
public interface DispatcherHandlerSPI {

    /**
     * 消息分发方法
     * @param request 请求参数对象 请求参数
     */
    void dispatch(DispatcherRequest request);
}
