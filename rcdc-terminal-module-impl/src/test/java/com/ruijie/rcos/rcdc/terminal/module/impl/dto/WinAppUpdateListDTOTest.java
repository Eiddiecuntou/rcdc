package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDiskInfoDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        GetSetTester getSetTester = new GetSetTester(WinAppUpdateListDTO.class);
        getSetTester.registerTypeValueCreator(java.util.List.class, () -> new ArrayList<>());
        WinAppUpdateListDTO winAppUpdateListDTO = new WinAppUpdateListDTO("xxx", 1);
        getSetTester.runTest();
        Assert.assertTrue(true);
    }
}
