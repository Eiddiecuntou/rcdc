package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.webmvc.api.vo.Sort;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月5日
 * 
 * @author ls
 */
public class AbstractPageQueryTemplateTest {
    
    /**
     * 测试pageQuery，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testPageQueryArgumentIsNull() throws Exception {
        PageQuery pageQuery = new PageQuery();
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(null, TerminalEntity.class), "PageWebRequest不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(new PageSearchRequest(), null), "Class不能为null");
        assertTrue(true);
    }
    
    /**
     * 测试pageQuery，ArgumentException
     * @throws Exception 异常
     */
    @Test
    public void testPageQueryArgumentException() throws Exception {
        PageQuery pageQuery = new PageQuery();
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(new PageSearchRequest(), TerminalEntity.class), "默认排序信息不能为null");
        new MockUp<PageQuery>() {
            @Mock
            public DefaultDataSort getDefaultDataSort() {
                // mock
                return new DefaultDataSort("", Direction.DESC);
            }
        };
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(new PageSearchRequest(), TerminalEntity.class), "默认排序信息不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试pageQuery，字段不匹配
     * @throws Exception 异常
     */
    @Test
    public void testPageQueryArgumentException1() throws Exception {
        PageQuery pageQuery = new PageQuery();
        new MockUp<PageQuery>() {
            @Mock
            public DefaultDataSort getDefaultDataSort() {
                // mock
                return new DefaultDataSort("sds", Direction.DESC);
            }
        };
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(new PageSearchRequest(), TerminalEntity.class), "指定的排序字段sds不合法");
        
        // 指定排序
        PageSearchRequest request = new PageSearchRequest();
        Sort sort = new Sort();
        sort.setSortField("aaa");
        request.setSort(sort);
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(request, TerminalEntity.class), "指定的排序字段[aaa]不合法");
        
        // 精确排序
        request.setSort(null);
        MatchEqual[] matchEqualArr = new MatchEqual[1];
        MatchEqual matchEqual = new MatchEqual();
        matchEqual.setName("sss");
        matchEqualArr[0] = matchEqual;
        
        request.setMatchEqualArr(matchEqualArr);
        ThrowExceptionTester.throwIllegalArgumentException(() -> pageQuery.pageQuery(request, TerminalEntity.class), "指定的精确查询字段[sss]不合法");
        assertTrue(true);
    }
    
    /**
     * 测试pageQuery，没有条件查询
     * @param page mock对象
     * @throws Exception 异常
     */
    @Test
    public void testPageQueryNoCondition(@Mocked Page<TerminalEntity> page) throws Exception {
        Assert.notNull(page, "page can not be null");
        PageQuery pageQuery = new PageQuery();
        new MockUp<PageQuery>() {
            @Mock
            public DefaultDataSort getDefaultDataSort() {
                // mock
                return new DefaultDataSort("terminalId", Direction.DESC);
            }
            
            @Mock
            public Page<TerminalEntity> find(Specification<TerminalEntity> specification, Pageable pageable) {
                // mock
                if (specification == null) {
                    //
                    return null;
                }
                return page;
            }
        };
        PageSearchRequest request = new PageSearchRequest();
        request.setSort(null);
        request.setSearchKeyword("");
        
        MatchEqual[] matchEqualArr = new MatchEqual[0];
        request.setMatchEqualArr(matchEqualArr);
        
        assertEquals(null, pageQuery.pageQuery(request, TerminalEntity.class));
    }
    
    /**
     * 测试pageQuery，
     * @param page mock对象
     * @throws Exception 异常
     */
    @Test
    public void testPageQuery(@Mocked Page<TerminalEntity> page) throws Exception {
        PageQuery pageQuery = new PageQuery();
        EntityFieldMapper entityFieldMapper = new EntityFieldMapper();
        Map<String, String> map = entityFieldMapper.getMapper();
        map.clear();
        entityFieldMapper.mapping("id", "id");
        new MockUp<PageQuery>() {
            @Mock
            public DefaultDataSort getDefaultDataSort() {
                // mock
                return new DefaultDataSort("terminalId", Direction.DESC);
            }
            
            @Mock
            public Page<TerminalEntity> find(Specification<TerminalEntity> specification, Pageable pageable) {
                // mock
                if (specification == null) {
                    //
                    return null;
                }
                return page;
            }
        };
        PageSearchRequest request = new PageSearchRequest();
        Sort sort = new Sort();
        sort.setSortField("terminalId");
        sort.setDirection(com.ruijie.rcos.sk.webmvc.api.vo.Sort.Direction.DESC);
        request.setSort(sort);
        
        MatchEqual[] matchEqualArr = new MatchEqual[1];
        MatchEqual matchEqual = new MatchEqual();
        matchEqual.setName("id");
        matchEqualArr[0] = matchEqual;
        request.setMatchEqualArr(matchEqualArr);
        assertEquals(page, pageQuery.pageQuery(request, TerminalEntity.class));
        map.clear();
    }

    /**
     * Description: Function Description
     * Copyright: Copyright (c) 2019
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年3月5日
     * 
     * @author ls
     */
    private class PageQuery extends AbstractPageQueryTemplate<TerminalEntity> {
        
        @Override
        protected List<String> getSearchColumn() {
            // 
            return null;
        }

        @Override
        protected DefaultDataSort getDefaultDataSort() {
            // 
            return null;
        }

        @Override
        protected void mappingField(EntityFieldMapper entityFieldMapper) {
            
        }

        @Override
        protected Page<TerminalEntity> find(Specification<TerminalEntity> specification, Pageable pageable) {
            // 
            return null;
        }
        
    }
}
