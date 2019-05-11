package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;

/**
 * 
 * Description: 终端检测查询构造条件
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年1月28日
 * 
 * @author nt
 */
public class TerminalDetectSpecification implements Specification<TerminalDetectionEntity> {

    private static final long serialVersionUID = 1L;

    private Date startTime;

    private Date endTime;

    public TerminalDetectSpecification(CbbTerminalDetectPageRequest request) {
        Assert.notNull(request, "request can not be null");

        endTime = request.getEndTime();
        startTime = request.getStartTime();
    }

    @Override
    public Predicate toPredicate(Root<TerminalDetectionEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Assert.notNull(root, "root can not be null");
        Assert.notNull(query, "query can not be null");
        Assert.notNull(criteriaBuilder, "criteriaBuilder can not be null");

        Predicate predicate = null;
        if (startTime != null && endTime != null) {
            predicate = criteriaBuilder.between(root.get("createTime"), startTime, endTime);
        }
        return predicate;
    }

}
