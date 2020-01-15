package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CommonMessageCode;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: 消息处理工具
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
public class MessageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);


    /**
     * shine应答消息解析
     *
     * @param data 待解析的消息
     * @param clz 消息对应的类
     * @param <T> 消息对应的实体类
     * @return 返回消息对象
     */
    public static <T> CbbShineMessageResponse parse(String data, @Nullable Class<T> clz) {
        Assert.notNull(data, "data不能为null");
        Assert.hasText(data, "data不能为空");
        CbbShineMessageResponse<T> response;
        try {
            response = JSON.parseObject(data, CbbShineMessageResponse.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("报文数据格式转换错误；data:[" + data + "]", e);
        }
        if (clz == null) {
            LOGGER.info("Class<T>参数未传；data:{}", data);
            return response;
        }
        T content = response.getContent();
        if (content == null || StringUtils.isBlank(content.toString())) {
            throw new IllegalArgumentException("content内容不能为空；content:" + content);
        }

        T t;
        try {
            t = JSON.parseObject(content.toString(), clz);
        } catch (Exception e) {
            throw new IllegalArgumentException("报文数据格式转换错误；data:[" + content.toString() + "]", e);
        }
        response.setContent(t);
        return response;
    }

    /**
     * 构造shine应答消息
     * 
     * @param request 请求参数
     * @param content 响应内容
     * @return shine应答消息
     */
    public static CbbResponseShineMessage buildResponseMessage(CbbDispatcherRequest request, Object content) {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(content, "content can not be null");

        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage();
        responseMessage.setAction(request.getDispatcherKey());
        responseMessage.setRequestId(request.getRequestId());
        responseMessage.setTerminalId(request.getTerminalId());
        responseMessage.setCode(CommonMessageCode.SUCCESS);
        responseMessage.setContent(content);

        return responseMessage;
    }

    /**
     * 构造异常情况shine应答消息
     *
     * @param request 请求参数
     * @return shine应答消息
     */
    public static CbbResponseShineMessage buildErrorResponseMessage(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbResponseShineMessage responseMessage = new CbbResponseShineMessage();
        responseMessage.setAction(request.getDispatcherKey());
        responseMessage.setRequestId(request.getRequestId());
        responseMessage.setTerminalId(request.getTerminalId());
        responseMessage.setCode(CommonMessageCode.CODE_ERR_OTHER);

        return responseMessage;
    }

}
