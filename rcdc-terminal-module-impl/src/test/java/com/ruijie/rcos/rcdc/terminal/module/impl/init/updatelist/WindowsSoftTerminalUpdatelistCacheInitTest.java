package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.util.StringUtils;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.AppTerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/11
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class WindowsSoftTerminalUpdatelistCacheInitTest {

    @Tested
    private WinAppTerminalUpdatelistCacheInit cacheInit;

    @Test
    public void testGetUpdateListPath() {
        String updateListPath = cacheInit.getUpdateListPath();
        Assert.assertEquals("/opt/ftp/terminal/terminal_component/windows_app/update.list", updateListPath);
    }

    @Test
    public void testGetUpdateListCacheManager() {
        Map<CbbTerminalTypeEnums, CbbWinAppUpdateListDTO> updateListCacheManager =
                cacheInit.getUpdateListCacheManager();
        Assert.assertEquals(AppTerminalUpdateListCacheManager.getUpdateListCache(), updateListCacheManager);
    }

    @Test
    public void testGetTerminalType() {
        CbbTerminalTypeEnums terminalType = cacheInit.getTerminalType();
        Assert.assertEquals(CbbTerminalTypeEnums.WINDOWS, terminalType);
    }

    @Test
    public void testCacheInitPre() {

        cacheInit.cacheInitPre();

        new Verifications() {
            {
                AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
                times = 1;
            }
        };
    }

    @Test
    public void testCacheInitFinished() {

        cacheInit.cacheInitFinished();

        new Verifications() {
            {
                AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
                times = 1;
            }
        };
    }

    @Test
    public void testFillUpdateList() {

        new MockUp(Md5Builder.class){
            @Mock
            public byte[] computeFileMd5(File file) {
                return "aaa".getBytes();
            }
        };

        new MockUp(StringUtils.class){
            @Mock
            public String bytes2Hex(byte[] bytes) {
                return "aaa";
            }
        };

        CbbWinAppUpdateListDTO dto = new CbbWinAppUpdateListDTO();
        cacheInit.fillUpdateList(dto);
        Assert.assertEquals("aaa", dto.getValidateMd5());
    }

    /**
     * 测试填充updatelist计算MD5时出现IOException
     */
    @Test
    public void testFillUpdateListHasIOException() {

        new MockUp(Md5Builder.class){
            @Mock
            public byte[] computeFileMd5(File file) throws IOException{
                throw new IOException("aaa");
            }
        };

        new MockUp(StringUtils.class){
            @Mock
            public String bytes2Hex(byte[] bytes) {
                return "aaa";
            }
        };

        CbbWinAppUpdateListDTO dto = new CbbWinAppUpdateListDTO();
        cacheInit.fillUpdateList(dto);

        new Verifications(){
             {
                 try {
                     Md5Builder.computeFileMd5((File) any);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 times = 1;

                 StringUtils.bytes2Hex((byte[])any);
                 times = 0;
             }
         };
    }
}
