package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupHierarchyChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupNameDuplicationChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupSubNumChecker;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/6/8
 *
 * @author XiaoJiaXin
 */
@RunWith(SkyEngineRunner.class)
public class DeleteTerminalGroupValidatorTest {

    @Tested
    private DeleteTerminalGroupValidator validator;

    @Injectable
    private TerminalGroupService terminalGroupService;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    @Injectable
    private GroupHierarchyChecker groupHierarchyChecker;

    @Injectable
    private GroupSubNumChecker groupSubNumChecker;

    @Injectable
    private GroupNameDuplicationChecker groupNameDuplicationChecker;

    @Test
    public void testValidateMoveGroupIdNotNull() throws BusinessException {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());
        groupEntity.setName("name");
        groupEntity.setParentId(UUID.randomUUID());
        new Expectations() {
            {
                terminalGroupService.checkGroupExist((UUID) any);
                result = groupEntity;
            }
        };
        validator.validate(UUID.randomUUID(), UUID.randomUUID());
        new Verifications() {
            {
                terminalGroupService.checkGroupExist((UUID) any);
                times = 2;
            }
        };

    }

    @Test
    public void testValidate() throws BusinessException {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());
        groupEntity.setName("name");
        groupEntity.setParentId(UUID.randomUUID());

        List<TerminalGroupEntity> subGroupList = new ArrayList<>();
        subGroupList.add(groupEntity);
        new Expectations() {
            {
                terminalGroupService.checkGroupExist((UUID) any);
                result = groupEntity;
                terminalGroupDAO.findByParentId(groupEntity.getId());
                result = subGroupList;
            }
        };
        validator.validate(UUID.randomUUID(), UUID.randomUUID());
        new Verifications() {
            {
                terminalGroupService.checkGroupExist((UUID) any);
                times = 3;
            }
        };
    }

    @Test
    public void testValidateMoveGroupIdNull() throws BusinessException {
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());
        groupEntity.setName("name");
        groupEntity.setParentId(UUID.randomUUID());

        List<TerminalGroupEntity> subGroupList = new ArrayList<>();
        subGroupList.add(groupEntity);
        new Expectations() {
            {
                terminalGroupService.checkGroupExist((UUID) any);
                result = groupEntity;
                terminalGroupDAO.findByParentId(groupEntity.getId());
                result = subGroupList;
            }
        };
        validator.validate(UUID.randomUUID(), null);
        new Verifications() {
            {
                terminalGroupService.checkGroupExist((UUID) any);
                times = 1;
            }
        };

    }
}