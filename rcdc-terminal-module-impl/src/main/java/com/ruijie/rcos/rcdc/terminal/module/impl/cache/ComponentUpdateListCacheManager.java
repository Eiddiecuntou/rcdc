package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
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
@Service
public class ComponentUpdateListCacheManager {


    /**
     * updatelist更新状态
     */
    public static boolean isUpdate = true;


    private static final Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> UPDATE_LIST_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加缓存
     *
     * @param platform 终端平台类型
     * @param updatelist 组件更新列表信息
     */
    public void addCache(TerminalPlatformEnums platform, CbbTerminalComponentUpdateListDTO updatelist) {
        Assert.notNull(platform, "platform can not be null");
        Assert.notNull(updatelist, "updatelist can not be null");

        UPDATE_LIST_CACHE_MAP.put(platform, updatelist);
    }

    /**
     * 移除缓存
     *
     * @param platform 终端平台类型
     */
    public void removeCache(TerminalPlatformEnums platform) {
        Assert.notNull(platform, "platform can not be null");

        UPDATE_LIST_CACHE_MAP.remove(platform);
    }

    /**
     * 获取对应终端平台类型缓存
     *
     * @param platform 终端平台类型
     * @return 返回对应缓存对象
     */
    public CbbTerminalComponentUpdateListDTO getCache(TerminalPlatformEnums platform) {
        Assert.notNull(platform, "platform can not be null");

        return UPDATE_LIST_CACHE_MAP.get(platform);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> getUpdateListCaches() {
        return UPDATE_LIST_CACHE_MAP;
    }

}
