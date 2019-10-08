package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
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
public class GroupHierarchyCheckerTest {

    @Tested
    private GroupHierarchyChecker checker;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    /**
     * 测试校验分组层级 - 分组id为null
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckGroupIdIsNull() throws BusinessException {
        checker.check(null, 9);

        new Verifications() {
            {
                terminalGroupDAO.findById((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试校验分组层级 - 分组id为默认分组
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckGroupIsDefault() throws BusinessException {
        checker.check(Constants.DEFAULT_TERMINAL_GROUP_UUID, 8);

        new Verifications() {
            {
                terminalGroupDAO.findById((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * 测试校验分组层级 - 分组不存在
     */
    @Test
    public void testCheckGroupNotFound() {
        UUID groupId = UUID.randomUUID();

        new Expectations() {
            {
                terminalGroupDAO.findById(groupId);
                result = Optional.empty();
            }
        };

        try {
            checker.check(groupId, 8);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_NOT_EXIST, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.findById(groupId);
                times = 1;
            }
        };
    }

    /**
     * 测试校验分组层级 - 分组数超过限制
     */
    @Test
    public void testCheckGroupGroupHierarchyExceedLimit() {
        UUID groupId = UUID.randomUUID();

        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(groupId);
        groupEntity.setParentId(groupId);

        TerminalGroupEntity groupEntity2 = new TerminalGroupEntity();
        groupEntity.setId(groupId);

        Optional<TerminalGroupEntity>[] opArr = new Optional[8];
        Optional<TerminalGroupEntity> optional = Optional.of(groupEntity);
        for (int i = 0; i < 7; i++) {
            opArr[i] = optional;
        }
        opArr[7] = Optional.of(groupEntity2);

        new Expectations() {
            {
                terminalGroupDAO.findById(groupId);
                returns(optional, optional, opArr);
            }
        };

        try {
            checker.check(groupId, 2);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_LEVEL_EXCEED_LIMIT, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.findById(groupId);
                times = 10;
            }
        };
    }

    /**
     * 测试校验分组层级 - 分组数未超过限制
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testCheck() throws BusinessException {
        UUID groupId = UUID.randomUUID();

        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(groupId);
        groupEntity.setParentId(groupId);

        TerminalGroupEntity groupEntity2 = new TerminalGroupEntity();
        groupEntity.setId(groupId);

        Optional<TerminalGroupEntity>[] opArr = new Optional[6];
        Optional<TerminalGroupEntity> optional = Optional.of(groupEntity);
        for (int i = 0; i < 5; i++) {
            opArr[i] = optional;
        }
        opArr[5] = Optional.of(groupEntity2);

        new Expectations() {
            {
                terminalGroupDAO.findById(groupId);
                returns(optional, optional, opArr);
            }
        };


        checker.check(groupId, 2);


        new Verifications() {
            {
                terminalGroupDAO.findById(groupId);
                times = 8;
            }
        };
    }

    /**
     * testGetSubHierarchy
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testGetSubHierarchy() {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());
        List<TerminalGroupEntity> subGroupList = Lists.newArrayList(groupEntity);
        new Expectations() {
            {
                terminalGroupDAO.findByParentId((UUID) any);
                returns(subGroupList, subGroupList, subGroupList, Collections.EMPTY_LIST);
            }
        };

        int subHierarchy = checker.getSubHierarchy(UUID.randomUUID());
        assertEquals(4, subHierarchy);

        new Verifications() {
            {
                terminalGroupDAO.findByParentId((UUID) any);
                times = 4;
            }
        };
    }
}
