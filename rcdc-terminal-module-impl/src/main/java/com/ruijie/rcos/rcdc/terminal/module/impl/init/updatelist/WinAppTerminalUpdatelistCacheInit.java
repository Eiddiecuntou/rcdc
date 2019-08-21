package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;

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

    private static final String UPDATE_LIST_PATH = "/opt/ftp/terminal/terminal_component/windows_app/update.list";

    private static final String COMPONENT_PACKAGE_DOWNLOAD_URL_PRE = "/terminal_component/windows_app/component/";

    @Override
    protected String getUpdateListPath() {
        return UPDATE_LIST_PATH;
    }

    @Override
    protected void fillUpdateList(CbbWinAppUpdateListDTO updatelist) {
        updatelist.setCompletePackageUrl(COMPONENT_PACKAGE_DOWNLOAD_URL_PRE + updatelist.getCompletePackageName());
        List<CbbWinAppComponentVersionInfoDTO> componentList = updatelist.getComponentList();
        componentList.forEach(component -> component
            .setCompletePackageUrl(COMPONENT_PACKAGE_DOWNLOAD_URL_PRE + component.getCompletePackageName()));
    }

    @Override
    protected TerminalTypeEnums getTerminalType() {
        return TerminalTypeEnums.APP_WINDOWS;
    }
}
