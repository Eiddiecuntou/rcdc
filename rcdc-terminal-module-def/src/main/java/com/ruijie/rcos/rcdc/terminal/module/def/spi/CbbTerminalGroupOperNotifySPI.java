package com.ruijie.rcos.rcdc.terminal.module.def.spi;


import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbTerminalGroupOperNotifyRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 终端组操作通知SPI接口定义
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年1月7日
 *
 * @author wjp
 */
public interface CbbTerminalGroupOperNotifySPI {


    /**
     * 消息通知：终端组织架构发生变更（删）
     *
     * @param terminalGroupOperNotifyRequest 入参
     * @return 响应
     */
    DefaultResponse notifyTerminalGroupChange(CbbTerminalGroupOperNotifyRequest terminalGroupOperNotifyRequest);

}
