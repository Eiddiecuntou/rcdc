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
    private static final Map<TerminalTypeEnums, Boolean> UPDATE_LIST_CACHE_READY_STATE_MAP = Maps.newHashMap();

    /**
     * updatelist缓存
     */
    private static final Map<TerminalTypeEnums, ? super CbbCommonUpdatelistDTO> UPDATE_LIST_CACHE_MAP = Maps.newHashMap();

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
     * @return 返回对应缓存对象
     */
    public static <T extends CbbCommonUpdatelistDTO> T get(TerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        return (T) UPDATE_LIST_CACHE_MAP.get(terminalType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public static Map<TerminalTypeEnums, ? super CbbCommonUpdatelistDTO> getUpdateListCache() {
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
     *
     * @param terminalType 终端类型
     * @return 是否未就绪
     */
    public static boolean isCacheReady(TerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        Boolean isReady = UPDATE_LIST_CACHE_READY_STATE_MAP.get(terminalType);
        if (isReady == null) {
            return false;
        }
        return isReady;
    }

}
