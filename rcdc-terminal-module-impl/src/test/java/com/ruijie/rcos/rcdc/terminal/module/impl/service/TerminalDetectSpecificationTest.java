package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectSpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        TerminalDetectSpecification specification = new TerminalDetectSpecification(new CbbTerminalDetectPageRequest());
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(null, query, criteriaBuilder), "root can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, null, criteriaBuilder), "query can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> specification.toPredicate(root, query, null), "criteriaBuilder can not be null");
        assertTrue(true);
    }

    /**
     * 测试toPredicate,筛选时间为空
     * 
     * @param root mock root
     * @param query mock query
     * @param criteriaBuilder mock criteriaBuilder
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateToday(@Mocked Root<TerminalDetectionEntity> root, @Mocked CriteriaQuery<?> query,
            @Mocked CriteriaBuilder criteriaBuilder) {
        CbbTerminalDetectPageRequest pageRequest = new CbbTerminalDetectPageRequest();

        TerminalDetectSpecification specification = new TerminalDetectSpecification(pageRequest);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertEquals(null, predicate);
    }

    /**
     * 测试toPredicate,筛选时间不为空
     * 
     * @param root mock root
     * @param query mock query
     * @param criteriaBuilder mock criteriaBuilder
     * @param predicate mock predicate
     * @throws Exception 异常
     */
    @Test
    public void testToPredicateScreenTime(@Mocked Root<TerminalDetectionEntity> root, @Mocked CriteriaQuery<?> query,
            @Mocked CriteriaBuilder criteriaBuilder, @Mocked Predicate predicate) {
        CbbTerminalDetectPageRequest pageRequest = new CbbTerminalDetectPageRequest();
        pageRequest.setEndTime(new Date());
        pageRequest.setStartTime(new Date());

        new Expectations() {
            {
                criteriaBuilder.between(root.get("createTime"), (Date) any, (Date) any);
                result = predicate;
            }
        };


        TerminalDetectSpecification specification = new TerminalDetectSpecification(pageRequest);
        assertEquals(predicate, specification.toPredicate(root, query, criteriaBuilder));
    }
}
