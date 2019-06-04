package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.ComponentUpdateListCacheManager;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalComponentUpgradeCacheInitTest {

    @Tested
    private TerminalComponentUpgradeCacheInit init;

    @Injectable
    private ComponentUpdateListCacheManager cacheManager;

    /**
     * 测试safeInit,upgradeDirectory不是目录
     */
    @Test
    public void testSafeInitUpgradeDirectoryIsNotDir() {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,subfileArr为空
     */
    @Test
    public void testSafeInitsubfileArrIsEmpty() {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                return new File[0];
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,subFile不是目录
     */
    @Test
    public void testSafeInitSubFileIsNotDir() {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory(Invocation invocation) {
                Assert.notNull(invocation, "invocation can not be null");
                File file = invocation.getInvokedInstance();
                File file1 = new File(Constants.TERMINAL_TERMINAL_COMPONET_UPGRADE_PATH);
                if (file1.getPath().equals(file.getPath())) {
                    return true;
                }
                return false;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,updateListFile不是文件
     */
    @Test
    public void testSafeInitUpdateListFileIsNotFile() {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return false;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,IOException
     * 
     * @param utils mock FileUtils
     * @throws IOException 异常
     */
    @Test
    public void testSafeInitIoException(@Mocked FileUtils utils) throws IOException {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new Expectations() {
            {
                FileUtils.readFileToString((File) any, Charset.forName("UTF-8"));
                result = new IOException();
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,componentVersionList为空
     * 
     * @param utils mock FileUtils
     * @throws IOException 异常
     */
    @Test
    public void testSafeInitComponentVersionListIsEmpty(@Mocked FileUtils utils) throws IOException {
        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) throws IOException {
                return "md5".getBytes();
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };
        CbbTerminalComponentUpdateListDTO dto = new CbbTerminalComponentUpdateListDTO();
        dto.setComponentList(Collections.emptyList());
        String updatelistStr = JSONObject.toJSONString(dto);
        new Expectations() {
            {
                FileUtils.readFileToString((File) any, Charset.forName("UTF-8"));
                result = updatelistStr;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 0;
            }
        };
    }

    /**
     * 测试safeInit,platformArrStr为空
     * 
     * @param utils mock FileUtils
     * @throws IOException 异常
     */
    @Test
    public void testSafeInitPlatformArrStrIsBlank(@Mocked FileUtils utils) throws IOException {
        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) throws IOException {
                return "md5".getBytes();
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };
        CbbTerminalComponentUpdateListDTO dto = new CbbTerminalComponentUpdateListDTO();
        List<CbbTerminalComponentVersionInfoDTO> componentVersionList = new ArrayList<>();
        componentVersionList.add(new CbbTerminalComponentVersionInfoDTO());
        dto.setComponentList(componentVersionList);
        String updatelistStr = JSONObject.toJSONString(dto);
        new Expectations() {
            {
                FileUtils.readFileToString((File) any, Charset.forName("UTF-8"));
                result = updatelistStr;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 2;
            }
        };
    }

    /**
     * 测试safeInit,platformStr为空或非法平台信息
     * 
     * @param utils mock FileUtils
     * @throws IOException 异常
     */
    @Test
    public void testSafeInitPlatformStrIsBlank(@Mocked FileUtils utils) throws IOException {
        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) throws IOException {
                return "md5".getBytes();
            }
        };
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };
        CbbTerminalComponentUpdateListDTO dto = new CbbTerminalComponentUpdateListDTO();
        List<CbbTerminalComponentVersionInfoDTO> componentVersionList = new ArrayList<>();
        CbbTerminalComponentVersionInfoDTO versionInfoDTO = new CbbTerminalComponentVersionInfoDTO();
        versionInfoDTO.setPlatform("&465");
        componentVersionList.add(versionInfoDTO);
        dto.setComponentList(componentVersionList);
        String updatelistStr = JSONObject.toJSONString(dto);
        new Expectations() {
            {
                FileUtils.readFileToString((File) any, Charset.forName("UTF-8"));
                result = updatelistStr;
            }
        };
        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 2;
            }
        };
    }

    /**
     * 测试safeInit,
     * 
     * @param utils mock FileUtils
     * @throws IOException 异常
     */
    @Test
    public void testSafeInit(@Mocked FileUtils utils) throws IOException {

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) throws IOException {
                return "md5".getBytes();
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[2];
                fileArr[0] = new File("/1");
                fileArr[1] = new File("/2");
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        CbbTerminalComponentUpdateListDTO dto = new CbbTerminalComponentUpdateListDTO();
        List<CbbTerminalComponentVersionInfoDTO> componentVersionList = new ArrayList<>();
        CbbTerminalComponentVersionInfoDTO versionInfoDTO = new CbbTerminalComponentVersionInfoDTO();
        versionInfoDTO.setPlatform("ALL");
        componentVersionList.add(versionInfoDTO);
        dto.setComponentList(componentVersionList);
        String updatelistStr = JSONObject.toJSONString(dto);
        Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> caches = new HashMap<>();

        new Expectations() {
            {
                FileUtils.readFileToString((File) any, Charset.forName("UTF-8"));
                result = updatelistStr;
                cacheManager.getUpdateListCaches();
                result = caches;
            }
        };

        init.safeInit();
        new Verifications() {
            {
                cacheManager.getUpdateListCaches();
                times = 2;
            }
        };
    }
}
