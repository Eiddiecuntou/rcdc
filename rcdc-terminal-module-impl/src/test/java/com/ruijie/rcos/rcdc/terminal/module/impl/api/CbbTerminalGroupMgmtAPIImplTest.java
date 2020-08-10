package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalGroupMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbDeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbEditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalGroupOperNotifySPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbTerminalGroupOperNotifyRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalGroupHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalGroupServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/16
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalGroupMgmtAPIImplTest {

    @Tested
    private CbbTerminalGroupMgmtAPIImpl api;

    @Injectable
    private TerminalGroupService terminalGroupService;

    @Injectable
    private TerminalGroupHandler terminalGroupHandler;

    @Injectable
    private TerminalGroupServiceTx terminalGroupServiceTx;

    @Injectable
    private CbbTerminalGroupOperNotifySPI cbbTerminalGroupOperNotifySPI;

    /**
     *  测试getAllTerminalGroup()
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetAllTerminalGroup() throws BusinessException {

        List<TerminalGroupEntity> groupList = new ArrayList<>();
        TerminalGroupEntity entity = new TerminalGroupEntity();
        entity.setId(CbbTerminalGroupMgmtAPI.DEFAULT_TERMINAL_GROUP_ID);
        groupList.add(entity);
        TerminalGroupEntity entity2 = new TerminalGroupEntity();
        entity2.setId(UUID.randomUUID());
        groupList.add(entity2);

        new Expectations() {
            {
                terminalGroupService.findAll();
                result = groupList;
            }
        };

        api.getAllTerminalGroup();

        new Verifications() {
            {
                terminalGroupService.findAll();
                times = 1;
            }
        };
    }

    /**
     *  测试加载终端分组树-无终端分组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testLoadTerminalGroupCompleteTreeWhileNoGroup() throws BusinessException {

        new Expectations() {
            {
                terminalGroupService.findAll();
                result = null;
            }
        };

        TerminalGroupTreeNodeDTO[] dtoArr = api.loadTerminalGroupCompleteTree(new CbbGetTerminalGroupCompleteTreeRequest());
        assertEquals(0, dtoArr.length);

        new Verifications() {
            {
                terminalGroupService.findAll();
                times = 1;
            }
        };
    }

    /**
     *  测试加载终端分组树- 过来默认分组和普通分组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testLoadTerminalGroupCompleteTreeFilterGroup() throws BusinessException {

        List<TerminalGroupEntity> groupList = buildTerminalGroupEntities();

        TerminalGroupEntity filterGroup = new TerminalGroupEntity();
        filterGroup.setId(UUID.randomUUID());
        groupList.add(filterGroup);

        new Expectations() {
            {
                terminalGroupService.findAll();
                result = groupList;
            }
        };

        CbbGetTerminalGroupCompleteTreeRequest request = new CbbGetTerminalGroupCompleteTreeRequest();
        request.setEnableFilterDefaultGroup(true);
        request.setFilterGroupId(filterGroup.getId());
        TerminalGroupTreeNodeDTO[] dtoArr = api.loadTerminalGroupCompleteTree(request);

        new Verifications() {
            {
                terminalGroupService.findAll();
                times = 1;
                terminalGroupHandler.filterDefaultGroup((List<TerminalGroupEntity>) any);
                times = 1;
                terminalGroupHandler.assembleGroupTree((UUID) any, (List<TerminalGroupEntity>) any, (UUID) any);
                times = 1;
            }
        };
    }

    /**
     *  测试加载终端分组树
     * @throws BusinessException 业务异常
     */
    @Test
    public void testLoadTerminalGroupCompleteTree() throws BusinessException {

        List<TerminalGroupEntity> groupList = buildTerminalGroupEntities();

        new Expectations() {
            {
                terminalGroupService.findAll();
                result = groupList;
            }
        };

        CbbGetTerminalGroupCompleteTreeRequest request = new CbbGetTerminalGroupCompleteTreeRequest();
        request.setEnableFilterDefaultGroup(false);
        api.loadTerminalGroupCompleteTree(request);

        new Verifications() {
            {
                terminalGroupService.findAll();
                times = 1;
                terminalGroupHandler.filterDefaultGroup((List<TerminalGroupEntity>) any);
                times = 0;
                terminalGroupHandler.assembleGroupTree((UUID) any, (List<TerminalGroupEntity>) any, (UUID) any);
                times = 1;
            }
        };
    }

    private List<TerminalGroupEntity> buildTerminalGroupEntities() {
        List<TerminalGroupEntity> groupList = Lists.newArrayList();
        TerminalGroupEntity groupEntity1 = new TerminalGroupEntity();
        groupEntity1.setId(UUID.randomUUID());
        groupEntity1.setName("aaa");

        TerminalGroupEntity groupEntity2 = new TerminalGroupEntity();
        groupEntity2.setId(UUID.randomUUID());
        groupEntity2.setName("bbb");

        TerminalGroupEntity groupEntity3 = new TerminalGroupEntity();
        groupEntity3.setId(UUID.randomUUID());
        groupEntity3.setName("aaa111");
        groupEntity3.setParentId(groupEntity1.getId());

        groupList.add(groupEntity1);
        groupList.add(groupEntity2);
        groupList.add(groupEntity3);

        TerminalGroupEntity defaultGroup = new TerminalGroupEntity();
        defaultGroup.setId(Constants.DEFAULT_TERMINAL_GROUP_UUID);
        groupList.add(defaultGroup);

        return groupList;
    }

    /**
     *  测试根据名称获取终端组 - 未查找到终端组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetByNameWhileNotFound() throws BusinessException {
        new Expectations() {
            {
                terminalGroupService.getByName((UUID) any, anyString);
                result = null;
            }
        };

        TerminalGroupDTO dto = api.getByName(new CbbTerminalGroupRequest("aaa", UUID.randomUUID()));
        assertEquals(null, dto);
    }

    /**
     *  测试根据名称获取终端组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetByName() throws BusinessException {

        List<TerminalGroupEntity> groupEntityList = Lists.newArrayList();
        TerminalGroupEntity groupEntity = new TerminalGroupEntity();
        groupEntity.setId(UUID.randomUUID());
        groupEntity.setName("aaa");
        groupEntityList.add(groupEntity);

        new Expectations() {
            {
                terminalGroupService.getByName((UUID) any, anyString);
                result = groupEntityList;
            }
        };

        TerminalGroupDTO dto = api.getByName(new CbbTerminalGroupRequest("aaa", UUID.randomUUID()));
        assertEquals(groupEntity.getId(), dto.getId());
        assertEquals(groupEntity.getName(), dto.getGroupName());

        new Verifications() {
            {
                terminalGroupService.getByName((UUID) any, anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试根据id获取分组- 父分组id不为null
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testLoadByIdParentGroupIdIsNotNull() throws BusinessException {

        TerminalGroupEntity groupEntity1 = new TerminalGroupEntity();
        groupEntity1.setId(UUID.randomUUID());
        groupEntity1.setName("aaa");
        groupEntity1.setParentId(UUID.randomUUID());

        TerminalGroupEntity groupEntity2 = new TerminalGroupEntity();
        groupEntity2.setId(UUID.randomUUID());
        groupEntity2.setName("bbb");

        new Expectations() {
            {
                terminalGroupService.getTerminalGroup((UUID) any);
                returns (groupEntity1, groupEntity2);
            }
        };

        TerminalGroupDTO dto = api.loadById(UUID.randomUUID());
        assertEquals(groupEntity1.getId(), dto.getId());
        assertEquals(groupEntity1.getName(), dto.getGroupName());
        assertEquals(groupEntity2.getName(), dto.getParentGroupName());

        new Verifications() {
            {
                terminalGroupService.getTerminalGroup((UUID) any);
                times = 2;
            }
        };
    }

    /**
     * 测试根据id获取分组- 父分组id为null
     * 
     * @throws BusinessException exception
     */
    @Test
    public void testLoadByIdParentGroupIdIsNull() throws BusinessException {

        TerminalGroupEntity groupEntity1 = new TerminalGroupEntity();
        groupEntity1.setId(UUID.randomUUID());
        groupEntity1.setName("aaa");

        new Expectations() {
            {
                terminalGroupService.getTerminalGroup((UUID) any);
                result = groupEntity1;
            }
        };

        TerminalGroupDTO dto = api.loadById(UUID.randomUUID());
        assertEquals(groupEntity1.getId(), dto.getId());
        assertEquals(groupEntity1.getName(), dto.getGroupName());
        assertEquals(null, dto.getParentGroupName());

        new Verifications() {
            {
                terminalGroupService.getTerminalGroup((UUID) any);
                times = 1;
            }
        };
    }

    /**
     *  测试创建终端组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCreateTerminalGroup() throws BusinessException {
        new Expectations() {
            {
                terminalGroupService.saveTerminalGroup((TerminalGroupDTO) any);
            }
        };

        api.createTerminalGroup(new CbbTerminalGroupRequest("aaa", UUID.randomUUID()));

        new Verifications() {
            {
                terminalGroupService.saveTerminalGroup((TerminalGroupDTO) any);
                times = 1;
            }
        };
    }

    /**
     *  测试修改终端组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testModifyGroupById() throws BusinessException {
        new Expectations() {
            {
                terminalGroupService.modifyGroupById((TerminalGroupDTO) any);
            }
        };

        api.editTerminalGroup(new CbbEditTerminalGroupRequest(UUID.randomUUID(), "aaa", UUID.randomUUID()));

        new Verifications() {
            {
                terminalGroupService.modifyGroupById((TerminalGroupDTO) any);
                times = 1;
            }
        };
    }

    /**
     *  测试删除终端组
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDeleteTerminalGroup() throws BusinessException {
        final CbbDeleteTerminalGroupRequest deleteTerminalGroupRequest = new CbbDeleteTerminalGroupRequest();
        deleteTerminalGroupRequest.setId(UUID.randomUUID());
        deleteTerminalGroupRequest.setMoveGroupId(UUID.randomUUID());
        new Expectations() {
            {
                terminalGroupServiceTx.deleteGroup((UUID) any, (UUID) any);
                cbbTerminalGroupOperNotifySPI.notifyTerminalGroupChange((CbbTerminalGroupOperNotifyRequest) any);
            }
        };

        api.deleteTerminalGroup(deleteTerminalGroupRequest);

        new Verifications() {
            {
                terminalGroupServiceTx.deleteGroup((UUID) any, (UUID) any);
                times = 1;
                cbbTerminalGroupOperNotifySPI.notifyTerminalGroupChange((CbbTerminalGroupOperNotifyRequest) any);
                times = 1;
            }
        };
    }

    /**
     *  测试获取终端组名称路径
     * @throws BusinessException 业务异常
     */
    @Test
    public void testObtainGroupNamePathArr() throws BusinessException {

        String[] nameStrArr = new String[]{"aa","bb","cc"};
        new Expectations() {
            {
                terminalGroupService.getTerminalGroupNameArr((UUID) any);
                result = nameStrArr;
            }
        };

        String[] groupNameArr = api.obtainGroupNamePathArr(UUID.randomUUID());
        assertEquals(nameStrArr, groupNameArr);

        new Verifications() {
            {
                terminalGroupService.getTerminalGroupNameArr((UUID) any);
                times = 1;
            }
        };
    }


    @Test
    public void testCheckUseGroupNameDuplicationParam()  throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(()-> api.checkUseGroupNameDuplication(null), "Param [CbbTerminalGroupNameDuplicationRequest] must not be null");
        Assert.assertTrue(true);
    }

    @Test
    public void testCheckUseGroupNameDuplicationWhenNameUnique() throws BusinessException {
        UUID id = UUID.randomUUID();
        String grpuName = "groupName";
        UUID parentId = UUID.randomUUID();

        CbbTerminalGroupNameDuplicationRequest request = new CbbTerminalGroupNameDuplicationRequest(id, parentId,
                grpuName);

        new Expectations(){
            {
                terminalGroupService.checkGroupNameUnique((TerminalGroupDTO) any);
                result = false;
            }
        };

        boolean hasDuplication = api.checkUseGroupNameDuplication(request);
        Assert.assertTrue(hasDuplication);

        new Verifications() {
            {
                terminalGroupService.checkGroupNameUnique((TerminalGroupDTO) any);
                times = 1;
            }
        };
    }


    @Test
    public void testCheckUseGroupNameDuplicationWhenNameNotUnique() throws BusinessException {
        UUID id = UUID.randomUUID();
        String grpuName = "groupName";
        UUID parentId = UUID.randomUUID();

        CbbTerminalGroupNameDuplicationRequest request = new CbbTerminalGroupNameDuplicationRequest(id, parentId,
                grpuName);

        new Expectations(){
            {
                terminalGroupService.checkGroupNameUnique((TerminalGroupDTO) any);
                result = true;
            }
        };

        boolean hasDuplication = api.checkUseGroupNameDuplication(request);
        Assert.assertFalse(hasDuplication);

        new Verifications() {
            {
                terminalGroupService.checkGroupNameUnique((TerminalGroupDTO) any);
                times = 1;
            }
        };
    }


    @Test
    public void testCheckUseGroupNameDuplicationWhenException() throws BusinessException {
        UUID id = UUID.randomUUID();
        String grpuName = "groupName";
        UUID parentId = UUID.randomUUID();

        CbbTerminalGroupNameDuplicationRequest request = new CbbTerminalGroupNameDuplicationRequest(id, parentId,
                grpuName);

        new Expectations(){
            {
                terminalGroupService.checkGroupNameUnique((TerminalGroupDTO) any);
                result = new BusinessException("error");
            }
        };

        boolean hasDuplication = api.checkUseGroupNameDuplication(request);
        Assert.assertTrue(hasDuplication);

        new Verifications() {
            {
                terminalGroupService.checkGroupNameUnique((TerminalGroupDTO) any);
                times = 1;
            }
        };
    }
}

