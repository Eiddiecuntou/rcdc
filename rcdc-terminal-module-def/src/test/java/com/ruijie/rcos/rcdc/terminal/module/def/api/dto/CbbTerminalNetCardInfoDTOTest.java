package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import static org.junit.Assert.*;

import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Description: CbbTerminalNetCardInfoDTO单元测试类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/20 9:25 上午
 *
 * @author zhouhuan
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalNetCardInfoDTOTest {

    /**
     * 测试get、set方法
     */
    @Test
    public void testGetAndSet() {
        GetSetTester tester = new GetSetTester(CbbTerminalNetCardInfoDTO.class);
        tester.runTest();
        Assert.assertTrue(true);
    }
}