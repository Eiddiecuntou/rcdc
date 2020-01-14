package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.DefaultDataSort;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.EntityFieldMapper;

import mockit.*;

import java.util.List;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月1日
 * 
 * @author ls
 */
public class QuerySystemUpgradeTerminalGroupListServiceTest {

    @Tested
    private QuerySystemUpgradeTerminalGroupListService service;

    @Injectable
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    /**
     *  获取查询字段
     */
    @Test
    public void testGetSearchColumn() {
        List<String> searchColumnList = service.getSearchColumn();
        assertEquals(1, searchColumnList.size());
        assertEquals("sysUpgradeId", searchColumnList.get(0));
    }

    /**
     * 测试getDefaultDataSort
     */
    @Test
    public void testGetDefaultDataSort() {
        DefaultDataSort defaultDataSort = service.getDefaultDataSort();
        assertEquals("createTime", defaultDataSort.getSortField());
        assertEquals(Direction.DESC, defaultDataSort.getDirection());
    }

    /**
     * 测试mappingField
     * 
     * @param entityFieldMapper mock对象
     */
    @Test
    public void testMappingField(@Mocked EntityFieldMapper entityFieldMapper) {
        service.mappingField(entityFieldMapper);

        new Verifications() {
            {
                entityFieldMapper.mapping("upgradeTaskId", "sysUpgradeId");
                times = 1;
            }
        };
    }

    /**
     * 测试find，specification为null
     * 
     * @param pageable mock对象
     * @param page mock对象
     */
    @Test
    public void testFindSpecificationIsNull(@Mocked Pageable pageable, @Mocked Page<TerminalSystemUpgradeTerminalGroupEntity> page) {
        Specification<TerminalSystemUpgradeTerminalGroupEntity> specification = null;

        new Expectations() {
            {
                systemUpgradeTerminalGroupDAO.findAll(specification, pageable);
                result = page;
            }
        };
        service.find(specification, pageable);
        new Verifications() {
            {
                systemUpgradeTerminalGroupDAO.findAll(pageable);
                times = 1;
                systemUpgradeTerminalGroupDAO.findAll(specification, pageable);
                times = 1;
            }
        };
    }

    /**
     * 测试find
     * 
     * @param specification mock对象
     * @param pageable mock对象
     * @param page mock对象
     */
    @Test
    public void testFind(@Mocked Specification<TerminalSystemUpgradeTerminalGroupEntity> specification, @Mocked Pageable pageable,
            @Mocked Page<TerminalSystemUpgradeTerminalGroupEntity> page) {

        new Expectations() {
            {
                systemUpgradeTerminalGroupDAO.findAll(specification, pageable);
                result = page;
            }
        };
        service.find(specification, pageable);
        new Verifications() {
            {
                systemUpgradeTerminalGroupDAO.findAll(pageable);
                times = 0;
                systemUpgradeTerminalGroupDAO.findAll(specification, pageable);
                times = 1;
            }
        };
    }

}
