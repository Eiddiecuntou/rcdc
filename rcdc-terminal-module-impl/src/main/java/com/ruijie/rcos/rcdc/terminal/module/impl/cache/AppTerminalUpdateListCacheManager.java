package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;


/**
 * 
 * Description: 软终端组件升级包版本信息缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月29日
 * 
 * @author nt
 */
public class AppTerminalUpdateListCacheManager {


    /**
     * updatelist更新状态
     */
    public static boolean isUpdate = true;


    private static final Map<CbbTerminalTypeEnums, CbbWinAppUpdateListDTO> UPDATE_LIST_CACHE_MAP =
            new ConcurrentHashMap<>();

    /**
     * 添加缓存
     *
     * @param terminalType 终端平台类型
     * @param updatelist 组件更新列表信息
     */
    public static void add(CbbTerminalTypeEnums terminalType, CbbWinAppUpdateListDTO updatelist) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(updatelist, "updatelist can not be null");

        UPDATE_LIST_CACHE_MAP.put(terminalType, updatelist);
    }

    /**
     * 获取对应软终端类型缓存
     *
     * @param terminalType 软终端类型
     * @return 返回对应缓存对象
     */
    public static CbbWinAppUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        return UPDATE_LIST_CACHE_MAP.get(terminalType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public static Map<CbbTerminalTypeEnums, CbbWinAppUpdateListDTO> getUpdateListCache() {
        return UPDATE_LIST_CACHE_MAP;
    }


    /**
     * 设置updatelist缓存就绪状态
     */
    public static void setUpdatelistCacheReady() {
        isUpdate = false;
    }

    /**
     * 设置updatelist缓存为未就绪状态
     */
    public static void setUpdatelistCacheNotReady() {
        isUpdate = true;
    }

    /**
     * 判断updatelist缓存是否为未就绪状态
     *
     * @return 是否未就绪
     */
    public static boolean isCacheNotReady() {
        return isUpdate;
    }
}
