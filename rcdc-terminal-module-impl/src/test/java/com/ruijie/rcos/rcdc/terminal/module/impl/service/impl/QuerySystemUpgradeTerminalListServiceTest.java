package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.DefaultDataSort;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.EntityFieldMapper;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月1日
 * 
 * @author ls
 */
public class QuerySystemUpgradeTerminalListServiceTest {

    @Tested
    private QuerySystemUpgradeTerminalListService service;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    /**
     * 测试getDefaultDataSort
     */
    @Test
    public void testGetDefaultDataSort() {
        DefaultDataSort defaultDataSort = service.getDefaultDataSort();
        assertEquals("state", defaultDataSort.getSortField());
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
                entityFieldMapper.mapping("terminalUpgradeState", "state");
                times = 1;
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
    public void testFindSpecificationIsNull(@Mocked Pageable pageable, @Mocked Page<TerminalSystemUpgradeTerminalEntity> page) {
        Specification<TerminalSystemUpgradeTerminalEntity> specification = null;

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findAll(specification, pageable);
                result = page;
            }
        };
        service.find(specification, pageable);
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findAll(pageable);
                times = 1;
                systemUpgradeTerminalDAO.findAll(specification, pageable);
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
    public void testFind(@Mocked Specification<TerminalSystemUpgradeTerminalEntity> specification, @Mocked Pageable pageable,
            @Mocked Page<TerminalSystemUpgradeTerminalEntity> page) {

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findAll(specification, pageable);
                result = page;
            }
        };
        service.find(specification, pageable);
        new Verifications() {
            {
                systemUpgradeTerminalDAO.findAll(pageable);
                times = 0;
                systemUpgradeTerminalDAO.findAll(specification, pageable);
                times = 1;
            }
        };
    }

}
