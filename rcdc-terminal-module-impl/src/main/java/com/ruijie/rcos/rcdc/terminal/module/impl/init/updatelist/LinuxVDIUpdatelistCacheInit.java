package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
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
    protected void fillUpdateList(CbbLinuxVDIUpdateListDTO updatelist) {
        // 这里无需处理
    }

    @Override
    protected TerminalTypeEnums getTerminalType() {
        return TerminalTypeEnums.VDI_LINUX;
    }

}
