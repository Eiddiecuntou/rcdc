package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/3/26
 *
 * @author nting
 */
@RunWith(SkyEngineRunner.class)
public class CommonUpdateListDTOTest {

    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {

        CommonUpdateListDTO dto = new CommonUpdateListDTO();
        dto.setBaseVersion("123");
        dto.setComponentPackageDirName("dirName");
        dto.setComponentPackageDirTorrentMd5("md5");
        dto.setComponentPackageDirTorrentUrl("url");

        assertEquals("123", dto.getBaseVersion());
        assertEquals("dirName", dto.getComponentPackageDirName());
        assertEquals("md5", dto.getComponentPackageDirTorrentMd5());
        assertEquals("url", dto.getComponentPackageDirTorrentUrl());
    }
}
