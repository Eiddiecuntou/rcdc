package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;

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
    public static boolean isUpdate = false;


    private static final Map<CbbTerminalTypeEnums, CbbTerminalComponentUpdateListDTO> UPDATE_LIST_CACHE_MAP =
            new ConcurrentHashMap<>();

    /**
     * 添加缓存
     *
     * @param terminalType 终端类型
     */
    public void addCache(CbbTerminalTypeEnums terminalType, CbbTerminalComponentUpdateListDTO updatelist) {
        Assert.notNull(terminalType, "terminalType can not be null");
        Assert.notNull(updatelist, "updatelist can not be null");

        UPDATE_LIST_CACHE_MAP.put(terminalType, updatelist);
    }

    /**
     * 移除缓存
     *
     * @param terminalType 终端类型
     */
    public void removeCache(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        UPDATE_LIST_CACHE_MAP.remove(terminalType);
    }

    /**
     * 获取对应终端类型缓存
     *
     * @param terminalType 终端类型
     * @return 返回对应缓存对象
     */
    public CbbTerminalComponentUpdateListDTO getCache(CbbTerminalTypeEnums terminalType) {
        Assert.notNull(terminalType, "terminalType can not be null");

        return UPDATE_LIST_CACHE_MAP.get(terminalType);
    }

    /**
     * 获取缓存集合
     *
     * @return 返回集合对象
     */
    public Map<CbbTerminalTypeEnums, CbbTerminalComponentUpdateListDTO> getUpdateListCaches() {
        return UPDATE_LIST_CACHE_MAP;
    }

}
