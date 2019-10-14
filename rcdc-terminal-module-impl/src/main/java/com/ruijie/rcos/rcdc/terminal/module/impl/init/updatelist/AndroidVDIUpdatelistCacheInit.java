package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbAndroidVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author XiaoJiaXin
 */
public class AndroidVDIUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<CbbAndroidVDIUpdateListDTO> {

    private static final String UPDATE_LIST_PATH =
            "/opt/upgrade/app/terminal_component/terminal_vdi_android/origin/update.list";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected void fillUpdateList(CbbAndroidVDIUpdateListDTO updateList) {

    }

    @Override
    protected TerminalTypeEnums getTerminalType() {
        return TerminalTypeEnums.VDI_ANDROID;
    }

}
