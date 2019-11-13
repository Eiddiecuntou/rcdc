package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
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
public class AndroidVDIUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CbbCommonUpdateListDTO> {

    private static final String UPDATE_LIST_PATH = "/opt/upgrade/app/terminal_component/terminal_vdi_android/origin/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected void fillUpdateList(CbbCommonUpdateListDTO updateList) {

    }

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.VDI_ANDROID;
    }

}
