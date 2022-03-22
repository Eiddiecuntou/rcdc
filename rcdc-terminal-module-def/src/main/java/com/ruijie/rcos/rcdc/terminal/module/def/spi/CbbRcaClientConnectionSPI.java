package com.ruijie.rcos.rcdc.terminal.module.def.spi;

/**
 * Description: rca-client连接spi
 * Copyright: Copyright (c) 2022
 * Company: RuiJie Co., Ltd.
 * Create Time: 2022/3/11 6:03 下午
 *
 * @author zhouhuan
 */
public interface CbbRcaClientConnectionSPI {

    /**
     * 是否是rca-client的连接
     * @param id 会话id
     * @return true-是；false-否
     */
    boolean isRcaClientConnection(String id);

    /**
     * 通知rca-client连接断开事件
     * @param id 会话id
     * @return object 返回值无意义。spi所有方法的返回值只能全为void或者全不为void，因此添加的返回值
     */
    Object notifyRcaClientDisconnect(String id);
}
