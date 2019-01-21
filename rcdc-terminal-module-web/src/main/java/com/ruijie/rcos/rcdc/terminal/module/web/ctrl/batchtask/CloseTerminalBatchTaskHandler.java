package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.*;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Description: 批量关闭终端任务处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 *
 * @author jarman
 */
public class CloseTerminalBatchTaskHandler extends AbstractBatchTaskHandler {

    private ProgrammaticOptLogRecorder optLogRecorder;

    private CbbTerminalOperatorAPI terminalOperatorAPI;

    private Map<UUID, String> idMap;

    public CloseTerminalBatchTaskHandler(CbbTerminalOperatorAPI terminalOperatorAPI, Map<UUID, String> idMap,
                                         Iterator<? extends BatchTaskItem> iterator, ProgrammaticOptLogRecorder optLogRecorder) {
        super(iterator);
        this.optLogRecorder = optLogRecorder;
        this.idMap = idMap;
        this.terminalOperatorAPI = terminalOperatorAPI;
    }

    @Override
    public BatchTaskItemResult processItem(BatchTaskItem taskItem) throws BusinessException {
        Assert.notNull(taskItem, "BatchTaskItem不能为null");
        String terminalId = idMap.get(taskItem.getItemID());
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        terminalOperatorAPI.shutdown(request);
        
        CbbTerminalBasicInfoResponse baiscInfoResp = terminalOperatorAPI.getTerminalBaiscInfo(request);
        String terminalName = baiscInfoResp.getTerminalName();
        optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CLOSE_SUCCESS_LOG, terminalName, terminalId);
        return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.SUCCESS)
                .msgKey(BusinessKey.RCDC_TERMINAL_CLOSE_RESULT_SUCCESS)
                .msgArgs(new String[]{terminalName, terminalId})
                .build();
    }

    @Override
    public void afterException(BatchTaskItem item, Exception e) {
        Assert.notNull(item, "item is not null");
        Assert.notNull(e, "exception is not null");
        if (e instanceof BusinessException) {
            BusinessException ex = (BusinessException) e;
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CLOSE_FAIL_LOG, ex.getI18nMessage());
        } else {
            throw new IllegalStateException("关闭终端异常，terminalId为[" + item.getItemID() + "]", e);
        }
    }


    @Override
    public BatchTaskFinishResult onFinish(int successCount, int failCount) {
        return buildDefaultFinishResult(successCount, failCount, BusinessKey.RCDC_TERMINAL_CLOSE_RESULT);
    }
}
