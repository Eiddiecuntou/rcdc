package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDownLoadUrlResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import mockit.*;
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
        CbbWinAppUpdateListDTO listDTO = new CbbWinAppUpdateListDTO();

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public CbbWinAppUpdateListDTO get(TerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(TerminalTypeEnums type) {
                return false;
            }
        };

        try {
            api.getWindowsAppDownloadUrl(new DefaultRequest());
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
            public CbbWinAppUpdateListDTO get(TerminalTypeEnums type) {
                return null;
            }

            @Mock
            public boolean isCacheReady(TerminalTypeEnums type) {
                return true;
            }
        };

        try {
            api.getWindowsAppDownloadUrl(new DefaultRequest());
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
        CbbWinAppUpdateListDTO listDTO = new CbbWinAppUpdateListDTO();
        listDTO.setComponentList(null);

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public CbbWinAppUpdateListDTO get(TerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(TerminalTypeEnums type) {
                return true;
            }
        };

        try {
            api.getWindowsAppDownloadUrl(new DefaultRequest());
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
        CbbWinAppUpdateListDTO listDTO = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> versionInfoDTOList = Lists.newArrayList();
        versionInfoDTOList.add(new CbbWinAppComponentVersionInfoDTO());
        listDTO.setComponentList(versionInfoDTOList);
        listDTO.setCompletePackageName("aaa");

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public CbbWinAppUpdateListDTO get(TerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(TerminalTypeEnums type) {
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
            api.getWindowsAppDownloadUrl(new DefaultRequest());
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COMPONENT_UPDATELIST_CACHE_INCORRECT, e.getKey());
        }

        new Verifications(){
            {
                TerminalUpdateListCacheManager.get(TerminalTypeEnums.APP_WINDOWS);
                times = 1;

                TerminalUpdateListCacheManager.isCacheReady(TerminalTypeEnums.APP_WINDOWS);
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
        CbbWinAppUpdateListDTO listDTO = new CbbWinAppUpdateListDTO();
        List<CbbWinAppComponentVersionInfoDTO> versionInfoDTOList = Lists.newArrayList();
        versionInfoDTOList.add(new CbbWinAppComponentVersionInfoDTO());
        listDTO.setComponentList(versionInfoDTOList);
        listDTO.setCompletePackageName("aaa");

        new MockUp<TerminalUpdateListCacheManager>() {

            @Mock
            public CbbWinAppUpdateListDTO get(TerminalTypeEnums type) {
                return listDTO;
            }

            @Mock
            public boolean isCacheReady(TerminalTypeEnums type) {
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
            CbbDownLoadUrlResponse windowsAppDownloadUrl = api.getWindowsAppDownloadUrl(new DefaultRequest());
            assertEquals("/opt/ftp/terminal/terminal_component/windows_app/component/aaa", windowsAppDownloadUrl.getDownLoadUrl());
        } catch (BusinessException e) {
            fail();
        }

        new Verifications(){
            {
                TerminalUpdateListCacheManager.get(TerminalTypeEnums.APP_WINDOWS);
                times = 1;

                TerminalUpdateListCacheManager.isCacheReady(TerminalTypeEnums.APP_WINDOWS);
                times = 1;

                FileUtils.isValidPath((File) any);
                times = 1;
            }
        };
    }
}
