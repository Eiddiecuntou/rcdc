package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbLinuxIDVUpdateListDTO;
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
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/28
 *
 * @author hs
 */
public class LinuxIDVComponentUpgradeHandler extends AbstractTerminalComponentUpgradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxIDVComponentUpgradeHandler.class);

    @Override
    public TerminalVersionResultDTO<CbbLinuxIDVUpdateListDTO> getVersion(GetVersionRequest request) {
        Assert.notNull(request, "request can not be null");

        LOGGER.debug("Linux IDV终端{[]}请求版本号", request.getTerminalId());
        if (!TerminalUpdateListCacheManager.isCacheReady(CbbTerminalTypeEnums.IDV_LINUX)) {
            LOGGER.info("Linux VDI终端[{}]请求版本号未就绪", request.getTerminalId());
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.PREPARING.getResult(), new CbbLinuxIDVUpdateListDTO());
        }

        CbbLinuxIDVUpdateListDTO updateList = TerminalUpdateListCacheManager.get(CbbTerminalTypeEnums.IDV_LINUX);

        // 判断终端类型升级包是否存在或是否含有组件信息
        if (updateList == null || CollectionUtils.isEmpty(updateList.getComponentList())) {
            LOGGER.debug("updatelist or component is null, terminalType is [{}]", CbbTerminalTypeEnums.IDV_LINUX.toString());
            return new TerminalVersionResultDTO (CbbTerminalComponentUpgradeResultEnums.ABNORMAL.getResult(), new CbbLinuxIDVUpdateListDTO());
        }

        String rainUpgradeVersion = request.getRainUpgradeVersion();
        String validateMd5 = request.getValidateMd5();
        String version = updateList.getVersion();
        CbbLinuxIDVUpdateListDTO updatelistDTO = new CbbLinuxIDVUpdateListDTO(version, updateList.getBaseVersion(), updateList.getComponentSize());

        // 根据版本号对比，版本相同且updatelist的MD5相同，不升级； 不同则根据平台类型筛选出组件信息，无组件信息则不支持升级，有则返回升级信息
        if (rainUpgradeVersion.equals(version) && Objects.equals(validateMd5, updateList.getValidateMd5())) {
            // 版本相同，不升级 0
            LOGGER.debug("version is same, return not need upgrade");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT.getResult(), updatelistDTO);
        }

        // 最低支持版本判断
        Integer terminalVersion = getVersionFromVerStr(rainUpgradeVersion);
        LOGGER.debug("terminal version is {}", terminalVersion);

        if (terminalVersion != 0 && isVersionBigger(updateList.getLimitVersion(), rainUpgradeVersion)) {
            LOGGER.debug("limit version is big, return not support");
            return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), updatelistDTO);
        }


        // 深拷贝对象
        CbbLinuxIDVUpdateListDTO copyUpdateList = SerializationUtils.clone(updateList);

        // 判断是否差异升级
        if (!rainUpgradeVersion.equals(copyUpdateList.getBaseVersion())) {
            LOGGER.info("非差异升级, 清理差异升级信息");
            clearDifferenceUpgradeInfo(copyUpdateList.getComponentList());
        }

        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.START.getResult(), copyUpdateList);
    }

}
