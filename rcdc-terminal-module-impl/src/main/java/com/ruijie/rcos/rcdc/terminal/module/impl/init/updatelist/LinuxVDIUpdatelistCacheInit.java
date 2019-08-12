package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.VDITerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: linux vdi终端updatelist缓存初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author nt
 */
@Service
public class LinuxVDIUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CbbLinuxVDIUpdateListDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxVDIUpdatelistCacheInit.class);

    private static final String UPDATE_LIST_PATH =
            "/opt/upgrade/app/terminal_component/terminal_vdi_linux/origin/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected Map<CbbTerminalTypeEnums, CbbLinuxVDIUpdateListDTO> getUpdateListCacheManager() {
        return VDITerminalUpdateListCacheManager.getUpdateListCache();
    }

    @Override
    protected void fillUpdateList(CbbLinuxVDIUpdateListDTO updatelist) {

    }

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.LINUX;
    }

    @Override
    protected void cacheInitFinished() {
        VDITerminalUpdateListCacheManager.setUpdatelistCacheReady();
    }

    @Override
    protected void cacheInitPre() {
        VDITerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }
}
