package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/12 13:08
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class TerminalGroupHandlerTest {

    private TerminalGroupHandler handler = new TerminalGroupHandler();

    /**
     * 过滤默认分组
     */
    @Test
    public void testFilterDefaultGroup() {
        List<TerminalGroupEntity> groupEntityList = buildTerminalGroupEntities();
        int size1 = groupEntityList.size();
        handler.filterDefaultGroup(groupEntityList);
        int size2 = groupEntityList.size();
        Assert.assertEquals(1, size1 - size2);
        for (TerminalGroupEntity entity : groupEntityList) {
            if (Objects.equals(entity.getId(), Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
                Assert.fail("默认分组仍存在");
            }
        }
        handler.filterDefaultGroup(groupEntityList);
        int size3 = groupEntityList.size();
        Assert.assertEquals(size2, size3);
    }

    @Test
    public void testAssembleGroupTree() {
        List<TerminalGroupEntity> groupEntityList = buildTerminalGroupEntities();
        TerminalGroupTreeNodeDTO[] nodeDTOArr = handler.assembleGroupTree(null, groupEntityList, null);
        for (TerminalGroupTreeNodeDTO nodeDTO : nodeDTOArr) {
            if ("aaa".equals(nodeDTO.getLabel())) {
                Assert.assertEquals(1, nodeDTO.getChildren().length);
                Assert.assertEquals("aaa111", nodeDTO.getChildren()[0].getLabel());
            }
        }

        groupEntityList = buildTerminalGroupEntities();
        UUID filterGroupId = null;
        for (TerminalGroupEntity entity : groupEntityList) {
            if ("aaa".equals(entity.getName())) {
                filterGroupId = entity.getId();
            }
        }
        nodeDTOArr = handler.assembleGroupTree(null, groupEntityList, filterGroupId);
        for (TerminalGroupTreeNodeDTO nodeDTO : nodeDTOArr) {
            if (Objects.equals(nodeDTO.getId(), filterGroupId)) {
                Assert.fail("过滤分组仍存在");
            }
        }
        Assert.assertEquals(Constants.DEFAULT_TERMINAL_GROUP_UUID, nodeDTOArr[nodeDTOArr.length - 1].getId());

        List<TerminalGroupEntity> emptyGroupEntityList = Lists.newArrayList();
        TerminalGroupTreeNodeDTO[] emptyNodeDTOArr = handler.assembleGroupTree(null, emptyGroupEntityList, null);
        Assert.assertEquals(0, emptyNodeDTOArr.length);
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
}