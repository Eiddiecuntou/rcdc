package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbAndroidVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * Description: AndroidVDI终端组件升级处理
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/10
 *
 * @author XiaoJiaXin
 */
public class AndroidVDIComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidVDIComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CbbAndroidVDIUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "request can not be null");

        LOGGER.info("Android终端[{}]请求版本号", request.getTerminalId());
        if (!TerminalUpdateListCacheManager.isCacheReady(CbbTerminalTypeEnums.VDI_ANDROID)) {
            LOGGER.info("Android终端[{}]请求版本号未就绪", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult());
        }
        CbbAndroidVDIUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(CbbTerminalTypeEnums.VDI_ANDROID);
        LOGGER.info("updatelist:{}", JSON.toJSONString(updatelist));
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.info("updatelist or component is null, terminalType is [{}]", CbbTerminalTypeEnums.VDI_ANDROID.toString());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult());
        }
        // 判断是否升级
        if (!isNeedToUpgrade(updatelist, request)) {
            // 版本相同、MD5值相同,不升级
            LOGGER.info("Android终端[{}]不需升级", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), updatelist);
        }

        // 判断是否支持升级
        if (!isSupportUpgrade(updatelist, request)) {
            LOGGER.info("终端[" + request.getTerminalId() + "]的OTA版本号低于版本号[" + updatelist.getOsLimit() + "],不支持升级");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), updatelist);
        }

        // 深拷贝对象
        CbbAndroidVDIUpdateListDTO copyUpdateList = SerializationUtils.clone(updatelist);

        LOGGER.info("start upgrade");
        // 判断是否差异升级,终端update.list的版本号(VER)与服务器update.list的BASE版本号相同则为差异升级
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        if (!rainUpgradeVersion.equals(copyUpdateList.getBaseVersion())) {
            LOGGER.info("Android终端[{}]组件进行非差异升级, 清理差异升级信息", request.getTerminalId());
            clearDifferenceUpgradeInfo(copyUpdateList.getComponentList());
        }

        LOGGER.info("Android终端[" + request.getTerminalId() + "]组件升级响应：" + JSON.toJSONString(copyUpdateList));

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), copyUpdateList);
    }

    private boolean isSupportUpgrade(CbbAndroidVDIUpdateListDTO updatelist, GetVersionRequest request) {
        // 终端OTA版本号高于OS_LIMIT则支持升级
        String rainOsVersion = request.getRainOsVersion();
        Integer terminalOTAVersion = getVersionFromVerStr(rainOsVersion);
        LOGGER.info("终端[" + request.getTerminalId() + "]的OTA版本号为[" + rainOsVersion + "]");
        return terminalOTAVersion == 0 || isVersionBigger(rainOsVersion, updatelist.getOsLimit());
    }

    /**
     * 检查终端是否需要升级
     *
     * @param: updatelist updatelist文件内容
     * @param：request 终端信息对象
     * @return: boolean 是否需要升级结果
     */
    private boolean isNeedToUpgrade(CbbAndroidVDIUpdateListDTO updatelist, GetVersionRequest request) {
        boolean isVersionEqual = request.getRainOsVersion().equals(updatelist.getVersion());
        boolean isMD5Equal = Objects.equals(request.getValidateMd5(), updatelist.getValidateMd5());
        return !isVersionEqual || !isMD5Equal;
    }
}
