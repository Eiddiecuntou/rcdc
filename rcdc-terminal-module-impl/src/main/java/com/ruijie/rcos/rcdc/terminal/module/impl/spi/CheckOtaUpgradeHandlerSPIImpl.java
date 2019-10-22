package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalOtaUpgradeInfo;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
@DispatcherImplemetion(ShineAction.CHECK_UPGRADE_OTA)
public class CheckOtaUpgradeHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckOtaUpgradeHandlerSPIImpl.class);

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getData(), "request.getData() can not be blank");
        TerminalOtaUpgradeInfo terminalOtaUpgradeInfo = getTerminaOtaUpgradeInfo();
        CbbResponseShineMessage<TerminalOtaUpgradeInfo> responseMessage = MessageUtils.buildResponseMessage(request, terminalOtaUpgradeInfo);
        messageHandlerAPI.response(responseMessage);
    }

    private TerminalOtaUpgradeInfo getTerminaOtaUpgradeInfo() {
        TerminalOtaUpgradeInfo upgradeInfo = new TerminalOtaUpgradeInfo();
        TerminalSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO.findFirstByPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        List<CbbSystemUpgradeTaskStateEnums> stateList = Arrays
                .asList(new CbbSystemUpgradeTaskStateEnums[] {CbbSystemUpgradeTaskStateEnums.UPGRADING});
        List<TerminalSystemUpgradeEntity> upgradingTaskList = systemUpgradeDAO
                .findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId(), stateList);
        if (upgradePackage.getIsDelete() == true || CollectionUtils.isEmpty(upgradingTaskList)) {
            upgradeInfo.setOtaVersion(StringUtils.EMPTY);
            upgradeInfo.setOtaMD5(StringUtils.EMPTY);
            upgradeInfo.setOtaSeedLink(StringUtils.EMPTY);
            upgradeInfo.setOtaSeedMD5(StringUtils.EMPTY);
            upgradeInfo.setUpgradeMode(CbbSystemUpgradeModeEnums.CLOSE_UPGRADE);
        }
        upgradeInfo.setOtaVersion(upgradePackage.getPackageVersion());
        upgradeInfo.setOtaMD5(upgradePackage.getFileMD5());
        upgradeInfo.setOtaSeedLink(upgradePackage.getSeedPath());
        upgradeInfo.setOtaSeedMD5(upgradePackage.getSeedMD5());
        upgradeInfo.setUpgradeMode(upgradePackage.getUpgradeMode());
        return upgradeInfo;
    }

}
