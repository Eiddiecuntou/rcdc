package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;

/**
 * Description: 软终端升级通用handler
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/7/23 14:51
 *
 * @author conghaifeng
 */
public abstract class AbstractAppComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAppComponentUpgradeHandler.class);

    /**
     * 获取终端组件升级信息
     *
     * @param request 请求参数
     * @return 升级信息
     */
    @Override
    public TerminalVersionResultDTO getVersion(GetVersionDTO request) {
        Assert.notNull(request, "get version request can not be null");

        CbbTerminalOsTypeEnums osType = getTerminalOsType();
        LOGGER.debug("[{}]软终端请求版本号", osType.name());
        if (!TerminalUpdateListCacheManager.isCacheReady(osType)) {
            LOGGER.debug("[{}]软终端请求版本号服务端未就绪", osType.name());
            return new TerminalVersionResultDTO<>(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(), new AppUpdateListDTO());
        }

        AppUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(osType);
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist不存在或updatelist中组件信息不存在，返回服务器异常响应");
            return new TerminalVersionResultDTO<>(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), new AppUpdateListDTO());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : {}", JSON.toJSONString(updatelist));
        }

        String versionStr = updatelist.getVersion();
        String rainUpgradeVersion = request.getRainUpgradeVersion();

        if (rainUpgradeVersion.equals(versionStr)) {
            // 版本相同，不升级 0
            LOGGER.debug("版本号服务端相同，不需要升级");
            return new TerminalVersionResultDTO<>(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), new AppUpdateListDTO());
        }

        boolean isVersionNotLess;
        try {
            isVersionNotLess = isVersionNotLess(rainUpgradeVersion, updatelist.getLimitVersion());
        } catch (Exception e) {
            LOGGER.error("比较osLimit版本失败", e);
            return new TerminalVersionResultDTO<>(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), new AppUpdateListDTO());
        }

        if (isVersionNotLess) {
            LOGGER.debug("版本号不小于服务端版本号，需要进行组件升级");
            return new TerminalVersionResultDTO<>(CbbTerminalComponentUpgradeResultEnums.START.getResult(), getIncrementUpgradeResult(updatelist));
        }

        LOGGER.debug("版本号小于服务端版本号，需要进行完整升级");
        return new TerminalVersionResultDTO<>(CbbTerminalComponentUpgradeResultEnums.START.getResult(), getCompleteUpgradeResult(updatelist));
    }

    private AppUpdateListDTO getCompleteUpgradeResult(AppUpdateListDTO updateList) {
        AppUpdateListDTO copyUpdateList = SerializationUtils.clone(updateList);

        copyUpdateList.setComponentList(Collections.emptyList());
        copyUpdateList.setComponentSize(0);
        return copyUpdateList;
    }

    private AppUpdateListDTO getIncrementUpgradeResult(AppUpdateListDTO updateList) {
        AppUpdateListDTO copyUpdateList = SerializationUtils.clone(updateList);

        // 增量升级，清除完整升级信息
        copyUpdateList.setName(StringUtils.EMPTY);
        copyUpdateList.setCompletePackageName(StringUtils.EMPTY);
        copyUpdateList.setCompletePackageUrl(StringUtils.EMPTY);
        copyUpdateList.setMd5(StringUtils.EMPTY);

        return copyUpdateList;
    }

    /**
     * 获取组件升级的终端类型
     *
     * @return 终端类型
     */
    protected abstract CbbTerminalOsTypeEnums getTerminalOsType();

}
