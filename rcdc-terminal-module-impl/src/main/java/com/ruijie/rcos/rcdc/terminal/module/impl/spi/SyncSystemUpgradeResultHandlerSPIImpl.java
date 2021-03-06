package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalLockHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper.SyncSystemUpgradeResultHelper;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;


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
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Autowired
    SyncSystemUpgradeResultHelper upgradeResultHelper;

    @Autowired
    private TerminalLockHelper terminalLockHelper;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");

        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
        TerminalTypeArchType terminalArchType = basicInfoService.obtainTerminalArchType(basicInfoEntity);

        // ?????????
        Lock lock = terminalLockHelper.putAndGetLock(request.getTerminalId());
        lock.lock();

        try {
            TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(terminalArchType);
            upgradeResultHelper.dealSystemUpgradeResult(basicInfoEntity, terminalArchType, handler, request);
        } catch (BusinessException e) {
            LOGGER.error("????????????[" + terminalArchType.name() + "]????????????????????????????????????", e);
            upgradeResultHelper.responseNotUpgrade(request);
            return;
        } finally {
            lock.unlock();
        }

    }
}
