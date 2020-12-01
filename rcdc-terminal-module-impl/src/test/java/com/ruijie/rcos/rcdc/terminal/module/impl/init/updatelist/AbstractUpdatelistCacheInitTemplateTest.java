package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.BaseUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;

/**
 * Description: Function Description Copyright: Copyright (c) 2018 Company:
 * Ruijie Co., Ltd. Create Time: 2019/8/8
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AbstractUpdatelistCacheInitTemplateTest {

    /**
     * testInitWhileUpdateListFileIsNotFile
     *
     * @throws IOException exception
     */
    @Test
    public void testInitWhileUpdateListFileIsNotFile() throws IOException {

        new MockUp<File>() {
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

        new Verifications() {
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 0;
            }
        };

    }

    /**
     * testInitWhileReadUpdateListFileContentError
     *
     * @throws IOException exception
     */
    @Test
    public void testInitWhileReadUpdateListFileContentError() throws IOException {

        new MockUp<File>() {
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

        new Verifications() {
            {
                JSON.parseObject(anyString, (Class) any);
                times = 0;
            }
        };
    }

    /**
     * testInitWhileUpdateListParseObjectIsNull
     *
     * @throws IOException exception
     */
    @Test
    public void testInitWhileUpdateListParseObjectIsNull() throws IOException {

        new MockUp<File>() {
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
            public void fillUpdateList(BaseUpdateListDTO updatelist) {
                throw new RuntimeException("aaa");
            }
        };

        try {
            TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
            cacheInit.init();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        new Verifications() {
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 1;

                JSON.parseObject(anyString, (Class) any);
                times = 1;
            }
        };

    }

    /**
     * testInitWhileComponentListIsEmpty
     *
     * @throws IOException exception
     */
    @Test
    public void testInitWhileComponentListIsEmpty() throws IOException {

        new MockUp<File>() {
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
                return new BaseUpdateListDTO();
            }
        };

        new MockUp(TestedUpdatelistCacheInit.class) {
            @Mock
            public void fillUpdateList(BaseUpdateListDTO updatelist) {
                throw new RuntimeException("aaa");
            }
        };

        try {
            TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
            cacheInit.init();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        new Verifications() {
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 1;

                JSON.parseObject(anyString, (Class) any);
                times = 1;
            }
        };

    }

    /**
     * testInit
     *
     * @throws IOException exception
     */
    @Test
    public void testInit() throws IOException {

        new MockUp<File>() {
            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp(FileUtils.class) {
            @Mock
            public String readFileToString(File file, Charset charset) {
                return "{\"componentList\":[{\"md5\":null,\"name\":null,\"platform\":null,\"version\":\"aaa\"}],"
                        + "\"componentSize\":null,\"limitVersion\":null,\"validateMd5\":null,\"version\":\"123\"}";
            }
        };

        try {
            TestedUpdatelistCacheInit cacheInit = new TestedUpdatelistCacheInit();
            cacheInit.init();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        new Verifications() {
            {
                FileUtils.readFileToString((File) any, (Charset) any);
                times = 1;

                JSON.parseObject(anyString, (Class) any);
                times = 1;
            }
        };

    }

    /**
     * Description: Function Description Copyright: Copyright (c) 2018 Company:
     * Ruijie Co., Ltd. Create Time: 2019/8/8
     *
     * @author nt
     */
    class TestedUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<BaseUpdateListDTO> {

        @Override
        protected String getUpdateListPath() {
            return "ssss";
        }

        @Override
        protected void fillUpdateList(BaseUpdateListDTO updatelist) {

        }

        @Override
        protected CbbTerminalOsTypeEnums getTerminalOsType() {
            return CbbTerminalOsTypeEnums.WINDOWS;
        }
    }
}
