package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.Test;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 * 
 * @author ls
 */
public class PageQuerySpecificationTest {

    @Mocked 
    private Root root;
    
    @Mocked
    private CriteriaQuery<?> query;
    
    @Mocked
    private CriteriaBuilder cb;
    
    /**
     * 测试构造器
     */
    @Test
    public void testConstructor() {
        // 参数列名为空但关键词不为空
        try {
            new PageQuerySpecification<>("df", new ArrayList<>(), new MatchEqual[1]);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("请指定需要搜索的列名", e.getMessage());
        }
    }
    
    /**
     * 测试toPredicate，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateArgumentIsNull() throws Exception {
        PageQuerySpecification<Object> specification = new PageQuerySpecification<>("", new ArrayList<>(), new MatchEqual[1]);
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(null, query, cb), "Root不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, null, cb), "CriteriaQuery不能为null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, query, null), "CriteriaBuilder不能为null");
        assertTrue(true);
    }
    
    /**
     * 测试toPredicate，查询结果都不为空
     * @param likePredicate mock对象
     * @param exactMatchPredicate mock对象
     * @param resultPredicate mock对象
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateAllNotNull(
            @Mocked Predicate likePredicate,
            @Mocked Predicate exactMatchPredicate,
            @Mocked Predicate resultPredicate) throws Exception {
        new Expectations() {
            {
                cb.and((Predicate[]) any);
                result = exactMatchPredicate;
                cb.or((Predicate[]) any);
                result = likePredicate;
                cb.and(likePredicate, exactMatchPredicate);
                result = resultPredicate;
            }
        };
        
        List<String> searchColumnList = new ArrayList<>();
        searchColumnList.add("sdsd");
        
        MatchEqual[] matchEqualArr = new MatchEqual[1];
        MatchEqual matchEqual = new MatchEqual();
        String[] fieldValueArr = new String[1];
        matchEqual.setValueArr(fieldValueArr);
        matchEqualArr[0] = matchEqual;
        
        PageQuerySpecification<Object> specification = new PageQuerySpecification<>("dfgdf", searchColumnList, matchEqualArr);
        
        assertEquals(resultPredicate, specification.toPredicate(root, query, cb));
    }
    
    /**
     * 测试toPredicate，likePredicate为空
     * @param exactMatchPredicate mock对象
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateLikePredicateIsNull(@Mocked Predicate exactMatchPredicate) throws Exception {
        new Expectations() {
            {
                cb.and((Predicate[]) any);
                result = exactMatchPredicate;
            }
        };
        
        List<String> searchColumnList = new ArrayList<>();
        searchColumnList.add("sdsd");
        
        MatchEqual[] matchEqualArr = new MatchEqual[1];
        MatchEqual matchEqual = new MatchEqual();
        String[] fieldValueArr = new String[1];
        matchEqual.setValueArr(fieldValueArr);
        matchEqualArr[0] = matchEqual;
        
        PageQuerySpecification<Object> specification = new PageQuerySpecification<>("", searchColumnList, matchEqualArr);
        
        assertEquals(exactMatchPredicate, specification.toPredicate(root, query, cb));
    }
    
    /**
     * 测试toPredicate，exactMatchPredicate为空
     * @param likePredicate mock对象
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateExactMatchPredicateIsNull(@Mocked Predicate likePredicate) throws Exception {
        new Expectations() {
            {
                cb.or((Predicate[]) any);
                result = likePredicate;
            }
        };
        
        List<String> searchColumnList = new ArrayList<>();
        searchColumnList.add("sdsd");
        
        MatchEqual[] matchEqualArr = new MatchEqual[0];
        
        PageQuerySpecification<Object> specification = new PageQuerySpecification<>("sds", searchColumnList, matchEqualArr);
        
        assertEquals(likePredicate, specification.toPredicate(root, query, cb));
    }
    
    /**
     * 测试toPredicate，都为空
     * @param likePredicate mock对象
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateAllIsNull(@Mocked Predicate likePredicate) throws Exception {
        List<String> searchColumnList = new ArrayList<>();
        searchColumnList.add("sdsd");
        
        MatchEqual[] matchEqualArr = new MatchEqual[0];
        
        PageQuerySpecification<Object> specification = new PageQuerySpecification<>("", searchColumnList, matchEqualArr);
        
        try {
            specification.toPredicate(root, query, cb);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("请指定查询条件", e.getMessage());
        }
    }

}
