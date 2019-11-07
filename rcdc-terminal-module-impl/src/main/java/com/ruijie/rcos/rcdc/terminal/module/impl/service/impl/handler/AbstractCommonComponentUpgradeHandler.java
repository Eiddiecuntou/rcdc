package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.Objects;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

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
    public TerminalVersionResultDTO<CbbCommonUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "get version request can not be null");

        CbbTerminalTypeEnums terminalType = getTerminalType();

        LOGGER.debug("[{}]终端请求版本号", terminalType.name());
        if (!TerminalUpdateListCacheManager.isCacheReady(terminalType)) {
            LOGGER.debug("[{}]终端请求版本号未就绪", terminalType.name());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult());
        }

        CbbCommonUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(terminalType);
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
            LOGGER.info("Android终端[{}]不需升级", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), updatelist);
        }

        // 判断是否支持升级
        if (!isSupportUpgrade(updatelist, request)) {
            LOGGER.info("终端[" + request.getTerminalId() + "]的系统版本号低于系统限制版本号[" + updatelist.getOsLimit() + "],不支持升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), updatelist);
        }

        // 深拷贝对象
        CbbCommonUpdateListDTO copyUpdateList = SerializationUtils.clone(updatelist);

        LOGGER.info("start upgrade");
        // 判断是否差异升级,终端update.list的版本号(VER)与服务器update.list的BASE版本号相同则为差异升级
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        if (!rainUpgradeVersion.equals(copyUpdateList.getBaseVersion())) {
            LOGGER.info("Android终端[{}]组件进行非差异升级, 清理差异升级信息", request.getTerminalId());
            clearDifferenceUpgradeInfo(copyUpdateList.getComponentList());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("终端[" + request.getTerminalId() + "]组件升级响应：" + JSON.toJSONString(copyUpdateList));
        }

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), copyUpdateList);
    }

    private boolean isSupportUpgrade(CbbCommonUpdateListDTO updateList, GetVersionRequest request) {
        // 终端系统版本号高于OS_LIMIT则支持升级
        return isVersionBigger(request.getOsInnerVersion(), updateList.getOsLimit());
    }

    /**
     * 检查终端是否需要升级
     *
     * @param: updatelist updatelist文件内容
     * @param：request 终端信息对象
     * @return: boolean 是否需要升级结果
     */
    private boolean isNeedToUpgrade(CbbCommonUpdateListDTO updatelist, GetVersionRequest request) {
        boolean isVersionEqual = request.getRainUpgradeVersion().equals(updatelist.getVersion());
        boolean isMD5Equal = Objects.equals(request.getValidateMd5(), updatelist.getValidateMd5());
        return !isVersionEqual || !isMD5Equal;
    }

    /**
     * 获取组件升级的终端类型
     * 
     * @return 终端类型
     */
    protected abstract CbbTerminalTypeEnums getTerminalType();

}
