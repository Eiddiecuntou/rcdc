package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdatelistDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.VDITerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/8
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AbstractUpdatelistCacheInitTemplateTest {

    @Test
    public void testInitWhileUpdateListFileIsNotFile() throws IOException {

        new MockUp<File>(){
            @Mock
            public boolean isFile() {
                return false;
            }
        };

        new MockUp(FileUtils.class) {
            @Mock
            public String readFileToString(File file, Charset charset) {
                return "content";
            }
        };

        TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
        cacheInit.init();

        new Verifications(){
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 0;
            }
        };

    }

    @Test
    public void testInitWhileReadUpdateListFileContentError() throws IOException {

        new MockUp<File>(){
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp(FileUtils.class) {
            @Mock
            public String readFileToString(File file, Charset charset) throws IOException {
                throw new IOException("sss");
            }
        };

        new MockUp(JSON.class) {
            @Mock
            public Object parseObject(String content, Class clz) {
                // 测试转换对象为null
                return null;
            }
        };

        TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
        cacheInit.init();

        new Verifications(){
            {
                JSON.parseObject(anyString, (Class) any);
                times = 0;
            }
        };
    }

    @Test
    public void testInitWhileUpdateListParseObjectIsNull() throws IOException {

        new MockUp<File>(){
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp(FileUtils.class) {
            @Mock
            public String readFileToString(File file, Charset charset) {
                return "sss";
            }
        };

        new MockUp(JSON.class) {
            @Mock
            public Object parseObject(String content, Class clz) {
                // 测试转换对象为null
                return null;
            }
        };

        new MockUp(TestedUpdatelistCacheInit.class) {
            @Mock
            public void fillUpdateList(CbbCommonUpdatelistDTO updatelist){
                throw new RuntimeException("aaa");
            }
        };

        try {
            TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
            cacheInit.init();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        new Verifications(){
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 1;

                JSON.parseObject(anyString, (Class) any);
                times = 1;
            }
        };

    }

    @Test
    public void testInitWhileComponentListIsEmpty() throws IOException {

        new MockUp<File>(){
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp(FileUtils.class) {
            @Mock
            public String readFileToString(File file, Charset charset) {
                return "";
            }
        };

        new MockUp(JSON.class) {
            @Mock
            public Object parseObject(String content, Class clz) {
                return new CbbCommonUpdatelistDTO();
            }
        };

        new MockUp(TestedUpdatelistCacheInit.class) {
            @Mock
            public void fillUpdateList(CbbCommonUpdatelistDTO updatelist){
                throw new RuntimeException("aaa");
            }
        };

        try {
            TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
            cacheInit.init();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        new Verifications(){
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 1;

                JSON.parseObject(anyString, (Class) any);
                times = 1;
            }
        };

    }

    @Test
    public void testInit() throws IOException {

        new MockUp<File>(){
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp(FileUtils.class) {
            @Mock
            public String readFileToString(File file, Charset charset) {
                return "{\"componentList\":[{\"md5\":null,\"name\":null,\"platform\":null,\"version\":\"aaa\"}],\"componentSize\":null,\"limitVersion\":null,\"validateMd5\":null,\"version\":\"123\"}";
            }
        };

        try {
            TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
            cacheInit.init();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        new Verifications(){
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 1;

                JSON.parseObject(anyString, (Class) any);
                times = 1;
            }
        };

    }

    class TestedUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CbbCommonUpdatelistDTO> {

        @Override
        protected String getUpdateListPath() {
            return "ssss";
        }

        @Override
        protected void cacheInitPre() {

        }

        @Override
        protected void cacheInitFinished() {

        }

        @Override
        protected Map<CbbTerminalTypeEnums, CbbCommonUpdatelistDTO> getUpdateListCacheManager() {
            return new HashMap<>();
        }

        @Override
        protected void fillUpdateList(CbbCommonUpdatelistDTO updatelist) {

        }

        @Override
        protected CbbTerminalTypeEnums getTerminalType() {
            return CbbTerminalTypeEnums.WINDOWS;
        }
    }
}
