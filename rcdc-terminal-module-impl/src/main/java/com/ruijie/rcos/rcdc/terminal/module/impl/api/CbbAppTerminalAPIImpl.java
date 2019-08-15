package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbAppTerminalAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDownLoadUrlResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/15
 *
 * @author nt
 */
public class CbbAppTerminalAPIImpl implements CbbAppTerminalAPI {

    private static final String WINDOWS_APP_COMPONENT_DIR =
            "/opt/ftp/terminal/terminal_component/windows_app/component/";

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbAppTerminalAPIImpl.class);

    @Override
    public CbbDownLoadUrlResponse getWindowsAppDownloadUrl() throws BusinessException {

        CbbWinAppUpdateListDTO listDTO =
                TerminalUpdateListCacheManager.get(TerminalTypeEnums.APP_WINDOWS, CbbWinAppUpdateListDTO.class);
        // 获取updatelist中完整组件的信息，从中获取全量包文件路径
        if (TerminalUpdateListCacheManager.isCacheNotReady(TerminalTypeEnums.APP_WINDOWS)) {
            LOGGER.error("windows软终端updatelist缓存未就绪");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_WINDOWS_APP_UPDATELIST_CACHE_NOT_READY);
        }

        if (listDTO == null || CollectionUtils.isEmpty(listDTO.getComponentList())) {
            LOGGER.error("windows软终端updatelist信息异常");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPDATELIST_CACHE_INCORRECT);
        }

        for (CbbWinAppComponentVersionInfoDTO versionInfo : listDTO.getComponentList()) {
            if (versionInfo.getComplete()) {
                return new CbbDownLoadUrlResponse(WINDOWS_APP_COMPONENT_DIR + versionInfo.getCompletePackageName());
            }
        }
        // 无全量包信息
        LOGGER.error("windows软终端updatelist信息异常");
        throw new BusinessException(BusinessKey.RCDC_TERMINAL_COMPONENT_UPDATELIST_CACHE_INCORRECT);
    }
}
