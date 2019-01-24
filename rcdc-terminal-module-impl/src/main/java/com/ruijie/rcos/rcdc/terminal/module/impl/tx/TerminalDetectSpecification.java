package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import java.util.Date;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.TerminalDateUtil;

/**
 * 
 * Description: 终端检测查询构造条件
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class TerminalDetectSpecification implements Specification<TerminalDetectionEntity> {

    private static final long serialVersionUID = 1L;

    private CbbDetectDateEnums detectDate;

    public TerminalDetectSpecification(CbbDetectDateEnums detectDate) {
        this.detectDate = detectDate;
    }

    @Override
    public Predicate toPredicate(Root<TerminalDetectionEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Assert.notNull(root, "root can not be null");
        Assert.notNull(query, "query can not be null");
        Assert.notNull(criteriaBuilder, "criteriaBuilder can not be null");

        Date now = new Date();
        Date date = detectDate == CbbDetectDateEnums.TODAY ? now : TerminalDateUtil.addDay(now, -1);
        Predicate predicate = criteriaBuilder.between(root.get("detectTime"), TerminalDateUtil.getDayStart(date), TerminalDateUtil.getDayEnd(date));
        return predicate;
    }

}
