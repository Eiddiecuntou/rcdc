package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.util.Map;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.UpgradeFileTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/24
 *
 * @author TING
 */
@Service
public class TerminalSystemUpgradePackageResolverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradePackageResolverFactory.class);

    private static final Map<UpgradeFileTypeEnums, SystemUpgradePackageResolver> RESOLVER_HANDLER = Maps.newHashMap();

    @Autowired
    private LinuxVDISystemUpgradeIsoPackageResolver linuxVDIIsoPackageResolver;

    @Autowired
    private LinuxVDISystemUpgradeZipPackageResolver linuxVDIZipPackageResolver;

    /**
     * 获取终端系统升级包处理对象
     *
     * @param fileName 文件名称
     * @return 升级包处理对象
     * @throws BusinessException 业务异常
     */
    public SystemUpgradePackageResolver getResolver(String fileName) throws BusinessException {
        Assert.hasText(fileName, "fileName can not be null");

        if (CollectionUtils.isEmpty(RESOLVER_HANDLER)) {
            synchronized (RESOLVER_HANDLER) {
                if (CollectionUtils.isEmpty(RESOLVER_HANDLER)) {
                    LOGGER.info("初始化系统升级包处理对象工厂");
                    init();
                }
            }
        }

        String fileType = FileOperateUtil.getFileTypeByFileName(fileName);
        UpgradeFileTypeEnums fileTypeEnums = UpgradeFileTypeEnums.convert(fileType);
        SystemUpgradePackageResolver resolver = RESOLVER_HANDLER.get(fileTypeEnums);

        if (resolver == null) {
            LOGGER.error("系统升级包文件类型为[{}]的升级处理对象不存在", fileType);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_RESOLVER_NOT_EXIST,
                    new String[]{fileType});
        }

        return resolver;
    }

    private void init() {
        RESOLVER_HANDLER.put(UpgradeFileTypeEnums.ISO, linuxVDIIsoPackageResolver);
        RESOLVER_HANDLER.put(UpgradeFileTypeEnums.ZIP, linuxVDIZipPackageResolver);
    }
}
