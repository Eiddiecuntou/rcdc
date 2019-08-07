package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.ruijie.rcos.sk.webmvc.api.vo.IdLabelEntry;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinSoftComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinSoftUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SoftTerminalUpdateListCacheManager;
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
public class WinSoftComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinSoftComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CbbWinSoftUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "get version request can not be null");

        LOGGER.info("soft windows终端请求版本号");

        if (SoftTerminalUpdateListCacheManager.isCacheNotReady()) {
            LOGGER.info("soft windows终端请求版本号服务端未就绪");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(),
                    new CbbWinSoftUpdateListDTO());
        }

        CbbWinSoftUpdateListDTO updatelist = SoftTerminalUpdateListCacheManager.get(CbbTerminalTypeEnums.WINDOWS);
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        String validateMd5 = request.getValidateMd5();
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist or component is null, return not support");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(),
                    new CbbWinSoftUpdateListDTO());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : {}", JSON.toJSONString(updatelist));
        }

        String versionStr = updatelist.getVersion();
        int version = getVersionFromVerStr(versionStr);
        // 根据版本号对比，版本相同且updatelist的MD5相同，不升级
        if (rainUpgradeVersion.equals(version) && Objects.equals(validateMd5, updatelist.getValidateMd5())) {
            // 版本相同，不升级 0
            LOGGER.debug("version is same, return not need upgrade");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(),
                    new CbbWinSoftUpdateListDTO());
        }

        if (version == 0 || compareVersion(updatelist.getLimitVersion(), rainUpgradeVersion)) {
            LOGGER.debug("limit version is big, return not support");
            // 完整升级
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                    getUpdateListResult(true));
        }

        LOGGER.debug("return start upgrade");

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(),
                getUpdateListResult(false));
    }

    /**
     * 获取升级包updatelist结果
     * 
     * @param isComplete 是否完整升级包
     * @return
     */
    private CbbWinSoftUpdateListDTO getUpdateListResult(boolean isComplete) {
        Map<CbbTerminalTypeEnums, CbbWinSoftUpdateListDTO> updateListCache =
                SoftTerminalUpdateListCacheManager.getUpdateListCache();
        CbbWinSoftUpdateListDTO cacheUpdateListDTO = updateListCache.get(CbbTerminalTypeEnums.WINDOWS);
        List<CbbWinSoftComponentVersionInfoDTO> componentList = cacheUpdateListDTO.getComponentList().stream()
                .filter(component -> (component.getComplete() == isComplete)).collect(Collectors.toList());

        CbbWinSoftUpdateListDTO resultDTO = new CbbWinSoftUpdateListDTO();
        resultDTO.setVersion(cacheUpdateListDTO.getVersion());
        resultDTO.setLimitVersion(cacheUpdateListDTO.getLimitVersion());
        resultDTO.setValidateMd5(cacheUpdateListDTO.getValidateMd5());
        resultDTO.setComponentList(componentList);
        resultDTO.setComponentSize(componentList.size());
        return resultDTO;
    }

}
