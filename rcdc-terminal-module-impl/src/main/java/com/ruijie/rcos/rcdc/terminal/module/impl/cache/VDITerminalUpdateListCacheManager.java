package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;

/**
 * 
 * Description: 系统组件升级包版本信息缓存
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月15日
 * 
 * @author nt
 */
public class VDITerminalUpdateListCacheManager {


    /**
     * updatelist更新状态
     */
    private static boolean isUpdate = true;


    private static final Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> UPDATE_LIST_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加缓存
     *
     * @param terminalType 终端平台类型
     * @param updatelist 组件更新列表信息
     */
    public static void add(CbbTerminalTypeEnums terminalType, CbbLinuxVDIUpdateListDTO updatelist) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(updatelist, "updatelist can not be null");

        UPDATE_LIST_CACHE_MAP.put(terminalType, updatelist);
    }

    /**
     * 移除缓存
     *
     * @param terminalType 终端平台类型
     */
    public static void remove(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "platform can not be null");

        UPDATE_LIST_CACHE_MAP.remove(terminalType);
    }

    /**
     * 获取对应终端平台类型缓存
     *
     * @param terminalType 终端平台类型
     * @return 返回对应缓存对象
     */
    public static CbbLinuxVDIUpdateListDTO get(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "platform can not be null");

        return UPDATE_LIST_CACHE_MAP.get(terminalType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public static Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> getUpdateListCache() {
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
