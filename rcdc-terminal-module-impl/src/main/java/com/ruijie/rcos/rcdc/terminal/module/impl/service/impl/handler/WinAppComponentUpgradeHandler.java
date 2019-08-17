package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public class WinAppComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinAppComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CbbWinAppUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "get version request can not be null");

        LOGGER.debug("windows软终端请求版本号");
        if (TerminalUpdateListCacheManager.isCacheNotReady(TerminalTypeEnums.APP_WINDOWS)) {
            LOGGER.debug("soft windows终端请求版本号服务端未就绪");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(),
                    new CbbWinAppUpdateListDTO());
        }

        CbbWinAppUpdateListDTO updatelist =
                TerminalUpdateListCacheManager.get(TerminalTypeEnums.APP_WINDOWS, CbbWinAppUpdateListDTO.class);
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist不存在或updatelist中组件信息不存在，返回服务器异常响应");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(),
                    new CbbWinAppUpdateListDTO());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : {}", JSON.toJSONString(updatelist));
        }

        String validateMd5 = request.getValidateMd5();
        String versionStr = updatelist.getVersion();
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        // 根据版本号对比，版本相同且updatelist的MD5相同，不升级
        if (rainUpgradeVersion.equals(versionStr)) {
            // 版本相同，不升级 0
            LOGGER.debug("版本号服务端相同，不需要升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(),
                    new CbbWinAppUpdateListDTO());
        }

        if (compareVersion(updatelist.getLimitVersion(), rainUpgradeVersion)) {
            LOGGER.debug("版本号小于服务端版本号，需要进行完整升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                    getUpdateListResult(updatelist, true));
        }

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                getUpdateListResult(updatelist, false));
    }

    /**
     * 获取升级包updatelist结果
     * 
     * @param isComplete 是否完整升级包
     * @return
     */
    private CbbWinAppUpdateListDTO getUpdateListResult(CbbWinAppUpdateListDTO updatelist, boolean isComplete) {
        List<CbbWinAppComponentVersionInfoDTO> componentList = updatelist.getComponentList().stream()
                .filter(component -> (component.getComplete() == isComplete)).collect(Collectors.toList());

        CbbWinAppUpdateListDTO resultDTO = new CbbWinAppUpdateListDTO();
        resultDTO.setVersion(updatelist.getVersion());
        resultDTO.setLimitVersion(updatelist.getLimitVersion());
        resultDTO.setValidateMd5(updatelist.getValidateMd5());
        resultDTO.setComponentList(componentList);
        resultDTO.setComponentSize(componentList.size());
        return resultDTO;
    }

}
