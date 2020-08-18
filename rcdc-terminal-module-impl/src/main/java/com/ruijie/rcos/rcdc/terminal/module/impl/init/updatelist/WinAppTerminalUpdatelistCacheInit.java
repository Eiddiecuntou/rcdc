package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.impl.dto.WinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.WinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 *
 * @author nt
 */
@Service
public class WinAppTerminalUpdatelistCacheInit extends AbstractUpdatelistCacheInitTemplate<WinAppUpdateListDTO> {

    private static final String UPDATE_LIST_PATH = "/opt/ftp/terminal/terminal_component/windows_app/update.list";

    private static final String COMPONENT_PACKAGE_DOWNLOAD_URL_PRE = "/terminal_component/windows_app/component/";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected void fillUpdateList(WinAppUpdateListDTO updatelist) {
        updatelist.setCompletePackageUrl(COMPONENT_PACKAGE_DOWNLOAD_URL_PRE + updatelist.getCompletePackageName());
        List<WinAppComponentVersionInfoDTO> componentList = updatelist.getComponentList();
        componentList.forEach(component -> component
            .setCompletePackageUrl(COMPONENT_PACKAGE_DOWNLOAD_URL_PRE + component.getCompletePackageName()));
    }

    @Override
    protected CbbTerminalTypeEnums getTerminalType() {
        return CbbTerminalTypeEnums.APP_WINDOWS;
    }
}
