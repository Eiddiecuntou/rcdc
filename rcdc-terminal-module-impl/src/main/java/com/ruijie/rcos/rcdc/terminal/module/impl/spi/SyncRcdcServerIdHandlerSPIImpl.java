package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.RcosGlobalPlatformId;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Description: 终端获取云主机id
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年3月30日
 *
 * @author jarman
 */
@DispatcherImplemetion(ShineAction.SYNC_RCOS_GLOBAL_PLATFORM_ID)
public class SyncRcdcServerIdHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRcdcServerIdHandlerSPIImpl.class);

    private static final String RCOS_GLOBAL_PLATFORM_ID_FILE = "/etc/rcos_global/platform_id";

    private static final String SERVER_ID = "serverId";

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");
        LOGGER.info("请求平台id,action:{}", ShineAction.SYNC_RCOS_GLOBAL_PLATFORM_ID);
        File file = new File(RCOS_GLOBAL_PLATFORM_ID_FILE);
        if (file.exists() && file.isFile()) {
            String platformIdString;
            try {
                platformIdString = FileUtils.readFileToString(file, Charset.defaultCharset());
            } catch (IOException e) {
                LOGGER.error("读取文件:" + RCOS_GLOBAL_PLATFORM_ID_FILE + "异常", e);
                return;
            }
            RcosGlobalPlatformId rcosGlobalPlatformId = JSON.parseObject(platformIdString, RcosGlobalPlatformId.class);
            String platFormId = rcosGlobalPlatformId.getPlatformId();
            CbbResponseShineMessage responseMessage = MessageUtils.buildResponseMessage(request, ImmutableMap.of(SERVER_ID, platFormId));
            messageHandlerAPI.response(responseMessage);
        } else {
            LOGGER.error("文件[" + RCOS_GLOBAL_PLATFORM_ID_FILE + "]不存在");
        }
    }
}
