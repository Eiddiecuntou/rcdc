package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 前端参数字段映射到数据库实体类字段
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/20
 *
 * @author Jarman
 */
public class EntityFieldMapper {

    private static final Map<String, String> MAP = new HashMap<>();

    /**
     * 把字段映射成指定字段
     * 
     * @param paramField 前端传过来的请求参数字段
     * @param entityField 数据库实体类字段
     */
    public void mapping(String paramField, String entityField) {
        Assert.hasText(paramField, "paramField不能为空");
        Assert.hasText(entityField, "entityField不能为空");
        MAP.put(paramField, entityField);
    }

    Map<String, String> getMapper() {
        return MAP;
    }
}
