package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import org.springframework.stereotype.Service;

/**
 * Description: android vdi终端updatelist缓存初始化
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author XiaoJiaXin
 */
@Service
public class AndroidUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CommonUpdateListDTO> {

    private static final String UPDATE_LIST_PATH = "/opt/upgrade/app/terminal_component/terminal_android_arm/origin/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected void fillUpdateList(CommonUpdateListDTO updateList) {

    }

    @Override
    protected TerminalOsArchType getTerminalOsArch() {
        return TerminalOsArchType.ANDROID_ARM;
    }

}
