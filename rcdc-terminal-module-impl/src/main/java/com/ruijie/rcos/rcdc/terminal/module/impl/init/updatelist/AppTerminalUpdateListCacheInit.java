package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.AppTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTerminalUpdateListCacheInit.class);

    public static final String DEFAULT_VALIDATE_MD5 = "default_validate_md5";

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

        updatelist.setValidateMd5(obtainUpdateListMd5());
    }

    private String obtainUpdateListMd5() {

        File updatelistFile = new File(updateListPath);
        try {
            return StringUtils.bytes2Hex(Md5Builder.computeFileMd5(updatelistFile));
        } catch (IOException e) {
            LOGGER.error("updatelist file md5 calc error", e);
            return DEFAULT_VALIDATE_MD5;
        }
    }

    @Override
    protected CbbTerminalOsTypeEnums getTerminalOsType() {
        return this.osType;
    }
}
