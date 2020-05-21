package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.BetweenTimeRangeMatch;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: 分页条件查询抽象模板
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/10
 *
 * @param <T> 数据库表Entity
 * @author Jarman
 */
public abstract class AbstractPageQueryTemplate<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPageQueryTemplate.class);

    /**
     * 分页条件查询
     *
     * @param request 查询参数对象
     * @param clz 需要查询的数据库Entity类
     * @return 返回分页数据
     */
    public final Page<T> pageQuery(PageSearchRequest request, Class<T> clz) {
        Assert.notNull(request, "PageWebRequest不能为null");
        Assert.notNull(clz, "Class不能为null");
        // 精确匹配字段映射成数据库Entity字段
        request.setMatchEqualArr(mappingField(request.getMatchEqualArr()));
        // 排序字段映射成数据库Entity字段
        mappingSortField(request);
        // 检查排序字段和条件查询字段合法性
        checkMatchField(request, clz);
        // 检查默认排序字段是否合法
        DefaultDataSort defaultDataSort = getDefaultDataSort();
        Assert.notNull(defaultDataSort, "默认排序信息不能为null");
        String defaultSortField = defaultDataSort.getSortField();
        Assert.hasText(defaultSortField, "默认排序信息不能为空");
        if (!isMatchField(defaultSortField, clz)) {
            throw new IllegalArgumentException("指定的排序字段" + defaultSortField + "不合法");
        }
        // 构建Pageable
        Pageable pageable = getPageRequest(request, defaultSortField, defaultDataSort.getDirection());
        String searchKeyword = request.getSearchKeyword();
        MatchEqual[] matchEqualArr = request.getMatchEqualArr();
        BetweenTimeRangeMatch betweenTimeRangeMatch = request.getBetweenTimeRangeMatch();

        if (StringUtils.isBlank(searchKeyword) && ArrayUtils.isEmpty(matchEqualArr) && Objects.isNull(betweenTimeRangeMatch)) {
            // 没有条件查询
            return find(null, pageable);
        }
        List<String> searchColumnList = getSearchColumn();
        // 构建specification
        Specification<T> specification = new PageQuerySpecification<>(searchKeyword, searchColumnList, matchEqualArr, betweenTimeRangeMatch);

        return find(specification, pageable);
    }

    private void mappingSortField(PageSearchRequest request) {
        if (request.getSort() == null) {
            return;
        }

        Map<String, String> filedMappingResult = getMappingFieldResult();
        String sortFieldName = request.getSort().getSortField();
        if (filedMappingResult.containsKey(sortFieldName)) {
            request.getSort().setSortField(filedMappingResult.get(sortFieldName));
        }
    }

    private MatchEqual[] mappingField(MatchEqual[] matchEqualArr) {
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            return new MatchEqual[0];
        }
        Map<String, String> filedMappingResult = getMappingFieldResult();
        MatchEqual[] targetMatchEqualArr = new MatchEqual[matchEqualArr.length];
        for (int i = 0; i < matchEqualArr.length; i++) {
            MatchEqual matchEqual = matchEqualArr[i];
            String filedName = matchEqual.getName();
            if (filedMappingResult.containsKey(filedName)) {
                String entityField = filedMappingResult.get(filedName);
                matchEqual.setName(entityField);
                LOGGER.info("字段[{}]映射到Entity字段[{}]", filedName, entityField);
            }
            targetMatchEqualArr[i] = matchEqual;
        }
        return targetMatchEqualArr;
    }

    private Map<String, String> getMappingFieldResult() {
        EntityFieldMapper entityFieldMapper = new EntityFieldMapper();
        mappingField(entityFieldMapper);
        Map<String, String> filedMappingResult = entityFieldMapper.getMapper();
        return filedMappingResult;
    }

    /**
     * 搜索对应的表列
     */
    protected abstract List<String> getSearchColumn();

    /**
     * 获取默认排序
     */
    protected abstract DefaultDataSort getDefaultDataSort();

    /**
     * 前端查询字段映射到数据库字段,根据需实现映射代码
     *
     * @param entityFieldMapper
     */
    protected abstract void mappingField(EntityFieldMapper entityFieldMapper);

    /**
     * 分页查询
     *
     * @param specification
     * @param pageable
     * @return
     */
    protected abstract Page<T> find(Specification<T> specification, Pageable pageable);

    /**
     * 封装获取分页对象
     *
     * @param defaultSortField 没有指定排序字段时使用的默认排序字段
     * @param defaultDirection 默认排序方式
     * @return 分页请求对象
     */
    private Pageable getPageRequest(PageSearchRequest request, String defaultSortField, Direction defaultDirection) {
        Assert.notNull(defaultSortField, "请指定默认排序字段");
        Assert.notNull(defaultDirection, "请指定默认排序方式");
        int page = request.getPage();
        int limit = request.getLimit();
        if (request.getSort() == null) {
            // 没有指定排序字段
            LOGGER.debug("没有指定排序字段，使用默认排序字段：[{}],排序方式为：[{}]", defaultSortField, defaultDirection);
            return PageRequest.of(page, limit, new Sort(defaultDirection, defaultSortField));
        }
        // 有指定排序字段和排方式
        Direction directionEnum = Direction.fromString(request.getSort().getDirection().name());
        Sort sort = new Sort(directionEnum, request.getSort().getSortField());
        return PageRequest.of(page, limit, sort);
    }

    /**
     * 检查排序字段和条件查询的字段是否合法
     */
    private void checkMatchField(PageSearchRequest request, Class<T> clz) {
        if (request.getSort() != null) {
            String sortField = request.getSort().getSortField();
            boolean isMatch = isMatchField(sortField, clz);
            if (!isMatch) {
                throw new IllegalArgumentException("指定的排序字段[" + sortField + "]不合法");
            }
        }
        MatchEqual[] matchEqualArr = request.getMatchEqualArr();
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            LOGGER.debug("没有指定精确匹配查询字段");
            return;
        }
        for (MatchEqual exactMatch : matchEqualArr) {
            boolean isMatch = isMatchField(exactMatch.getName(), clz);
            if (!isMatch) {
                throw new IllegalArgumentException("指定的精确查询字段[" + exactMatch.getName() + "]不合法");
            }
        }
    }

    private boolean isMatchField(String targetField, Class<T> clz) {
        Field[] fieldArr = clz.getDeclaredFields();
        for (Field field : fieldArr) {
            if (targetField.equals(field.getName())) {
                return true;
            }
        }
        LOGGER.error("在[{}]类中不存在[{}]字段", clz.getName(), targetField);
        return false;
    }
}
