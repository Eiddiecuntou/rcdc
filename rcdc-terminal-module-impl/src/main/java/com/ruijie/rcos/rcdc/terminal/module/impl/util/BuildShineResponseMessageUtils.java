package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalPassword;

/**
 * 
 * Description: 构建与shine通信的响应数据
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月16日
 * 
 * @author nt
 */
public class BuildShineResponseMessageUtils {

    public static CbbResponseShineMessage buildResponseMessage(CbbDispatcherRequest request, Object content) {
        Assert.notNull(request, "request can not be null");
        
        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage();
        responseMessage.setAction(request.getDispatcherKey());
        responseMessage.setRequestId(request.getRequestId());
        responseMessage.setTerminalId(request.getTerminalId());
        responseMessage.setCode(Constants.SUCCESS);
        responseMessage.setContent(content);
        
        return responseMessage;
    }
}
