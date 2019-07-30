package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.runner.RunWith;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/29
 *
 * @author hs
 */
@RunWith(JMockit.class)
public class TerminalLogoAPIImplTest {

    @Tested
    private TerminalLogoAPIImpl terminalLogoAPI;

    @Injectable
    private ConfigFacade configFacade;

    @Injectable
    private GlobalParameterAPI globalParameterAPI;

    @Injectable
    private TerminalLogoService terminalLogoService;


}
