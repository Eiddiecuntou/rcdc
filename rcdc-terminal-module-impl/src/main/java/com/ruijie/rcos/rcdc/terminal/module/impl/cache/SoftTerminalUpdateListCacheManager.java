package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinSoftUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;

/**
 * 
 * Description: 软终端组件升级包版本信息缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月15日
 * 
 * @author nt
 */
public class SoftTerminalUpdateListCacheManager {


    /**
     * updatelist更新状态
     */
    public static boolean isUpdate = true;


    private static final Map<CbbTerminalTypeEnums, CbbWinSoftUpdateListDTO> UPDATE_LIST_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加缓存
     *
     * @param terminalType 软终端类型
     * @param updatelist 组件更新列表信息
     */
    public static void add(CbbTerminalTypeEnums terminalType, CbbWinSoftUpdateListDTO updatelist) {
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
    public static CbbWinSoftUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        return UPDATE_LIST_CACHE_MAP.get(terminalType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public static Map<CbbTerminalTypeEnums, CbbWinSoftUpdateListDTO> getUpdateListCache() {
        return UPDATE_LIST_CACHE_MAP;
    }


    /**
     * 设置updatelist缓存就绪状态
     */
    public static void setUpdatelistCacheReady(){
        isUpdate = false;
    }

    /**
     * 设置updatelist缓存为未就绪状态
     */
    public static void setUpdatelistCacheNotReady(){
        isUpdate = true;
    }

    public static boolean isCacheNotReady() {
        return isUpdate;
    }
}
