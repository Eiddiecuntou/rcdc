package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/10
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class WinAppUpdateListDTOTest {

    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        AppUpdateListDTO dto = new AppUpdateListDTO();
        dto.setCompletePackageName("completePackageName");
        dto.setCompletePackageUrl("completePackageUrl");
        dto.setMd5("md5");
        dto.setName("name");
        dto.setPlatform("window");
        assertEquals("completePackageName", dto.getCompletePackageName());
        assertEquals("completePackageUrl", dto.getCompletePackageUrl());
        assertEquals("md5", dto.getMd5());
        assertEquals("name", dto.getName());
        assertEquals("window", dto.getPlatform());

    }
}
