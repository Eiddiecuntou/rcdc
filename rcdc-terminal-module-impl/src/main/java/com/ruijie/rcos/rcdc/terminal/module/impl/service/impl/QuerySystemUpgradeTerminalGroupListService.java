package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.AbstractPageQueryTemplate;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.DefaultDataSort;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.EntityFieldMapper;

/**
 * 
 * Description: 升级任务终端分组列表查询
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年12月30日
 * 
 * @author nt
 */
@Service
public class QuerySystemUpgradeTerminalGroupListService extends AbstractPageQueryTemplate<TerminalSystemUpgradeTerminalGroupEntity> {

    @Autowired
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    @Override
    protected List<String> getSearchColumn() {
        return ImmutableList.of("sysUpgradeId");
    }

    @Override
    protected DefaultDataSort getDefaultDataSort() {
        return new DefaultDataSort("createTime", Direction.DESC);
    }

    @Override
    protected void mappingField(EntityFieldMapper entityFieldMapper) {
        entityFieldMapper.mapping("upgradeTaskId", "sysUpgradeId");
    }

    @Override
    protected Page<TerminalSystemUpgradeTerminalGroupEntity> find(Specification<TerminalSystemUpgradeTerminalGroupEntity> specification,
            Pageable pageable) {
        if (specification == null) {
            systemUpgradeTerminalGroupDAO.findAll(pageable);
        }
        return systemUpgradeTerminalGroupDAO.findAll(specification, pageable);
    }

}
