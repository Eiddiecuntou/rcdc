package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Description: linux vdi arm架构终端updatelist缓存初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author ting
 */
@Service
public class LinuxArmUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CommonUpdateListDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxArmUpdatelistCacheInit.class);

    private static final String UPDATE_LIST_PATH =
            "/opt/upgrade/app/terminal_component/terminal_linux_arm/origin/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }


    @Override
    protected void fillUpdateList(CommonUpdateListDTO updatelist) {
        // 这里无需处理
    }

    @Override
    protected TerminalOsArchType getTerminalOsArch() {
        return TerminalOsArchType.LINUX_ARM;
    }

}
