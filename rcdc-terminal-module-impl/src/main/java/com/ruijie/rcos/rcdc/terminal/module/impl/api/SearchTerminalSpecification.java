package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;


/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/4
 *
 * @author Jarman
 */
public class SearchTerminalSpecification implements Specification {

    private String keyword;

    public SearchTerminalSpecification(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
        Assert.notNull(root, " Param [root] must not be null");
        Assert.notNull(query, " Param [query] must not be null");
        Assert.notNull(criteriaBuilder, " Param [criteriaBuilder] must not be null");
        List<Predicate> predicateList = new ArrayList<Predicate>();
        predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + keyword + "%"));
        predicateList.add(criteriaBuilder.like(root.get("ip").as(String.class), "%" + keyword + "%"));
        Predicate[] predicateArr = new Predicate[predicateList.size()];
        query.where(criteriaBuilder.or(predicateList.toArray(predicateArr)));
        query.orderBy(criteriaBuilder.desc(root.get("create_time")));

        return query.getRestriction();
    }
}
