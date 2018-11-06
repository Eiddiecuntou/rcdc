package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.NoticeRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherInterface;

/**
 * Description: 终端事件通知SPI接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@DispatcherInterface
public interface TerminalEventNoticeSPI {

    /**
     * 消息通知
     * @param request
     */
    void notify(final NoticeRequest request);
}
