package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.google.common.base.Objects;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/12 10:52
 *
 * @author zhangyichi
 */
@Service
public class TerminalGroupHandler {

    /**
     * 过滤掉未分组
     * @param groupList 分组列表
     */
    public void filterDefaultGroup(List<TerminalGroupEntity> groupList) {
        Assert.notNull(groupList, "groupList cannot be null!");

        Iterator<TerminalGroupEntity> iterator = groupList.iterator();
        for (; iterator.hasNext();) {
            TerminalGroupEntity group = iterator.next();
            if (Objects.equal(Constants.DEFAULT_TERMINAL_GROUP_UUID, group.getId())) {
                iterator.remove();
                return;
            }
        }
    }

    /**
     * 组装树形结构
     *
     * @param parentId 父级节点
     * @param groupList 分组列表
     * @param filterGroupId 分组列表
     * @return 树形结构的分组列表
     */
    public TerminalGroupTreeNodeDTO[] assembleGroupTree(@Nullable UUID parentId, List<TerminalGroupEntity> groupList, @Nullable UUID filterGroupId) {
        Assert.notNull(groupList, "groupList cannot be null!");

        if (CollectionUtils.isEmpty(groupList)) {
            return new TerminalGroupTreeNodeDTO[0];
        }

        List<TerminalGroupEntity> subList = new ArrayList<>();
        Iterator<TerminalGroupEntity> iterator = groupList.iterator();
        TerminalGroupEntity defaultGroup = null;
        for (; iterator.hasNext();) {
            TerminalGroupEntity group = iterator.next();
            //过滤的分组跳过
            if (Objects.equal(group.getId(), filterGroupId)) {
                continue;
            }

            if (Objects.equal(parentId, group.getParentId())) {
                //将默认分组放到列表最后
                if (Objects.equal(group.getId(), Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
                    defaultGroup = group;
                    continue;
                }
                subList.add(group);
                iterator.remove();
            }
        }
        if (defaultGroup != null) {
            subList.add(defaultGroup);
        }

        TerminalGroupTreeNodeDTO[] dtoArr = convertToNodeDTO(subList);
        for (TerminalGroupTreeNodeDTO dto : dtoArr) {
            dto.setChildren(assembleGroupTree(dto.getId(), groupList, filterGroupId));
        }

        return dtoArr;
    }

    /**
     * 将分组对象转换为dto数组输出
     *
     * @param subList 分组列表
     * @return dto数组
     */
    private TerminalGroupTreeNodeDTO[] convertToNodeDTO(List<TerminalGroupEntity> subList) {
        int size = subList.size();
        TerminalGroupTreeNodeDTO[] dtoArr = new TerminalGroupTreeNodeDTO[size];
        Stream.iterate(0, i -> i + 1).limit(size).forEach(i -> {
            TerminalGroupEntity entity = subList.get(i);
            TerminalGroupTreeNodeDTO dto = new TerminalGroupTreeNodeDTO();
            entity.converToDTO(dto);
            dto.setEnableDefault(Constants.DEFAULT_TERMINAL_GROUP_UUID.equals(dto.getId()));
            dtoArr[i] = dto;
        });
        return dtoArr;
    }
}
