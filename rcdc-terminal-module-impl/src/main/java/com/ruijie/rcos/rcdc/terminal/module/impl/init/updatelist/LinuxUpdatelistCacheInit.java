package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import org.springframework.stereotype.Service;

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
public class LinuxUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CommonUpdateListDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxUpdatelistCacheInit.class);

    private static final String UPDATE_LIST_PATH =
            "/opt/upgrade/app/terminal_component/terminal_linux/origin/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }


    @Override
    protected void fillUpdateList(CommonUpdateListDTO updatelist) {
        // 这里无需处理
    }

    @Override
    protected CbbTerminalOsTypeEnums getTerminalOsType() {
        return CbbTerminalOsTypeEnums.LINUX;
    }

}
