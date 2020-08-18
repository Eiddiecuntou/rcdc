package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.WinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.WinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/15
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class CbbAppTerminalAPIImplTest {

    @Tested
    private CbbAppTerminalAPIImpl api;

    /**
     *  测试获取windows软终端下载路径-缓存未就绪
     */
    @Test
    public void testGetWindowsAppDownloadUrlCacheIsNotReady() {
        WinAppUpdateListDTO listDTO = new WinAppUpdateListDTO();

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public WinAppUpdateListDTO get(CbbTerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(CbbTerminalTypeEnums type) {
                return false;
            }
        };

        try {
            api.getWindowsAppDownloadUrl();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_WINDOWS_APP_UPDATELIST_CACHE_NOT_READY, e.getKey());
        }
    }

    /**
     *  测试获取windows软终端下载路径-缓存为空
     */
    @Test
    public void testGetWindowsAppDownloadUrlCacheIsEmpty() {

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public WinAppUpdateListDTO get(CbbTerminalTypeEnums type) {
                // 获取updatelist缓存为空
                return null;
            }

            @Mock
            public boolean isCacheReady(CbbTerminalTypeEnums type) {
                return true;
            }
        };

        try {
            api.getWindowsAppDownloadUrl();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COMPONENT_UPDATELIST_CACHE_INCORRECT, e.getKey());
        }
    }

    /**
     *  测试获取windows软终端下载路径-缓存异常
     */
    @Test
    public void testGetWindowsAppDownloadUrlCacheIncorrect() {
        WinAppUpdateListDTO listDTO = new WinAppUpdateListDTO();
        listDTO.setComponentList(null);

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public WinAppUpdateListDTO get(CbbTerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(CbbTerminalTypeEnums type) {
                return true;
            }
        };

        try {
            api.getWindowsAppDownloadUrl();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COMPONENT_UPDATELIST_CACHE_INCORRECT, e.getKey());
        }
    }

    /**
     *  测试获取windows软终端下载路径-文件路径异常
     */
    @Test
    public void testGetWindowsAppDownloadUrlPackageUrlInvalid() {
        WinAppUpdateListDTO listDTO = new WinAppUpdateListDTO();
        List<WinAppComponentVersionInfoDTO> versionInfoDTOList = Lists.newArrayList();
        versionInfoDTOList.add(new WinAppComponentVersionInfoDTO());
        listDTO.setComponentList(versionInfoDTOList);
        listDTO.setCompletePackageName("aaa");

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public WinAppUpdateListDTO get(CbbTerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(CbbTerminalTypeEnums type) {
                return true;
            }
        };

        new MockUp<FileUtils>() {

            @Mock
            public boolean isValidPath(File file) {
                return false;
            }
        };

        try {
            api.getWindowsAppDownloadUrl();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COMPONENT_UPDATELIST_CACHE_INCORRECT, e.getKey());
        }

        new Verifications() {
            {
                TerminalUpdateListCacheManager.get(CbbTerminalTypeEnums.APP_WINDOWS);
                times = 1;

                TerminalUpdateListCacheManager.isCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
                times = 1;

                FileUtils.isValidPath((File) any);
                times = 1;
            }
        };
    }

    /**
     *  测试获取windows软终端下载路径
     */
    @Test
    public void testGetWindowsAppDownloadUrl() {
        WinAppUpdateListDTO listDTO = new WinAppUpdateListDTO();
        List<WinAppComponentVersionInfoDTO> versionInfoDTOList = Lists.newArrayList();
        versionInfoDTOList.add(new WinAppComponentVersionInfoDTO());
        listDTO.setComponentList(versionInfoDTOList);
        listDTO.setCompletePackageName("aaa");

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public WinAppUpdateListDTO get(CbbTerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(CbbTerminalTypeEnums type) {
                return true;
            }
        };

        new MockUp<FileUtils>() {

            @Mock
            public boolean isValidPath(File file) {
                return true;
            }
        };

        try {
            String downLoadUrl = api.getWindowsAppDownloadUrl();
            assertEquals("/opt/ftp/terminal/terminal_component/windows_app/component/aaa", downLoadUrl);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                TerminalUpdateListCacheManager.get(CbbTerminalTypeEnums.APP_WINDOWS);
                times = 1;

                TerminalUpdateListCacheManager.isCacheReady(CbbTerminalTypeEnums.APP_WINDOWS);
                times = 1;

                FileUtils.isValidPath((File) any);
                times = 1;
            }
        };
    }
}
