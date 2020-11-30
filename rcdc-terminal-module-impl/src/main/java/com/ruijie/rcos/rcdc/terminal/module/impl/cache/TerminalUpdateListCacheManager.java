package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.BaseUpdateListDTO;
import org.springframework.util.Assert;

import java.util.Map;


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
    private static final Map<CbbTerminalOsTypeEnums, Boolean> UPDATE_LIST_CACHE_READY_STATE_MAP = Maps.newHashMap();

    /**
     * updatelist缓存
     */
    private static final Map<CbbTerminalOsTypeEnums, ? super BaseUpdateListDTO> UPDATE_LIST_CACHE_MAP = Maps.newHashMap();

    /**
     * 添加缓存
     *
     * @param <T> updatelist对象
     * @param osType 终端平台类型
     * @param updatelist 组件更新列表信息
     */
    public static <T extends BaseUpdateListDTO> void add(CbbTerminalOsTypeEnums osType, T updatelist) {
        Assert.notNull(osType, "osType can not be null");
        Assert.notNull(updatelist, "updatelist can not be null");

        UPDATE_LIST_CACHE_MAP.put(osType, updatelist);
    }

    /**
     * 获取对应终端系统的升级信息缓存
     *
     * @param <T> updatelist对象
     * @param osType 终端系统类型
     * @return 返回对应缓存对象
     */
    public static <T extends BaseUpdateListDTO> T get(CbbTerminalOsTypeEnums osType) {
        Assert.notNull(osType, "osType can not be null");

        return (T) UPDATE_LIST_CACHE_MAP.get(osType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public static Map<CbbTerminalOsTypeEnums, ? super BaseUpdateListDTO> getUpdateListCache() {
        return UPDATE_LIST_CACHE_MAP;
    }

    /**
     * 设置updatelist缓存就绪状态
     *
     * @param osType 终端系统类型
     */
    public static void setUpdatelistCacheReady(CbbTerminalOsTypeEnums osType) {
        Assert.notNull(osType, "osType can not be null");
        UPDATE_LIST_CACHE_READY_STATE_MAP.put(osType, true);
    }

    /**
     * 设置updatelist缓存为未就绪状态
     *
     * @param osType 终端系统类型
     */
    public static void setUpdatelistCacheNotReady(CbbTerminalOsTypeEnums osType) {
        Assert.notNull(osType, "osType can not be null");
        UPDATE_LIST_CACHE_READY_STATE_MAP.put(osType, false);
    }

    /**
     * 判断updatelist缓存是否为未就绪状态
     *
     * @param osType 终端系统类型
     * @return 是否未就绪
     */
    public static boolean isCacheReady(CbbTerminalOsTypeEnums osType) {
        Assert.notNull(osType, "osType can not be null");

        Boolean isReady = UPDATE_LIST_CACHE_READY_STATE_MAP.get(osType);
        if (isReady == null) {
            return false;
        }
        return isReady;
    }

}
