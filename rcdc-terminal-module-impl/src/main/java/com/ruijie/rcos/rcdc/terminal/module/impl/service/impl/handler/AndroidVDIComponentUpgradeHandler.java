package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbAndroidVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.DeepCopyUtil;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * Description: Function Description
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
        if (!TerminalUpdateListCacheManager.isCacheReady(TerminalTypeEnums.VDI_ANDROID)) {
            LOGGER.info("Android终端[{}]请求版本号未就绪", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult());
        }
        CbbAndroidVDIUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(TerminalTypeEnums.VDI_ANDROID);
        // 终端OTA版本号
        String rainOsVersion = request.getRainOsVersion();
        // 终端update.list的VER版本号
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        // 终端update.list的MD5值
        String validateMd5 = request.getValidateMd5();
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.info("updatelist or component is null, terminalType is [{}]", TerminalTypeEnums.VDI_ANDROID.toString());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult());
        }
        LOGGER.info("updatelist:{}", JSON.toJSONString(updatelist));

        CbbAndroidVDIUpdateListDTO updatelistDTO = new CbbAndroidVDIUpdateListDTO(updatelist);

        // 版本相同且updatelist的MD5相同,不升级
        if (rainOsVersion.equals(updatelist.getVersion()) && Objects.equals(validateMd5, updatelist.getValidateMd5())) {
            // 版本相同,不升级
            LOGGER.info("Android终端[{}]版本一致,不需升级", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), updatelistDTO);
        }

        // 最低支持版本判断,终端OTA版本号低于OS_LIMIT则不支持升级
        Integer terminalOTAVersion = getVersionFromVerStr(rainOsVersion);
        LOGGER.info("终端[" + request.getTerminalId() +"]的OTA版本号为[" + rainOsVersion + "]");
        if (terminalOTAVersion != 0 && isVersionBigger(updatelist.getOsLimit(), rainOsVersion)) {
            LOGGER.info("终端[{}]的OTA版本号低于OS_LIMIT,不支持升级", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), updatelistDTO);
        }

        // 深拷贝对象
        CbbAndroidVDIUpdateListDTO copyUpdateList = DeepCopyUtil.deepCopy(updatelist);

        LOGGER.debug("start upgrade");
        // 判断是否差异升级,终端update.list的版本号(VER)与服务器update.list的BASE版本号相同则为差异升级
        if (!rainUpgradeVersion.equals(copyUpdateList.getBaseVersion())) {
            LOGGER.info("Android终端[{}]组件进行非差异升级, 清理差异升级信息", request.getTerminalId());
            clearDifferenceUpgradeInfo(copyUpdateList.getComponentList());
        }

        LOGGER.info("Android终端[" + request.getTerminalId() + "]组件升级响应：" + JSON.toJSONString(copyUpdateList));

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), copyUpdateList);
    }

    /**
     * 构建响应结果dto
     */
    private TerminalVersionResultDTO buildResult(CbbTerminalComponentUpgradeResultEnums result, CbbAndroidVDIUpdateListDTO updateListDTO) {
        return new TerminalVersionResultDTO(result.getResult(), updateListDTO);
    }
}
