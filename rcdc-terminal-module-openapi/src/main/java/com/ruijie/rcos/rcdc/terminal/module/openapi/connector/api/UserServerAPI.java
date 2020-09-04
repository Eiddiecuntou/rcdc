package com.ruijie.rcos.rcdc.terminal.module.openapi.connector.api;

import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.connectkit.api.annotation.ApiAction;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.SessionAlias;
import com.ruijie.rcos.sk.connectkit.api.annotation.tcp.Tcp;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/8/24
 *
 * @author hs
 */
@Tcp
public interface UserServerAPI {

    /**
     * 服务端主动发送消息
     * 
     * @param msg msg
     * @param sessionAlias sessionAlias
     *
     * @throws BusinessException BusinessException
     * @return boolean
     */
    @ApiAction("userMsgAlias")
    boolean sendMsgByAlias(String msg, @SessionAlias String sessionAlias) throws BusinessException;

    /**
     * 服务端主动发送消息
     *
     * @param msg msg
     * @param session session
     *
     * @throws BusinessException BusinessException
     * @return boolean
     */
    @ApiAction("userMsgSession")
    boolean sendMsgBySession(String msg, Session session) throws BusinessException;
}
