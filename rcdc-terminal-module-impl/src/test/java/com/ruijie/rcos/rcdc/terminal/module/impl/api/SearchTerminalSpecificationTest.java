package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月23日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class SearchTerminalSpecificationTest {

    private SearchTerminalSpecification specification = new SearchTerminalSpecification("key");

    /**
     * 测试toPredicate，参数为空
     * 
     * @param root mock Root
     * @param query mock CriteriaQuery
     * @param criteriaBuilder mock CriteriaBuilder
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateArgumentIsNull(@Injectable Root root, @Injectable CriteriaQuery query, @Injectable CriteriaBuilder criteriaBuilder)
            throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(null, query, criteriaBuilder),
                "Param [root] must not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, null, criteriaBuilder),
                "Param [query] must not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, query, null),
                "Param [criteriaBuilder] must not be null");
        assertTrue(true);
    }

    /**
     * 测试toPredicate，
     * 
     * @param root mock Root
     * @param query mock CriteriaQuery
     * @param criteriaBuilder mock CriteriaBuilder
     */
    @Test
    public void testToPredicate(@Injectable Root root, @Injectable CriteriaQuery query, @Injectable CriteriaBuilder criteriaBuilder) {
        try {
            specification.toPredicate(root, query, criteriaBuilder);
        } catch (Exception e) {
            fail();
        }
    }

}
