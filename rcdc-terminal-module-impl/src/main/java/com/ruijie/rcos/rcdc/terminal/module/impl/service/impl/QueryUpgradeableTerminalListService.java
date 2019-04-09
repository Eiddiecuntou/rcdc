package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.google.common.collect.ImmutableList;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.UpgradeableTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.ViewUpgradeableTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.AbstractPageQueryTemplate;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.DefaultDataSort;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.EntityFieldMapper;

/**
 * 
 * Description: 终端升级服务实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
@Service
public class QueryUpgradeableTerminalListService extends AbstractPageQueryTemplate<ViewUpgradeableTerminalEntity> {

    @Autowired
    private UpgradeableTerminalDAO upgradeableTerminalDAO;

    @Override
    protected List<String> getSearchColumn() {
        return ImmutableList.of("terminalName", "ip");
    }

    @Override
    protected DefaultDataSort getDefaultDataSort() {
        return new DefaultDataSort("createTime", Direction.DESC);
    }

    @Override
    protected void mappingField(EntityFieldMapper entityFieldMapper) {

    }

    @Override
    protected Page<ViewUpgradeableTerminalEntity> find(Specification<ViewUpgradeableTerminalEntity> specification, Pageable pageable) {
        if (specification == null) {
            upgradeableTerminalDAO.findAll(pageable);
        }
        return upgradeableTerminalDAO.findAll(specification, pageable);
    }

}
