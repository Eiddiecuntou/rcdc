package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalComponentUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalVersionRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalVersionResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.ComponentUpdateListCacheManager;

/**
 * 
 * Description: 终端组件升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月15日
 * 
 * @author nt
 */
public class CbbTerminalComponentUpgradeAPIImpl implements CbbTerminalComponentUpgradeAPI {

    @Autowired
    private ComponentUpdateListCacheManager cacheManager;

    @Override
    public CbbTerminalVersionResponse getVersion(CbbTerminalVersionRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbTerminalComponentUpdateListDTO totalUpdateList = cacheManager.getCache(CbbTerminalTypeEnums.ALL);

        String version = totalUpdateList.getVersion();
        CbbTerminalComponentUpdateListDTO updatelistDTO = new CbbTerminalComponentUpdateListDTO(
                version, totalUpdateList.getBaseVersion(), totalUpdateList.getComponentSize());
        // 根据版本号对比，版本相同，不升级； 不同则根据平台类型筛选出组件信息，无组件信息则不支持升级，有则返回升级信息
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        if (rainUpgradeVersion.equals(version)) {
            // 版本相同，不升级 0
            return new CbbTerminalVersionResponse(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(),
                    updatelistDTO);
        }
        
        // 最低支持版本判断
        if(compareVersion(totalUpdateList.getMinimumVersion(), rainUpgradeVersion)) {
            return new CbbTerminalVersionResponse(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(),
                    updatelistDTO);
        }

        // 判断终端类型是否含有组件信息
        CbbTerminalComponentUpdateListDTO updatelist = cacheManager.getCache(request.getTerminalType());
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            return new CbbTerminalVersionResponse(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(),
                    updatelistDTO);
        }

        // 判断updatelist是否处于更新中，若处于更新中，则为未就绪状态
        if (ComponentUpdateListCacheManager.isUpdate) {
            return new CbbTerminalVersionResponse(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(),
                    updatelistDTO);
        }

        return new CbbTerminalVersionResponse(CbbTerminalComponentUpgradeResultEnums.START.getResult(), updatelist);
    }

    /**
     * 比较版本
     * @param firstVersion 
     * @param secVersion
     * @return
     */
    private boolean compareVersion(String firstVersion, String secVersion) {
        int v1 = getVersionFromVerStr(firstVersion);
        int v2 = getVersionFromVerStr(secVersion);
        return v1 > v2;
    }

    private Integer getVersionFromVerStr(String version) {
        // 版本号格式： 1.0.0.1
        return Integer.valueOf(version.substring(0, version.lastIndexOf(".")).replace(".", ""));
    }
    
}
