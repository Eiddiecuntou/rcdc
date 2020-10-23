package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupTreeNodeDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(SkyEngineRunner.class)
public class GetTerminalGroupTreeDTOTest {

    /**
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        GetSetTester getSetTester = new GetSetTester(GetTerminalGroupTreeDTO.class);
        getSetTester.runTest();

        new GetTerminalGroupTreeDTO(new CbbTerminalGroupTreeNodeDTO[]{});
        Assert.assertTrue(true);
    }

}