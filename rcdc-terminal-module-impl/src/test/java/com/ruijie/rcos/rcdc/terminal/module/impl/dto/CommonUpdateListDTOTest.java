package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/10/16
 *
 * @author hs
 */
@RunWith(SkyEngineRunner.class)
public class CommonUpdateListDTOTest {


    /**
     * 测试GetterAndSetter
     */
    @Test
    public void testGetterAndSetter() {
        GetSetTester tester = new GetSetTester(CommonUpdateListDTO.class);
        tester.registerTypeValueCreator(List.class, () -> new ArrayList<>());
        tester.runTest();
        assertTrue(true);
    }
}
