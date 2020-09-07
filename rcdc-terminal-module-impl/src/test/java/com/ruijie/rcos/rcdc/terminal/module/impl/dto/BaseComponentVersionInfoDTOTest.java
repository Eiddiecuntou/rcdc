package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/4
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class BaseComponentVersionInfoDTOTest {

    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        BaseComponentVersionInfoDTO dto = new BaseComponentVersionInfoDTO();
        dto.setMd5("123");
        dto.setName("name");
        dto.setPlatform("linux");
        dto.setVersion("1");
        assertEquals("123", dto.getMd5());
        assertEquals("name", dto.getName());
        assertEquals("linux", dto.getPlatform());
        assertEquals("1", dto.getVersion());


    }

}
