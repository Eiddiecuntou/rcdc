package com.ruijie.rcos.rcdc.terminal.module.def.api.request;


import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageRequest;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import com.ruijie.rcos.sk.webmvc.api.vo.ExactMatch;
import com.ruijie.rcos.sk.webmvc.api.vo.Sort;

/**
 * Description: 分页搜索请求对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/8
 *
 * @author Jarman
 */
public class PageSearchRequest extends DefaultPageRequest {

    @Nullable
    private String searchKeyword;

    @Nullable
    private MatchEqual[] matchEqualArr;

    @Nullable
    private Sort sort;

    @Nullable
    private BetweenTimeRangeMatch betweenTimeRangeMatch;

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }


    public MatchEqual[] getMatchEqualArr() {
        return matchEqualArr;
    }

    public void setMatchEqualArr(MatchEqual[] matchEqualArr) {
        this.matchEqualArr = matchEqualArr;
    }

    @Nullable
    public BetweenTimeRangeMatch getBetweenTimeRangeMatch() {
        return betweenTimeRangeMatch;
    }

    public void setBetweenTimeRangeMatch(@Nullable BetweenTimeRangeMatch betweenTimeRangeMatch) {
        this.betweenTimeRangeMatch = betweenTimeRangeMatch;
    }

    public PageSearchRequest() {

    }

    public PageSearchRequest(PageWebRequest webRequest) {
        Assert.notNull(webRequest, "PageWebRequest不能为null");
        this.setPage(webRequest.getPage());
        this.setLimit(webRequest.getLimit());
        this.setSearchKeyword(webRequest.getSearchKeyword());
        if (webRequest.getSort() != null) {
            this.setSort(webRequest.getSort());
        }
        if (ArrayUtils.isNotEmpty(webRequest.getExactMatchArr())) {
            ExactMatch[] exactMatchArr = webRequest.getExactMatchArr();
            MatchEqual[] matchEqualArr = exactMatchConvert(exactMatchArr);
            this.setMatchEqualArr(matchEqualArr);
        }
    }

    protected MatchEqual[] exactMatchConvert(ExactMatch[] exactMatchArr) {
        Assert.notNull(exactMatchArr, "exactMatchArr must not be null");
        MatchEqual[] matchEqualArr = new MatchEqual[exactMatchArr.length];
        for (int i = 0; i < exactMatchArr.length; i++) {
            ExactMatch exactMatch = exactMatchArr[i];
            MatchEqual matchEqual = new MatchEqual(exactMatch.getName(), exactMatch.getValueArr());
            matchEqualArr[i] = matchEqual;
        }

        return matchEqualArr;
    }

    /**
     * 自定义添加精确匹配查询条件
     * 
     * @param matchEqual 添加对象
     * @return 返回结果
     */
    public PageSearchRequest appendCustomMatchEqual(MatchEqual matchEqual) {
        Assert.notNull(matchEqual, "MatchEqual不能为null");
        MatchEqual[] sourceArr = this.getMatchEqualArr();
        if (ArrayUtils.isEmpty(sourceArr)) {
            this.setMatchEqualArr(new MatchEqual[] {matchEqual});
            return this;
        }
        MatchEqual[] targetArr = new MatchEqual[sourceArr.length + 1];
        System.arraycopy(sourceArr, 0, targetArr, 0, sourceArr.length);
        targetArr[sourceArr.length] = matchEqual;
        this.setMatchEqualArr(targetArr);
        return this;
    }

    /**
     * 复写MatchEqual，UUID类型
     * 
     * @param name 复写对应的name字段
     * @return 返回结果
     */
    public PageSearchRequest coverMatchEqualForUUID(String name) {
        Assert.hasText(name, "name不能为空");
        MatchEqual[] sourceArr = this.getMatchEqualArr();
        if (ArrayUtils.isEmpty(sourceArr)) {
            return this;
        }
        for (MatchEqual source : sourceArr) {
            if (name.equals(source.getName())) {
                Object[] valueArr = source.getValueArr();
                UUID[] idArr = new UUID[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    idArr[i] = UUID.fromString(String.valueOf(valueArr[i]));
                }
                source.setValueArr(idArr);
            }
        }
        return this;
    }

}
