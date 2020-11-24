package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.AppUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dto.CommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Description: 终端组件升级通用handler
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/7
 *
 * @author nt
 */
public abstract class AbstractCommonComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommonComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CommonUpdateListDTO> getVersion(GetVersionDTO request) {
        Assert.notNull(request, "get version request can not be null");

        CbbTerminalOsTypeEnums osType = getTerminalOsType();

        LOGGER.debug("终端系统类型为[{}]的终端请求版本号", osType.name());
        if (!TerminalUpdateListCacheManager.isCacheReady(osType)) {
            LOGGER.debug("终端系统类型为[{}]的终端请求版本号未就绪", osType.name());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult());
        }

        CommonUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(osType);
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist or component is null, return not support");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : {}", JSON.toJSONString(updatelist));
        }

        // 判断是否升级
        if (!isNeedToUpgrade(updatelist, request)) {
            // 版本相同、MD5值相同,不升级
            LOGGER.debug("终端[{}]不需升级", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), updatelist);
        }

        // 判断是否支持升级
        boolean isSupport;
        try {
            isSupport = isSupportUpgrade(updatelist, request);
        } catch (Exception e) {
            LOGGER.error("比较osLimit版本失败", e);
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), new AppUpdateListDTO());
        }

        if (!isSupport) {
            LOGGER.debug("终端[" + request.getTerminalId() + "]的系统版本号低于系统限制版本号[" + updatelist.getOsLimit() + "],不支持升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT_FOR_LOWER_OS_VERSION.getResult(), updatelist);
        }

        // 深拷贝对象
        CommonUpdateListDTO copyUpdateList = SerializationUtils.clone(updatelist);

        LOGGER.info("start upgrade");
        // 判断是否差异升级,终端update.list的版本号(VER)与服务器update.list的BASE版本号相同则为差异升级
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        if (!rainUpgradeVersion.equals(copyUpdateList.getBaseVersion())) {
            LOGGER.debug("终端[{}]组件进行非差异升级, 清理差异升级信息", request.getTerminalId());
            clearDifferenceUpgradeInfo(copyUpdateList.getComponentList());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("终端[" + request.getTerminalId() + "]组件升级响应：" + JSON.toJSONString(copyUpdateList));
        }

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), copyUpdateList);
    }

    private boolean isSupportUpgrade(CommonUpdateListDTO updateList, GetVersionDTO request) {
        if (StringUtils.isBlank(request.getOsInnerVersion()) || StringUtils.isBlank(updateList.getOsLimit())) {
            // 支持旧版本终端未上报osInnerVersion场景升级,或updatelist文件中无oslimit信息的情况
            return true;
        }

        // 终端系统版本号高于OS_LIMIT则支持升级
        return isVersionNotLess(request.getOsInnerVersion(), updateList.getOsLimit());
    }

    /**
     * 清除差异升级信息
     *
     * @param componentList 组件升级信息
     */
    private void clearDifferenceUpgradeInfo(List<CommonComponentVersionInfoDTO> componentList) {
        Assert.notNull(componentList, "componentList cannot be null");
        for (CommonComponentVersionInfoDTO componentInfo : componentList) {
            componentInfo.setIncrementalPackageMd5(null);
            componentInfo.setIncrementalPackageName(null);
            componentInfo.setIncrementalPackageRelativePath(null);
            componentInfo.setIncrementalTorrentMd5(null);
            componentInfo.setIncrementalTorrentUrl(null);
            componentInfo.setBasePackageName(null);
            componentInfo.setBasePackageMd5(null);
        }
    }

    /**
     * 检查终端是否需要升级
     *
     * @param: updatelist updatelist文件内容
     * @param：request 终端信息对象
     * @return: boolean 是否需要升级结果
     */
    private boolean isNeedToUpgrade(CommonUpdateListDTO updatelist, GetVersionDTO request) {
        boolean isVersionEqual = request.getRainUpgradeVersion().equals(updatelist.getVersion());
        boolean isMD5Equal = Objects.equals(request.getValidateMd5(), updatelist.getValidateMd5());
        return !isVersionEqual || !isMD5Equal;
    }

    /**
     * 获取组件升级的终端系统类型
     *
     * @return 终端系统类型
     */
    protected abstract CbbTerminalOsTypeEnums getTerminalOsType();

}
