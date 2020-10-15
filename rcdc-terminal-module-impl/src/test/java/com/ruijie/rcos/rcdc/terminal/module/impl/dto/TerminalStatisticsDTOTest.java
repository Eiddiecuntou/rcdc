package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/25
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class TerminalStatisticsDTOTest {

    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        TerminalStatisticsDTO dto = new TerminalStatisticsDTO(1L, "state");
        dto.setCount(2L);
        dto.setState("state2");
        Assert.assertEquals(2, dto.getCount().intValue());
        Assert.assertEquals("state2", dto.getState());
    }
}
