package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/24
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class GroupNameDuplicationCheckerTest {

    @Tested
    private GroupNameDuplicationChecker checker;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    /**
     * 测试参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCheckArgIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> checker.check(null, "1122"), "groupEntity can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> checker.check(new TerminalGroupEntity(), ""), "groupName can not be blank");
        assertTrue(true);
    }

    /**
     * 测试子分组数超出限制
     */
    @Test
    public void testCheck() throws BusinessException {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());

        new Expectations() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                result = Lists.newArrayList();
            }
        };
        checker.check(groupEntity, "123");

        new Verifications() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                times = 1;
            }
        };
    }

    /**
     * 测试有重复分组
     */
    @Test
    public void testCheckHasDuplicationGroup() {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());

        List<TerminalGroupEntity> list = Lists.newArrayList(groupEntity);
        new Expectations() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                result = list;
            }
        };

        try {
            checker.check(groupEntity, "123");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_DELETE_TERMINAL_GROUP_SUB_GROUP_HAS_DUPLICATION_WITH_MOVE_GROUP, e.getKey());
        }


        new Verifications() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                times = 1;
            }
        };
    }

}
