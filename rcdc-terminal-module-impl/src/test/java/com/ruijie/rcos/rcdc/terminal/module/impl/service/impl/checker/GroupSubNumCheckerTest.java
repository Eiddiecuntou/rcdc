package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import java.util.UUID;

import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

import static org.junit.Assert.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/24
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class GroupSubNumCheckerTest {

    @Tested
    private GroupSubNumChecker checker;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    /**
     *  测试参数为空
     * @throws Exception 异常
     */
    @Test
    public void testCheckArgIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> checker.check(null, 1), "groupEntity can not be null");
        assertTrue(true);
    }

    /**
     * 测试子分组数超出限制
     */
    @Test
    public void testCheckSubGroupExceedLimit() {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());

        new Expectations() {
            {
                terminalGroupDAO.countByParentId(groupEntity.getId());
                result = 200;
            }
        };
        try {
            checker.check(groupEntity, 1);
            fail();
        } catch (BusinessException e) {
            assertEquals(PublicBusinessKey.RCDC_TERMINALGROUP_SUB_GROUP_NUM_EXCEED_LIMIT, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.countByParentId(groupEntity.getId());
                times = 1;
            }
        };
    }

    /**
     * 测试分组数未超出限制
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testCheck() throws BusinessException {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());

        new Expectations() {
            {
                terminalGroupDAO.countByParentId(groupEntity.getId());
                result = 199;
            }
        };

        checker.check(groupEntity, 1);


        new Verifications() {
            {
                terminalGroupDAO.countByParentId(groupEntity.getId());
                times = 1;
            }
        };
    }

}
