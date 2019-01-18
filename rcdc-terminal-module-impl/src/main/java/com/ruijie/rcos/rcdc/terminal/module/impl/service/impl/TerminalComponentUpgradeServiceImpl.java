package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.ComponentUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;

/**
 * 
 * Description: 终端组件升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
@Service
public class TerminalComponentUpgradeServiceImpl implements TerminalComponentUpgradeService {

    @Autowired
    private ComponentUpdateListCacheManager cacheManager;
    

    @Override
    public TerminalVersionResultDTO getVersion(String rainOsVersion, TerminalPlatformEnums platform) {
        Assert.hasText(rainOsVersion, "rainOsVersion can not be blank");
        Assert.notNull(platform, "platform can not be null");


         CbbTerminalComponentUpdateListDTO updatelist = cacheManager.getCache(platform);
         String version = updatelist.getVersion();
         CbbTerminalComponentUpdateListDTO updatelistDTO = new CbbTerminalComponentUpdateListDTO(
         version, updatelist.getBaseVersion(), updatelist.getComponentSize());
         // 判断终端类型升级包是否存在或是否含有组件信息
         if(updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
         return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(),
         new CbbTerminalComponentUpdateListDTO());
         }
        
         // 根据版本号对比，版本相同，不升级； 不同则根据平台类型筛选出组件信息，无组件信息则不支持升级，有则返回升级信息
         if (rainOsVersion.equals(version)) {
         // 版本相同，不升级 0
         return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(),
         updatelistDTO);
         }
        
         // 最低支持版本判断
         if(compareVersion(updatelist.getLimitVersion(), rainOsVersion)) {
         return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(),
         updatelistDTO);
         }
        
         // 判断updatelist是否处于更新中，若处于更新中，则为未就绪状态
         // TODO 在ip变更spi中需将isupdate设置为更新中状态
         if (ComponentUpdateListCacheManager.isUpdate) {
         return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(),
         updatelistDTO);
         }
        
         return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), updatelist);
    }

    /**
     * 比较版本
     * 
     * @param firstVersion
     * @param secondVersion
     * @return
     */
    private boolean compareVersion(String firstVersion, String secondVersion) {
        int v1 = getVersionFromVerStr(firstVersion);
        int v2 = getVersionFromVerStr(secondVersion);
        return v1 > v2;
    }

    /**
     * 转换版本号为数字
     * 
     * @param version 版本信息
     * @return 数字版本号
     */
    private Integer getVersionFromVerStr(String version) {
        /*
         * 版本号格式： 1.0.0.1
         * 版本号约定：4位数，是否升级判断用前3位即可
         * 第4位用于场景标记（1-云办公，2-云课堂）
         */
        return Integer.valueOf(version.substring(0, version.lastIndexOf(".")).replace(".", ""));
    }
}
