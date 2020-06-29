package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

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
     * 测试子分组数超出限制
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testCheck() throws BusinessException {
        UUID uuid = UUID.randomUUID();
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(uuid);
        List<TerminalGroupEntity> groupList = Lists.newArrayList(groupEntity);
        TerminalGroupEntity deleteGroupEntity = new TerminalGroupEntity();
        deleteGroupEntity.setId(uuid);
        new Expectations() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                result = groupList;
            }
        };
        checker.check(deleteGroupEntity, groupEntity, "123");

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
        TerminalGroupEntity deleteGroupEntity = new TerminalGroupEntity();
        deleteGroupEntity.setId(UUID.randomUUID());
        List<TerminalGroupEntity> groupList = Lists.newArrayList(groupEntity);
        new Expectations() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                result = groupList;
            }
        };

        try {
            checker.check(deleteGroupEntity, groupEntity, "123");
            fail();
        } catch (BusinessException e) {
            assertEquals(PublicBusinessKey.RCDC_DELETE_TERMINAL_GROUP_SUB_GROUP_HAS_DUPLICATION_WITH_MOVE_GROUP, e.getKey());
        }


        new Verifications() {
            {
                terminalGroupDAO.findByParentIdAndName(groupEntity.getId(), "123");
                times = 1;
            }
        };
    }

}
