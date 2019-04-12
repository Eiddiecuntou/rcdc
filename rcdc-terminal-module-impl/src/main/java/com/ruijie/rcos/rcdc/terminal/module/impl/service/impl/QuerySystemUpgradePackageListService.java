package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.google.common.collect.ImmutableList;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.AbstractPageQueryTemplate;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.DefaultDataSort;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.EntityFieldMapper;

/**
 * 
 * Description: 终端刷机包分页查询
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
@Service
public class QuerySystemUpgradePackageListService extends AbstractPageQueryTemplate<TerminalSystemUpgradePackageEntity> {

    @Autowired
    private TerminalSystemUpgradePackageDAO systemUpgradePackageDAO;

    @Override
    protected List<String> getSearchColumn() {
        return ImmutableList.of("packageName");
    }

    @Override
    protected DefaultDataSort getDefaultDataSort() {
        return new DefaultDataSort("uploadTime", Direction.DESC);
    }

    @Override
    protected void mappingField(EntityFieldMapper entityFieldMapper) {

    }

    @Override
    protected Page<TerminalSystemUpgradePackageEntity> find(Specification<TerminalSystemUpgradePackageEntity> specification, Pageable pageable) {
        if (specification == null) {
            systemUpgradePackageDAO.findAll(pageable);
        }
        return systemUpgradePackageDAO.findAll(specification, pageable);
    }

}
