package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.rcdc.terminal.module.impl.util.DeepCopyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbWinAppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public class WinAppComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinAppComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CbbWinAppUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "get version request can not be null");

        LOGGER.debug("windows软终端请求版本号");
        if (!TerminalUpdateListCacheManager.isCacheReady(TerminalTypeEnums.APP_WINDOWS)) {
            LOGGER.debug("soft windows终端请求版本号服务端未就绪");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(), new CbbWinAppUpdateListDTO());
        }

        CbbWinAppUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(TerminalTypeEnums.APP_WINDOWS);
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist不存在或updatelist中组件信息不存在，返回服务器异常响应");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), new CbbWinAppUpdateListDTO());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : {}", JSON.toJSONString(updatelist));
        }

        String versionStr = updatelist.getVersion();
        String rainUpgradeVersion = request.getRainUpgradeVersion();

        if (rainUpgradeVersion.equals(versionStr)) {
            // 版本相同，不升级 0
            LOGGER.debug("版本号服务端相同，不需要升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), new CbbWinAppUpdateListDTO());
        }

        if (isVersionBigger(updatelist.getLimitVersion(), rainUpgradeVersion)) {
            LOGGER.debug("版本号小于服务端版本号，需要进行完整升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), getCompleteUpgradeResult(updatelist));
        }

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), getIncrementUpgradeResult(updatelist));
    }

    private CbbWinAppUpdateListDTO getCompleteUpgradeResult(CbbWinAppUpdateListDTO updatelist) {
        CbbWinAppUpdateListDTO copyUpdateList = DeepCopyUtil.deepCopy(updatelist);
        copyUpdateList.setComponentList(Lists.newArrayList());
        copyUpdateList.setComponentSize(0);
        return copyUpdateList;
    }

    private CbbWinAppUpdateListDTO getIncrementUpgradeResult(CbbWinAppUpdateListDTO updatelist) {
        CbbWinAppUpdateListDTO copyUpdateList = DeepCopyUtil.deepCopy(updatelist);

        // 增量升级，清除完整升级信息
        copyUpdateList.setName(StringUtils.EMPTY);
        copyUpdateList.setCompletePackageName(StringUtils.EMPTY);
        copyUpdateList.setCompletePackageUrl(StringUtils.EMPTY);
        copyUpdateList.setMd5(StringUtils.EMPTY);

        return copyUpdateList;
    }

}
