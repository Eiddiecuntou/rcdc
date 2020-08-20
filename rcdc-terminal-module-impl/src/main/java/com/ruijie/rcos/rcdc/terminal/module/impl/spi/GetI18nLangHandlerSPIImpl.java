package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import java.util.Locale;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.Localelanguage;
import com.ruijie.rcos.sk.base.i18n.SkyEngineLocaleHolder;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * 
 * Description: 终端获取国际化信息SPI
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月21日
 * 
 * @author nt
 */
@DispatcherImplemetion(ShineAction.GET_I18N_LANG)
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
            CbbResponseShineMessage responseMessage = MessageUtils.buildResponseMessage(request, localeLanguage);
            messageHandlerAPI.response(responseMessage);
        } catch (Exception e) {
            LOGGER.error("终端获取国际化语言消息应答失败", e);
        }

    }

}
