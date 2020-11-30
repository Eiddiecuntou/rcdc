package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.AppTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
public class AppTerminalUpdateListCacheInit extends AbstractUpdatelistCacheInitTemplate<AppUpdateListDTO> {

    private String updateListPath;

    private String componentPackageDownloadUrlPre;

    private CbbTerminalOsTypeEnums osType;

    public AppTerminalUpdateListCacheInit(CbbTerminalOsTypeEnums osType) {
        Assert.notNull(osType, "osType can not be null");

        this.osType = osType;
        AppTerminalOsTypeEnums appOsType = AppTerminalOsTypeEnums.valueOf(osType.name());
        this.updateListPath = appOsType.getUpdateListPath();
        this.componentPackageDownloadUrlPre = appOsType.getComponentPackageDownloadUrlPre();
    }

    public AppTerminalUpdateListCacheInit() {
    }

    @Override
    protected String getUpdateListPath() {
        return updateListPath;
    }

    @Override
    protected void fillUpdateList(AppUpdateListDTO updatelist) {
        updatelist.setCompletePackageUrl(componentPackageDownloadUrlPre + updatelist.getCompletePackageName());
        List<AppComponentVersionInfoDTO> componentList = updatelist.getComponentList();
        componentList.forEach(component -> component
                .setCompletePackageUrl(componentPackageDownloadUrlPre + component.getCompletePackageName()));
    }

    @Override
    protected CbbTerminalOsTypeEnums getTerminalOsType() {
        return this.osType;
    }
}
