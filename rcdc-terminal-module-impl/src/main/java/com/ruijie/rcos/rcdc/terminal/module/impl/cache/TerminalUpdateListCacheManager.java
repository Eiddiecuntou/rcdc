package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.Map;

import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdatelistDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;


/**
 * 
 * Description: 软终端组件升级包版本信息缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月29日
 * 
 * @author nt
 */
public class TerminalUpdateListCacheManager {


    /**
     * updatelist是否就绪状态缓存
     */
    public static final Map<TerminalTypeEnums, Boolean> UPDATE_LIST_CACHE_READY_STATE_MAP = Maps.newHashMap();

    /**
     * updatelist缓存
     */
    private static final Map<TerminalTypeEnums, CbbCommonUpdatelistDTO> UPDATE_LIST_CACHE_MAP = Maps.newHashMap();

    /**
     * 添加缓存
     *
     * @param <T> updatelist对象
     * @param terminalType 终端平台类型
     * @param updatelist 组件更新列表信息
     */
    public static <T extends CbbCommonUpdatelistDTO> void add(TerminalTypeEnums terminalType, T updatelist) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(updatelist, "updatelist can not be null");

        UPDATE_LIST_CACHE_MAP.put(terminalType, updatelist);
    }

    /**
     * 获取对应软终端类型缓存
     *
     * @param <T> updatelist对象
     * @param terminalType 软终端类型
     * @param clz updatelist对象类型 FIXME 可以删除
     * @return 返回对应缓存对象
     */
    public static <T extends CbbCommonUpdatelistDTO> T get(TerminalTypeEnums terminalType, Class<T> clz) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(clz, "clz can not be null");

        return (T) UPDATE_LIST_CACHE_MAP.get(terminalType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public static Map<TerminalTypeEnums, CbbCommonUpdatelistDTO> getUpdateListCache() {
        return UPDATE_LIST_CACHE_MAP;
    }

    /**
     * 设置updatelist缓存就绪状态
     *
     * @param terminalType 终端类型
     */
    public static void setUpdatelistCacheReady(TerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");
        UPDATE_LIST_CACHE_READY_STATE_MAP.put(terminalType, true);
    }

    /**
     * 设置updatelist缓存为未就绪状态
     *
     * @param terminalType 终端类型
     */
    public static void setUpdatelistCacheNotReady(TerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");
        UPDATE_LIST_CACHE_READY_STATE_MAP.put(terminalType, false);
    }

    /**
     * 判断updatelist缓存是否为未就绪状态
     * FIXME 建议改为 isCacheReady 是否准备就绪 ，正向逻辑更好理解
     * @param terminalType 终端类型
     * @return 是否未就绪
     */
    public static boolean isCacheNotReady(TerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        Boolean isReady = UPDATE_LIST_CACHE_READY_STATE_MAP.get(terminalType);
        if (isReady == null) {
            // FIXME 直接 return true就可以了
            isReady = false;
        }
        return !isReady;
    }
}
