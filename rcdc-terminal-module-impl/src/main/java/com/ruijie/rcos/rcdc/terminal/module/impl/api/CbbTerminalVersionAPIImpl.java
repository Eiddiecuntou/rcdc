package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalVersionAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 *
 * Description: 客户端版本信息API
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021年09月27日
 *
 * @author linke
 */
public class CbbTerminalVersionAPIImpl implements CbbTerminalVersionAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalVersionAPIImpl.class);

    @Override
    public String getTerminalVersion(CbbCpuArchType cbbCpuArchType, String terminalOsType) {
        Assert.notNull(cbbCpuArchType, "cbbCpuArchType不能为空");
        Assert.hasText(terminalOsType, "terminalOsType不能为空");

        CbbTerminalOsTypeEnums osType = CbbTerminalOsTypeEnums.valueOf(terminalOsType.toUpperCase());
        TerminalOsArchType osArchType = TerminalOsArchType.convert(osType, cbbCpuArchType);
        LOGGER.info("操作系统架构为：{}", osArchType.name());

        AppUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(osArchType);
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist不存在或updatelist中组件信息不存在，返回服务器异常响应");
            // 返回空
            return null;
        }

        return updatelist.getVersion();
    }
}
