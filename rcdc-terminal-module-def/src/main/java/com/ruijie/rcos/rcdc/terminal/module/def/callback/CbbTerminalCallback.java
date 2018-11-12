package com.ruijie.rcos.rcdc.terminal.module.def.callback;

import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Callback;

/**
 * Description: 异步请求终端回调接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public interface CbbTerminalCallback extends Callback {

    /**
     * 请求成功响应回调方法
     *
     * @param msg 消息对象
     */
    void success(CbbShineMessageResponse msg);

    /**
     * 请求超时
     */
    void timeout();


}
