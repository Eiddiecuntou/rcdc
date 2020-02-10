package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import org.junit.Test;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.message.CommonMessageCode;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
public class MessageUtilsTest {

    /**
     * 测试转换数据Data为空
     */
    @Test
    public void testParseWithDataIsEmpty() {
        String obj = "";
        try {
            MessageUtils.parse(obj, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "data不能为空");
        }
    }

    /**
     * 测试转换数据Data为空
     */
    @Test
    public void testParseWithDataIsNull() {
        try {
            MessageUtils.parse(null, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "data不能为null");
        }
    }

    /**
     * 测试转换数据DataFormat错误
     */
    @Test
    public void testParseWithDataFormatError() {
        try {
            String data = "hello";
            MessageUtils.parse(data, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "报文数据格式转换错误；data:[hello]");
        }
    }

    /**
     * 测试转换数据class,data为空
     */
    @Test
    public void testParseWithDataClassIsNull() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, null);
            assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试转换数据class不为空
     */
    @Test
    public void testParseWithDataClassIsNotNull() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            response.setContent("");
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            assertEquals(e.getMessage(), "content内容不能为空；content:");
        }
    }

    /**
     * 测试转换数据class为null
     */
    @Test
    public void testParseWithDataAndClassIsNotNull() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            response.setContent(null);
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            assertEquals(e.getMessage(), "content内容不能为空；content:null");
        }
    }

    /**
     * 测试转换数据不为空
     */
    @Test
    public void testParseWithDataAndClassIsNotNullAndContentIsNotEmpty() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            response.setContent("hello");
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            assertEquals(e.getMessage(), "报文数据格式转换错误；data:[hello]");
        }
    }

    /**
     * 测试转换
     */
    @Test
    public void testParseNormal() {
        try {
            CbbShineMessageResponse<User> response = new CbbShineMessageResponse();
            response.setCode(200);
            User user = new User();
            user.setId(100);
            user.setName("jarman");
            response.setContent(user);
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            assertEquals(response.getCode(), result.getCode());
            assertEquals(response.getContent().name, user.getName());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * testBuildErrorResponseMessage
     */
    @Test
    public void testBuildErrorResponseMessage() {
        CbbDispatcherRequest request = new CbbDispatcherRequest();
        request.setTerminalId("terminalId");
        request.setRequestId("requestId");
        request.setDispatcherKey("dispatchKey");

        CbbResponseShineMessage message = MessageUtils.buildErrorResponseMessage(request);

        assertEquals(request.getTerminalId(), message.getTerminalId());
        assertEquals(request.getRequestId(), message.getRequestId());
        assertEquals(request.getDispatcherKey(), message.getAction());
        assertEquals(CommonMessageCode.CODE_ERR_OTHER, message.getCode().intValue());
    }

    /**
     * 
     * Description: Function Description
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年1月21日
     * 
     * @author nt
     */
    public static class User {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
