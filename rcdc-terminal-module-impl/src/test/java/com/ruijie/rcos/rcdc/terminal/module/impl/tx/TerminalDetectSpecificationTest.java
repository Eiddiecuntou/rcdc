package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalDetectSpecificationTest {

    /**
     * 测试toPredicate,参数为空
     * 
     * @param root mock root
     * @param query mock query
     * @param criteriaBuilder mock criteriaBuilder
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateArgumentIsNull(@Mocked Root<TerminalDetectionEntity> root, @Mocked CriteriaQuery<?> query,
            @Mocked CriteriaBuilder criteriaBuilder) throws Exception {
        TerminalDetectSpecification specification = new TerminalDetectSpecification(CbbDetectDateEnums.TODAY);
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(null, query, criteriaBuilder), "root can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, null, criteriaBuilder), "query can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, query, null), "criteriaBuilder can not be null");
        assertTrue(true);
    }

    /**
     * 测试toPredicate,Today
     * 
     * @param root mock root
     * @param query mock query
     * @param criteriaBuilder mock criteriaBuilder
     * @param predicate mock predicate
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateToday(@Mocked Root<TerminalDetectionEntity> root, @Mocked CriteriaQuery<?> query,
            @Mocked CriteriaBuilder criteriaBuilder, @Mocked Predicate predicate) {
        new Expectations() {
            {
                criteriaBuilder.between(root.get("detectTime"), (Date) any, (Date) any);
                result = predicate;
            }
        };
        TerminalDetectSpecification specification = new TerminalDetectSpecification(CbbDetectDateEnums.TODAY);
        assertEquals(predicate, specification.toPredicate(root, query, criteriaBuilder));
    }

    /**
     * 测试toPredicate,Yesterday
     * 
     * @param root mock root
     * @param query mock query
     * @param criteriaBuilder mock criteriaBuilder
     * @param predicate mock predicate
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateYesterday(@Mocked Root<TerminalDetectionEntity> root, @Mocked CriteriaQuery<?> query,
            @Mocked CriteriaBuilder criteriaBuilder, @Mocked Predicate predicate) {
        new Expectations() {
            {
                criteriaBuilder.between(root.get("detectTime"), (Date) any, (Date) any);
                result = predicate;
            }
        };
        TerminalDetectSpecification specification = new TerminalDetectSpecification(CbbDetectDateEnums.YESTERDAY);
        assertEquals(predicate, specification.toPredicate(root, query, criteriaBuilder));
    }
}
