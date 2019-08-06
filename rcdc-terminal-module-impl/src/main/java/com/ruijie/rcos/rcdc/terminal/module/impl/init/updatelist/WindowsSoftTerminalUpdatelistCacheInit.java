package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinSoftUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SoftTerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author nt
 */
@Service
public class WindowsSoftTerminalUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CbbWinSoftUpdateListDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSoftTerminalUpdatelistCacheInit.class);

    @Override
    protected String getUpdateListPath() {
        return "C:\\Users\\nieti\\Desktop\\shine\\soft-update.list";
    }

    @Override
    protected void cacheInitPre() {
        SoftTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }

    @Override
    protected void cacheInitFinished() {
        SoftTerminalUpdateListCacheManager.setUpdatelistCacheReady();
    }

    @Override
    protected Map<CbbTerminalTypeEnums, CbbWinSoftUpdateListDTO> getUpdateListCacheManager() {
        return SoftTerminalUpdateListCacheManager.getUpdateListCache();
    }

    @Override
    protected void fillUpdateList(CbbWinSoftUpdateListDTO updatelist) {

    }

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.WINDOWS;
    }
}
