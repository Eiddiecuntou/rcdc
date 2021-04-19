package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper.SyncSystemUpgradeResultHelper;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/12
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.REPORT_SYSTEM_UPGRADE_RESULT)
public class SyncSystemUpgradeResultHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSystemUpgradeResultHandlerSPIImpl.class);


    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Autowired
    SyncSystemUpgradeResultHelper upgradeResultHelper;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");

        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
        CbbTerminalTypeEnums terminalType = obtainTerminalType(basicInfoEntity);

        TerminalSystemUpgradeHandler handler;
        try {
            handler = handlerFactory.getHandler(terminalType);
        } catch (BusinessException e) {
            LOGGER.error("终端类型[" + terminalType.name() + "]获取系统升级处理对象失败", e);
            upgradeResultHelper.responseNotUpgrade(request);
            return;
        }

        upgradeResultHelper.dealSystemUpgradeResult(basicInfoEntity, terminalType, handler, request);
    }

    CbbTerminalTypeEnums obtainTerminalType(TerminalEntity terminalEntity) {

        CbbTerminalPlatformEnums terminalPlatform = terminalEntity.getPlatform();
        String osType = terminalEntity.getTerminalOsType();

        // TODO 临时解决方案，后续版本需修订
        if (Constants.SPECIAL_PRODUCT_ID_CT3120.equals(terminalEntity.getProductId())) {
            LOGGER.info("3120终端系统升级返回IDV平台");
            return CbbTerminalTypeEnums.convert(CbbTerminalPlatformEnums.IDV.name(), osType);
        }

        if (terminalPlatform == CbbTerminalPlatformEnums.VOI) {
            LOGGER.info("VOI平台类型终端快刷转换成IDV类型");
            return CbbTerminalTypeEnums.convert(CbbTerminalPlatformEnums.IDV.name(), osType);
        }

        return CbbTerminalTypeEnums.convert(terminalPlatform.name(), osType);

    }


}
