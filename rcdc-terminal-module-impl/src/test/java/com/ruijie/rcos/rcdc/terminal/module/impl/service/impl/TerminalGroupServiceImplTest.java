package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;


import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupHierarchyChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupSubNumChecker;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.checker.GroupTotalNumChecker;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年09月23日
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class TerminalGroupServiceImplTest {

    @Tested
    private TerminalGroupServiceImpl terminalGroupService;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    @Injectable
    private GroupHierarchyChecker groupHierarchyChecker;

    @Injectable
    private GroupSubNumChecker groupSubNumChecker;

    @Injectable
    private GroupTotalNumChecker groupTotalNumChecker;


    @Before
    public void before() {

        new MockUp<LocaleI18nResolver>() {

            /**
             *
             * @param key key
             * @param args args
             * @return key
             */
            @Mock
            public String resolve(String key, String... args) {
                return key;
            }

        };
    }

    /**
     * 测试保存终端分组 - 分组名为保留分组
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testSaveTerminalGroupParentGroupIsDefault() throws BusinessException {
        TerminalGroupDTO groupDTO = new TerminalGroupDTO(UUID.randomUUID(), "123", Constants.DEFAULT_TERMINAL_GROUP_UUID);
        TerminalGroupEntity group = new TerminalGroupEntity();
        group.setParentId(UUID.randomUUID());

        try {
            terminalGroupService.saveTerminalGroup(groupDTO);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_CAN_NOT_CREATE_IN_DEFAULT, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.save((TerminalGroupEntity) any);
                times = 0;

                groupTotalNumChecker.check(1);
                times = 0;

                groupSubNumChecker.check(group, 1);
                times = 0;

                groupHierarchyChecker.check(groupDTO.getParentGroupId(), 1);
                times = 0;
            }
        };
    }

    /**
     * 测试保存终端分组
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSaveTerminalGroupTerminalIsDefaultName() throws BusinessException {
        TerminalGroupDTO groupDTO = new TerminalGroupDTO(UUID.randomUUID(), "总览", UUID.randomUUID());
        TerminalGroupEntity group = new TerminalGroupEntity();
        group.setParentId(UUID.randomUUID());
        new Expectations() {
            {
                terminalGroupDAO.findById((UUID) any);
                returns(Optional.of(group), Optional.of(group), Optional.of(new TerminalGroupEntity()));

                groupTotalNumChecker.check(1);

                groupSubNumChecker.check(group, 1);
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return "总览";
            }
        };

        try {
            terminalGroupService.saveTerminalGroup(groupDTO);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_USERGROUP_NOT_ALLOW_RESERVE_NAME, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.findById((UUID) any);
                times = 1;

                groupTotalNumChecker.check(1);
                times = 1;

                groupSubNumChecker.check(group, 1);
                times = 1;

                terminalGroupDAO.save((TerminalGroupEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试保存终端分组-上级分组id为空
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSaveTerminalGroup() throws BusinessException {
        TerminalGroupDTO groupDTO = new TerminalGroupDTO(UUID.randomUUID(), "name123", UUID.randomUUID());
        TerminalGroupEntity group = new TerminalGroupEntity();
        group.setParentId(UUID.randomUUID());
        new Expectations() {
            {
                terminalGroupDAO.findById(groupDTO.getParentGroupId());
                result = Optional.of(group);

                terminalGroupDAO.save((TerminalGroupEntity) any);

                groupTotalNumChecker.check(1);

                groupSubNumChecker.check(group, 1);

                groupHierarchyChecker.check(groupDTO.getParentGroupId(), 1);
            }
        };

        new MockUp<TerminalGroupServiceImpl>() {
            @Mock
            public boolean checkGroupNameUnique(TerminalGroupDTO terminalGroup) throws BusinessException {
                return true;
            }
        };

        new MockUp<LocaleI18nResolver>() {
            @Mock
            public String resolve(String key, String... args) {
                return "123";
            }
        };

        terminalGroupService.saveTerminalGroup(groupDTO);

        new Verifications() {
            {
                terminalGroupDAO.save((TerminalGroupEntity) any);
                times = 1;

                groupTotalNumChecker.check(1);
                times = 1;

                groupSubNumChecker.check(group, 1);
                times = 1;

                groupHierarchyChecker.check(groupDTO.getParentGroupId(), 1);
                times = 1;
            }
        };
    }

    /**
     * 测试保存终端分组-参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testSaveTerminalGroupParamIsNull() throws Exception {
        TerminalGroupDTO groupDTO2 = new TerminalGroupDTO(UUID.randomUUID(), "", null);
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalGroupService.saveTerminalGroup(null), "terminal group can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalGroupService.saveTerminalGroup(groupDTO2),
                "terminal group name can not be null");
        assertTrue(true);
    }

    /**
     * 测试编辑终端-参数为空
     *
     * @throws Exception 业务异常
     */
    @Test
    public void testModifyByTerminalIdParamIsEmpty() throws Exception {
        TerminalGroupDTO groupDTO2 = new TerminalGroupDTO(UUID.randomUUID(), "", null);
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalGroupService.modifyGroupById(null), "terminal group param can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalGroupService.modifyGroupById(groupDTO2),
                "terminal group name can not be blank");
        assertTrue(true);
    }


    /**
     * 测试编辑终端-父级节点id与自身相同
     *
     */
    @Test
    public void testmodifyGroupByIdWhileParentIdEqualsId() {
        UUID uuid = UUID.randomUUID();
        TerminalGroupDTO dto = new TerminalGroupDTO(uuid, "groupName123", uuid);

        try {
            terminalGroupService.modifyGroupById(dto);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_PARENT_CAN_NOT_SELECT_ITSELF, e.getKey());
        }
    }

    /**
     * 测试编辑终端-父级节点为null
     *
     * @throws BusinessException exception
     */
    @Test
    public void testmodifyGroupByIdWhileParentIdIsNull() throws BusinessException {
        UUID uuid = UUID.randomUUID();
        TerminalGroupDTO dto = new TerminalGroupDTO(uuid, "groupName123", null);

        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(uuid);
        groupEntity.setName("groupName123");
        groupEntity.setParentId(null);

        new MockUp<TerminalGroupServiceImpl>() {
            @Mock
            public TerminalGroupEntity checkGroupExist(UUID groupId) throws BusinessException {
                return groupEntity;
            }

            @Mock
            private void checkGroupName(TerminalGroupDTO terminalGroup) throws BusinessException {
                // 保存分组以覆盖，这里mock掉
            }
        };

        terminalGroupService.modifyGroupById(dto);

        new Verifications() {
            {
                TerminalGroupEntity saveGroup;
                terminalGroupDAO.save(saveGroup = withCapture());

                assertEquals(uuid, saveGroup.getId());
                assertEquals("groupName123", saveGroup.getName());
                assertEquals(null, saveGroup.getParentId());
            }
        };
    }


    /**
     * 测试获取终端分组
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetTerminalGroup() throws BusinessException {
        UUID id = UUID.randomUUID();
        new Expectations() {
            {
                terminalGroupDAO.findById(id);
                result = Optional.of(new TerminalGroupEntity());
            }
        };
        TerminalGroupEntity terminalGroup = terminalGroupService.getTerminalGroup(id);
        Assert.assertTrue(terminalGroup != null);

        new Verifications() {
            {
                terminalGroupDAO.findById(id);
                times = 1;
            }
        };
    }

    /**
     * 测试获取终端分组-分组不存在
     */
    @Test
    public void testGetTerminalGroupGroupNotExist() {
        UUID id = UUID.randomUUID();
        new Expectations() {
            {
                terminalGroupDAO.findById(id);
                result = Optional.empty();
            }
        };

        try {
            terminalGroupService.getTerminalGroup(id);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_NOT_EXIST, e.getKey());
        }

        new Verifications() {
            {
                terminalGroupDAO.findById(id);
                times = 1;
            }
        };
    }


    /**
     * 测试检验分组名称是否同级唯一
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckGroupNameUnique() throws BusinessException {
        UUID id = UUID.randomUUID();
        TerminalGroupDTO terminalGroup = new TerminalGroupDTO(id, "groupName123", UUID.randomUUID());
        List<TerminalGroupEntity> groupList = buildTerminalGroupList();
        new Expectations() {
            {
                terminalGroupDAO.findByParentId(terminalGroup.getParentGroupId());
                result = groupList;
            }
        };

        boolean enableUnique = terminalGroupService.checkGroupNameUnique(terminalGroup);
        Assert.assertTrue(enableUnique);

        new Verifications() {
            {
                terminalGroupDAO.findByParentId(terminalGroup.getParentGroupId());
                times = 1;
            }
        };
    }

    /**
     * 测试检验分组名称是否同级唯一-子级列表为空
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckGroupNameUniqueSubListIsEmpty() throws BusinessException {
        TerminalGroupDTO terminalGroup = new TerminalGroupDTO(UUID.randomUUID(), "groupName123", null);
        List<TerminalGroupEntity> groupList = Collections.emptyList();
        new Expectations() {
            {
                terminalGroupDAO.findByParentId(terminalGroup.getParentGroupId());
                result = groupList;
            }
        };

        boolean enableUnique = terminalGroupService.checkGroupNameUnique(terminalGroup);
        Assert.assertTrue(enableUnique);

        new Verifications() {
            {
                terminalGroupDAO.findByParentId(terminalGroup.getParentGroupId());
                times = 1;
            }
        };
    }

    /**
     * 测试检验分组名称是否同级唯一-存在同名
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckGroupNameUniqueNotUnique() throws BusinessException {
        TerminalGroupDTO terminalGroup = new TerminalGroupDTO(UUID.randomUUID(), "groupName123", null);
        List<TerminalGroupEntity> groupList = buildHasSameNameList(terminalGroup.getId());
        new Expectations() {
            {

                terminalGroupDAO.findByParentId(terminalGroup.getParentGroupId());
                result = groupList;
            }
        };

        try {
            terminalGroupService.checkGroupNameUnique(terminalGroup);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), PublicBusinessKey.RCDC_TERMINALGROUP_GROUP_NAME_DUPLICATE);
        }

        new Verifications() {
            {
                terminalGroupDAO.findByParentId(terminalGroup.getParentGroupId());
                times = 1;
            }
        };
    }

    @Test
    public void testCheckGroupNameUniqueWhenDefaultGroupName() {
        TerminalGroupDTO terminalGroup = new TerminalGroupDTO(UUID.randomUUID(), "总览", null);

        try {
            terminalGroupService.checkGroupNameUnique(terminalGroup);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getMessage(), BusinessKey.RCDC_TERMINAL_USERGROUP_NOT_ALLOW_RESERVE_NAME);
        }
    }

    private List<TerminalGroupEntity> buildHasSameNameList(UUID groupId) {
        List<TerminalGroupEntity> groupList = new ArrayList<TerminalGroupEntity>();
        for (int i = 0; i < 2; i++) {
            TerminalGroupEntity group = new TerminalGroupEntity();
            group.setId(UUID.randomUUID());
            group.setName("groupName123");
            groupList.add(group);
            if (i % 2 != 0) {
                group.setId(groupId);
            }
        }

        return groupList;
    }

    private List<TerminalGroupEntity> buildTerminalGroupList() {
        List<TerminalGroupEntity> groupList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            groupList.add(new TerminalGroupEntity());
        }
        return groupList;
    }

    /**
     * 检查终端是否存在
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCheckGroupExistWhileException() throws BusinessException {
        UUID groupId = UUID.randomUUID();
        Optional<TerminalGroupEntity> group = Optional.ofNullable(null);
        new Expectations() {
            {
                terminalGroupDAO.findById((UUID) any);
                result = group;
            }
        };
        try {
            terminalGroupService.checkGroupExist(groupId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINALGROUP_GROUP_NOT_EXIST, e.getKey());
        }
    }

}
