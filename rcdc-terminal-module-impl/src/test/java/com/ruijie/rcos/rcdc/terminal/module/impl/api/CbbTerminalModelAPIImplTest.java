package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalModelService;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author wjp
 */
@RunWith(JMockit.class)
public class CbbTerminalModelAPIImplTest {

    @Tested
    CbbTerminalModelAPIImpl terminalModelAPI;

    @Injectable
    TerminalModelService terminalModelService;

    /**
     * 测试listTerminalModel参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testListTerminalModelArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalModelAPI.listTerminalModel(null), "platformArr can not be null");
        assertTrue(true);
    }

    /**
     * 测试listTerminalModel参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testListTerminalModel() throws Exception {
        CbbTerminalModelDTO modelDTO = new CbbTerminalModelDTO();
        CbbTerminalModelDTO[] terminalModelArr = new CbbTerminalModelDTO[] {modelDTO};
        new Expectations() {
            {
                terminalModelService.queryTerminalModelByPlatform((CbbTerminalPlatformEnums[]) any);
                result = terminalModelArr;
            }
        };

        CbbTerminalModelDTO[] modelDTOArr = terminalModelAPI.listTerminalModel(new CbbTerminalPlatformEnums[]{});
        assertEquals(modelDTOArr, terminalModelArr);
    }

    /**
     * 测试queryByProductId参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testQueryByProductIdArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalModelAPI.findByProductId(null), "productId can not be null");
        assertTrue(true);
    }

    /**
     * 测试queryByProductId
     *
     * @throws Exception 异常
     */
    @Test
    public void testQueryByProductIdSuccess() throws Exception {
        new Expectations() {
            {
                terminalModelAPI.findByProductId("aaa");
            }
        };
        CbbTerminalModelDTO modelDTO = terminalModelAPI.findByProductId("aaa");

    }

    /**
     * testListTerminalOsType
     */
    @Test
    public void testListTerminalOsType() {

        new Expectations() {
            {
                terminalModelService.queryTerminalOsTypeByPlatform(new CbbTerminalPlatformEnums[]{CbbTerminalPlatformEnums.APP});
                result = Lists.newArrayList("Windows");
            }
        };

        List<String> terminalOsTypeList = terminalModelAPI.listTerminalOsType(new CbbTerminalPlatformEnums[]{CbbTerminalPlatformEnums.APP});

        Assert.assertEquals(terminalOsTypeList.get(0), "Windows");
    }
}
