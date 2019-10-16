package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxVDIUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/2
 *
 * @author nt
 */
public class LinuxVDIComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxVDIComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CbbLinuxVDIUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "get version request can not be null");

        LOGGER.debug("linux VDI终端请求版本号");
        if (!TerminalUpdateListCacheManager.isCacheReady(TerminalTypeEnums.VDI_LINUX)) {
            LOGGER.debug("linux VDI终端请求版本号未就绪");
            return buildResult(CbbTerminalComponentUpgradeResultEnums.PREPARING, new CbbLinuxVDIUpdateListDTO());
        }

        CbbLinuxVDIUpdateListDTO updatelist = TerminalUpdateListCacheManager.get(TerminalTypeEnums.VDI_LINUX);
        String rainUpgradeVersion = request.getRainUpgradeVersion();
        String validateMd5 = request.getValidateMd5();
        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.debug("updatelist or component is null, return not support");
            return buildResult(CbbTerminalComponentUpgradeResultEnums.ABNORMAL, new CbbLinuxVDIUpdateListDTO());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updatelist : {}", JSON.toJSONString(updatelist));
        }
        String version = updatelist.getVersion();
        CbbLinuxVDIUpdateListDTO updatelistDTO = new CbbLinuxVDIUpdateListDTO(version, updatelist.getBaseVersion(), updatelist.getComponentSize());

        // 根据版本号对比，版本相同且updatelist的MD5相同，不升级； 不同则根据平台类型筛选出组件信息，无组件信息则不支持升级，有则返回升级信息
        if (rainUpgradeVersion.equals(version) && Objects.equals(validateMd5, updatelist.getValidateMd5())) {
            // 版本相同，不升级 0
            LOGGER.debug("version is same, return not need upgrade");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), updatelistDTO);
        }

        // 最低支持版本判断
        Integer terminalVersion = getVersionFromVerStr(rainUpgradeVersion);
        LOGGER.debug("terminal version is {}", terminalVersion);
        if (terminalVersion != 0 && isVersionBigger(updatelist.getLimitVersion(), rainUpgradeVersion)) {
            LOGGER.debug("limit version is big, return not support");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), updatelistDTO);
        }

        // 深拷贝对象
        CbbLinuxVDIUpdateListDTO copyUpdateList = SerializationUtils.clone(updatelist);

        LOGGER.debug("return start upgrade");
        // 判断是否差异升级
        if (!rainUpgradeVersion.equals(copyUpdateList.getBaseVersion())) {
            LOGGER.info("非差异升级, 清理差异升级信息");
            clearDifferenceUpgradeInfo(copyUpdateList.getComponentList());
        }

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), copyUpdateList);
    }

    /**
     * 构建响应结果dto
     */
    private TerminalVersionResultDTO buildResult(CbbTerminalComponentUpgradeResultEnums result, CbbLinuxVDIUpdateListDTO updateListDto) {
        return new TerminalVersionResultDTO(result.getResult(), updateListDto);
    }

}
