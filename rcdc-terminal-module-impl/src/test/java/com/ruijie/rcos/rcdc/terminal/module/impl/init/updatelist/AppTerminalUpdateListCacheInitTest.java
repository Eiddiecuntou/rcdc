package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.util.StringUtils;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/11
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class AppTerminalUpdateListCacheInitTest {

    @Tested
    private AppTerminalUpdateListCacheInit cacheInit;

    /**
     * testGetUpdateListPath
     */
    @Test
    public void testGetUpdateListPath() {
        AppTerminalUpdateListCacheInit cacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalOsTypeEnums.WINDOWS);
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/ftp/terminal/terminal_component/windows_app/update.list", updateListPath);
    }

    /**
     * testGetTerminalType
     */
    @Test
    public void testGetTerminalOsType() {
        AppTerminalUpdateListCacheInit cacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalOsTypeEnums.WINDOWS);
        CbbTerminalOsTypeEnums osType = cacheInit.getTerminalOsType();
        Assert.assertEquals(CbbTerminalOsTypeEnums.WINDOWS, osType);
    }

    /**
     * 测试填充updatelist计算MD5时出现IOException
     */
    @Test
    public void testFillUpdateListHasIOException() {

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) {
                return "123".getBytes();
            }
        };

        new MockUp<StringUtils>() {
            @Mock
            public String bytes2Hex(byte[] byteArr) {
                return "123";
            }
        };

        AppTerminalUpdateListCacheInit cacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalOsTypeEnums.WINDOWS);
        AppUpdateListDTO dto = new AppUpdateListDTO();
        List<AppComponentVersionInfoDTO> versionList = Lists.newArrayList();
        AppComponentVersionInfoDTO versionInfoDTO = new AppComponentVersionInfoDTO();

        versionInfoDTO.setCompletePackageName("aaa");
        versionList.add(versionInfoDTO);
        dto.setComponentList(versionList);

        cacheInit.fillUpdateList(dto);

        Assert.assertEquals("/terminal_component/windows_app/component/aaa", versionInfoDTO.getCompletePackageUrl());
    }

    /**
     * 测试填充updatelist计算MD5时出现IOException2
     */
    @Test
    public void testFillUpdateListHasIOException2() {

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) throws IOException {
                throw new IOException("123");
            }
        };

        new MockUp<StringUtils>() {
            @Mock
            public String bytes2Hex(byte[] byteArr) {
                return "123";
            }
        };

        AppTerminalUpdateListCacheInit cacheInit = new AppTerminalUpdateListCacheInit(CbbTerminalOsTypeEnums.WINDOWS);
        AppUpdateListDTO dto = new AppUpdateListDTO();
        List<AppComponentVersionInfoDTO> versionList = Lists.newArrayList();
        AppComponentVersionInfoDTO versionInfoDTO = new AppComponentVersionInfoDTO();

        versionInfoDTO.setCompletePackageName("aaa");
        versionList.add(versionInfoDTO);
        dto.setComponentList(versionList);

        cacheInit.fillUpdateList(dto);

        Assert.assertEquals("/terminal_component/windows_app/component/aaa", versionInfoDTO.getCompletePackageUrl());
        Assert.assertEquals("default_validate_md5", dto.getValidateMd5());
    }
}
