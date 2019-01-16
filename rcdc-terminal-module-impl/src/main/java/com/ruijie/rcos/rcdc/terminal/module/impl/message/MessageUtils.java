package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

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
     * @param clz  消息对应的类
     * @param <T>  消息对应的实体类
     * @return 返回消息对象
     */
    public static <T> CbbShineMessageResponse parse(Object data, @Nullable Class<T> clz) {
        Assert.notNull(data, "data不能为null");
        Assert.hasText(data.toString(), "data不能为空");
        CbbShineMessageResponse<T> response;
        try {
            response = JSON.parseObject(data.toString(), CbbShineMessageResponse.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("报文数据格式转换错误；data:[" + data.toString() + "]", e);
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
}
