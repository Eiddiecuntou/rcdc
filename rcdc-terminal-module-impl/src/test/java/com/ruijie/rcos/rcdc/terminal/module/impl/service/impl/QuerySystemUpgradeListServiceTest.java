package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
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
public class QuerySystemUpgradeListServiceTest {

    @Tested
    private QuerySystemUpgradeListService service;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

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
                entityFieldMapper.mapping("packageId", "upgradePackageId");
                times = 1;
                entityFieldMapper.mapping("upgradeTaskState", "state");
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
    public void testFindSpecificationIsNull(@Mocked Pageable pageable, @Mocked Page<TerminalSystemUpgradeEntity> page) {
        Specification<TerminalSystemUpgradeEntity> specification = null;

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findAll(specification, pageable);
                result = page;
            }
        };
        service.find(specification, pageable);
        new Verifications() {
            {
                terminalSystemUpgradeDAO.findAll(pageable);
                times = 1;
                terminalSystemUpgradeDAO.findAll(specification, pageable);
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
    public void testFind(@Mocked Specification<TerminalSystemUpgradeEntity> specification, @Mocked Pageable pageable,
            @Mocked Page<TerminalSystemUpgradeEntity> page) {

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findAll(specification, pageable);
                result = page;
            }
        };
        service.find(specification, pageable);
        new Verifications() {
            {
                terminalSystemUpgradeDAO.findAll(pageable);
                times = 0;
                terminalSystemUpgradeDAO.findAll(specification, pageable);
                times = 1;
            }
        };
    }
}
