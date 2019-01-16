package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.Localelanguage;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.BuildShineResponseMessageUtils;
import com.ruijie.rcos.sk.base.i18n.SkyEngineLocaleHolder;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

@DispatcherImplemetion(ReceiveTerminalEvent.GET_I18N_LANG)
public class GetI18nLangHandlerSPIImpl implements CbbDispatcherHandlerSPI {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GetI18nLangHandlerSPIImpl.class);
    
    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;
    
    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");

        LOGGER.debug("=====终端获取国际化语言报文===={}", request.getData());
        try {
            Locale locale = SkyEngineLocaleHolder.getInstance().getLocale();
            Localelanguage localeLanguage = new Localelanguage();
            localeLanguage.setLang(locale.getLanguage());
            CbbResponseShineMessage responseMessage = BuildShineResponseMessageUtils.buildResponseMessage(request, localeLanguage);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端获取国际化语言消息应答失败", e);
        }
        
    }
    
}
