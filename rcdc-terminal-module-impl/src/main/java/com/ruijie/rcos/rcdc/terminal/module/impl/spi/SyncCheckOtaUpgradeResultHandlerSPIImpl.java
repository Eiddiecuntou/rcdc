package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckOtaUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CheckOtaUpgradeResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.SYNC_CHECK_OTA_UPGRADE_RESULT)
public class SyncCheckOtaUpgradeResultHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncCheckOtaUpgradeResultHandlerSPIImpl.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");
        CheckOtaUpgradeResult checkOtaUpgradeResult = convertJsondata(request);
        addUpgradeTerminal(checkOtaUpgradeResult);

    }

    private CheckOtaUpgradeResult convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        CheckOtaUpgradeResult checkOtaUpgradeResult = JSON.parseObject(jsonData, CheckOtaUpgradeResult.class);
        return checkOtaUpgradeResult;
    }

    private void addUpgradeTerminal(CheckOtaUpgradeResult checkOtaUpgradeResult) {
        Assert.notNull(checkOtaUpgradeResult, "checkOtaUpgradeResult can not be null");
        if (checkOtaUpgradeResult.getCheckOtaUpgradeResult() == CheckOtaUpgradeResultEnums.NEED_UPGRADE) {
            TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO.findFirstByPackageType(TerminalPlatformEnums.RK3188);
            TerminalSystemUpgradeTerminalEntity upgradeTerminal = new TerminalSystemUpgradeTerminalEntity();
            upgradeTerminal.setSysUpgradeId(upgradePackage.getId());
            upgradeTerminal.setTerminalId(checkOtaUpgradeResult.getTerminalId());
            upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
            upgradeTerminal.setCreateTime(new Date());
            systemUpgradeTerminalDAO.save(upgradeTerminal);
        }

    }
}
