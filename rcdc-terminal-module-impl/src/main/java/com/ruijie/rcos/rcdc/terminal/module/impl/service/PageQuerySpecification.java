package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.BetweenTimeRangeMatch;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;


/**
 * Description: 封装分页查询Specification接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/10
 * 
 * @param <T> 表实体类
 * 
 * @author Jarman
 */
public class PageQuerySpecification<T> implements Specification<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键字
     */
    private String searchKeyword;

    /**
     * 搜索的列名
     */
    private List<String> searchColumnList;

    /**
     * 条件查询，key 字段名，value 字段内容
     */
    private MatchEqual[] matchEqualArr;

    /**
     * 特定时间范围内查询对象
     */
    private BetweenTimeRangeMatch betweenTimeRangeMatch;

    public PageQuerySpecification(String searchKeyword, List<String> searchColumnList, MatchEqual[] matchEqualArr,
                                  BetweenTimeRangeMatch betweenTimeRangeMatch) {
        if (StringUtils.isNotBlank(searchKeyword) && CollectionUtils.isEmpty(searchColumnList)) {
            throw new IllegalArgumentException("请指定需要搜索的列名");
        }
        this.searchKeyword = searchKeyword;
        this.searchColumnList = searchColumnList;
        this.matchEqualArr = matchEqualArr;
        this.betweenTimeRangeMatch = betweenTimeRangeMatch;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Assert.notNull(root, "Root不能为null");
        Assert.notNull(query, "CriteriaQuery不能为null");
        Assert.notNull(cb, "CriteriaBuilder不能为null");
        Predicate likePredicate = buildLikePredicate(root, cb);
        Predicate exactMatchPredicate = buildExactMatchPredicate(root, cb);
        Predicate betweenTimePredicate = buildBetweenTimeRangeMatchPredicate(root, cb);
        List<Predicate> initialPredicateList = Lists.newArrayList(likePredicate, exactMatchPredicate, betweenTimePredicate);

        Predicate[] predicateArr = initialPredicateList.stream().filter(item -> item != null).toArray(Predicate[]::new);
        if (0 == predicateArr.length) {
            // 都为null的情况
            throw new IllegalArgumentException("请指定查询条件");
        }

        return cb.and(predicateArr);
    }

    /**
     * 构建精确匹配条件查询
     */
    private Predicate buildExactMatchPredicate(Root<T> root, CriteriaBuilder cb) {
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            // 没有设置精确匹配查询条件时返回null
            return null;
        }
        List<Predicate> predicateList = new ArrayList<>(matchEqualArr.length);
        for (MatchEqual matchField : matchEqualArr) {
            Object[] fieldValueArr = matchField.getValueArr();
            List<Predicate> matchPredicateList = new ArrayList<>(fieldValueArr.length);
            for (Object fieldValue : fieldValueArr) {
                matchPredicateList.add(cb.equal(root.get(matchField.getName()), fieldValue));
            }
            Predicate[] matchPredicateArr = new Predicate[matchPredicateList.size()];
            matchPredicateList.toArray(matchPredicateArr);
            predicateList.add(cb.or(matchPredicateArr));
        }
        Predicate[] predicateArr = new Predicate[predicateList.size()];
        Predicate matchPredicate = cb.and(predicateList.toArray(predicateArr));
        return matchPredicate;
    }

    /**
     * 构建like条件查询
     */
    private Predicate buildLikePredicate(Root<T> root, CriteriaBuilder cb) {
        if (StringUtils.isBlank(searchKeyword)) {
            // 没有传搜索关键字时返回null
            return null;
        }
        List<Predicate> predicateList = new ArrayList<>(searchColumnList.size());
        searchColumnList.forEach(item -> predicateList.add(cb.like(root.get(item).as(String.class), "%" + searchKeyword + "%")));
        Predicate[] predicateArr = new Predicate[predicateList.size()];
        Predicate likePredicate = cb.or(predicateList.toArray(predicateArr));
        return likePredicate;
    }

    /**
     * 构建特定时间范围查询条件
     */
    private Predicate buildBetweenTimeRangeMatchPredicate(Root<T> root, CriteriaBuilder cb) {
        if (null == betweenTimeRangeMatch) {
            // 没有传特定时间范围查询对象返回null
            return null;
        }
        List<Predicate> predicateList = Lists.newArrayList();

        if (this.betweenTimeRangeMatch.getStartTime() != null) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get(betweenTimeRangeMatch.getTimeKey()), this.betweenTimeRangeMatch.getStartTime()));
        }

        if (this.betweenTimeRangeMatch.getEndTime() != null) {
            predicateList.add(cb.lessThanOrEqualTo(root.get(betweenTimeRangeMatch.getTimeKey()), this.betweenTimeRangeMatch.getEndTime()));
        }
        Predicate[] predicateArr = new Predicate[predicateList.size()];

        return cb.and(predicateList.toArray(predicateArr));
    }

}
