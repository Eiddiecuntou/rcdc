package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.AppTerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author nt
 */
@Service
public class WinAppTerminalUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CbbWinAppUpdateListDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinAppTerminalUpdatelistCacheInit.class);

    private static final String UPDATE_LIST_PATH = "/opt/ftp/terminal/terminal_component/windows_app/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected void cacheInitPre() {
        AppTerminalUpdateListCacheManager.setUpdatelistCacheNotReady();
    }

    @Override
    protected void cacheInitFinished() {
        AppTerminalUpdateListCacheManager.setUpdatelistCacheReady();
    }

    @Override
    protected Map<CbbTerminalTypeEnums, CbbWinAppUpdateListDTO> getUpdateListCacheManager() {
        return AppTerminalUpdateListCacheManager.getUpdateListCache();
    }

    @Override
    protected void fillUpdateList(CbbWinAppUpdateListDTO updatelist) {
        try {
            updatelist.setValidateMd5(StringUtils.bytes2Hex(Md5Builder.computeFileMd5(new File(getUpdateListPath()))));
        } catch (IOException e) {
            LOGGER.error("计算updatelist文件MD5值异常", e);
        }
    }

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.WINDOWS;
    }
}
