package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.aaa.module.def.api.BaseSystemLogMgmtAPI;
import com.ruijie.rcos.base.aaa.module.def.api.request.systemlog.BaseCreateSystemLogRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 数据盘清空结果应答消息处理
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/8 14:27
 *
 * @author conghaifeng
 */
@DispatcherImplemetion(ShineAction.CLEAR_DATA_DISK_RESULT)
public class ClearDataDiskResultHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private BaseSystemLogMgmtAPI baseSystemLogMgmtAPI;

    public static final Logger LOGGER = LoggerFactory.getLogger(ClearDataDiskResultHandlerSPIImpl.class);

    private static final String RESULT_KEY = "clearResult";

    /**
     * 消息分发方法
     *
     * @param request 请求参数对象 请求参数
     */
    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request,"cbbDispatcherRequest can not be null");
        Assert.hasText(request.getTerminalId(), "terminalId 不能为空");
        Assert.notNull(request.getData(), "报文消息体不能为空");

        String data = request.getData();
        JSONObject jsonObject = JSON.parseObject(data);
        Boolean isClearSuccess = jsonObject.getBoolean(RESULT_KEY);
        LOGGER.info("shine返回清空数据盘结果：" + isClearSuccess);
        if (isClearSuccess == null) {
            LOGGER.error("终端返回清空数据盘是否成功值为null，terminalId = [{}]",request.getTerminalId());
            return;
        }
        if (isClearSuccess) {
            baseSystemLogMgmtAPI.createSystemLog(new BaseCreateSystemLogRequest(BusinessKey.RCDC_TERMINAL_CLEAR_DISK_SUCCESS,
                    request.getTerminalId()));
            LOGGER.warn("终端数据盘清空成功,terminalId = [{}]", request.getTerminalId());
        } else {
            baseSystemLogMgmtAPI.createSystemLog(new BaseCreateSystemLogRequest(BusinessKey.RCDC_TERMINAL_CLEAR_DISK_FAIL,
                    request.getTerminalId()));
            LOGGER.error("终端数据盘清空失败,terminalId = [{}]", request.getTerminalId());
        }
    }
}
