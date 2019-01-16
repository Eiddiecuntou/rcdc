package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
public class MessageUtilsTest {

    @Test
    public void testParseWithDataIsEmpty() {
        String obj = "";
        try {
            MessageUtils.parse(obj, null);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "data不能为空");
        }
    }

    @Test
    public void testParseWithDataIsNull() {
        try {
            MessageUtils.parse(null, null);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "data不能为null");
        }
    }

    @Test
    public void testParseWithDataFormatError() {
        try {
            String data = "hello";
            MessageUtils.parse(data, null);
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "报文数据格式转换错误；data:[hello]");
        }
    }

    @Test
    public void testParseWithDataClassIsNull() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, null);
            Assert.assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testParseWithDataClassIsNotNull() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            response.setContent("");
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            Assert.assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "content内容不能为空；content:");
        }
    }

    @Test
    public void testParseWithDataAndClassIsNotNull() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            response.setContent(null);
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            Assert.assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "content内容不能为空；content:null");
        }
    }

    @Test
    public void testParseWithDataAndClassIsNotNullAndContentIsNotEmpty() {
        try {
            CbbShineMessageResponse response = new CbbShineMessageResponse();
            response.setCode(200);
            response.setContent("hello");
            String json = JSON.toJSONString(response);
            CbbShineMessageResponse result = MessageUtils.parse(json, User.class);
            Assert.assertEquals(response.getCode(), result.getCode());
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "报文数据格式转换错误；data:[hello]");
        }
    }

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
            Assert.assertEquals(response.getCode(), result.getCode());
            Assert.assertEquals(response.getContent().name,user.getName());
        } catch (Exception e) {
            fail();
        }
    }

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


