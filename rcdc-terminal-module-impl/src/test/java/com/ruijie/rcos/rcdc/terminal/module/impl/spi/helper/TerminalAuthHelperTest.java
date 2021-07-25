package com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalWhiteListHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseAuthService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.SystemUpgradeResultInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalLicenseIDVServiceImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalLicenseVoiServiceImpl;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalLicenseVoiUpgradeServiceImpl;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/5/24
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)

public class TerminalAuthHelperTest {

    @Tested
    private TerminalAuthHelper helper;

    @Injectable
    private TerminalLicenseIDVServiceImpl terminalLicenseIDVServiceImpl;

    @Injectable
    private TerminalLicenseVoiUpgradeServiceImpl terminalLicenseVoiUpgradeServiceImpl;

    @Injectable
    private TerminalLicenseVoiServiceImpl terminalLicenseVoiServiceImpl;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    @Injectable
    private CbbTerminalWhiteListHandlerSPI whiteListHandlerSPI;

    @Injectable
    TerminalLicenseAuthService terminalLicenseAuthService;

}
