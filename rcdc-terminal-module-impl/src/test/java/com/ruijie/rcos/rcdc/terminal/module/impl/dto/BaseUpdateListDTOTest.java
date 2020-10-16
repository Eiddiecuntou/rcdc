package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/16
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class BaseUpdateListDTOTest {


    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        BaseUpdateListDTO dto = new BaseUpdateListDTO("1.0.0", 4);
        assertEquals("1.0.0", dto.getVersion());
        assertEquals(4, dto.getComponentSize().intValue());
    }
}
