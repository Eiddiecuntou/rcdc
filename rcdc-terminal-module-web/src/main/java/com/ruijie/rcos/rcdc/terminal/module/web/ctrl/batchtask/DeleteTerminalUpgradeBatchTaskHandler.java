package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.AbstractBatchTaskHandler;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItemResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;

/**
 * 
 * Description: 批量升级终端任务处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月28日
 * 
 * @author nt
 */
public class DeleteTerminalUpgradeBatchTaskHandler extends AbstractBatchTaskHandler {

    private ProgrammaticOptLogRecorder optLogRecorder;

    private Map<UUID, String> idMap;

    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    public DeleteTerminalUpgradeBatchTaskHandler(CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI,
            Map<UUID, String> idMap, Iterator<? extends BatchTaskItem> iterator,
            ProgrammaticOptLogRecorder optLogRecorder) {
        super(iterator);
        this.optLogRecorder = optLogRecorder;
        this.idMap = idMap;
        this.cbbTerminalUpgradeAPI = cbbTerminalUpgradeAPI;
    }

    @Override
    public BatchTaskItemResult processItem(BatchTaskItem taskItem) throws BusinessException {
        Assert.notNull(taskItem, "BatchTaskItem不能为null");

        String terminalId = idMap.get(taskItem.getItemID());

        deleteAddOptLog(terminalId);
        optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_SUCCESS_LOG, terminalId);
        return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.SUCCESS)
                .msgKey(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_RESULT_SUCCESS).msgArgs(new String[] {terminalId}).build();
    }


    private void deleteAddOptLog(String terminalId) throws BusinessException {
        CbbRemoveTerminalSystemUpgradeTaskRequest removeRequest = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        removeRequest.setTerminalId(terminalId);
        cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask(removeRequest);
        optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_SUCCESS_LOG, terminalId);
    }

    @Override
    public void afterException(BatchTaskItem item, Exception e) {
        Assert.notNull(item, "item is not null");
        Assert.notNull(e, "exception is not null");

        String terminalId = idMap.get(item.getItemID());
        if (e instanceof BusinessException) {
            BusinessException ex = (BusinessException) e;
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_FAIL_LOG, terminalId,
                    ex.getI18nMessage());
        } else {
            throw new IllegalStateException("终端升级异常，terminalId为[" + terminalId + "]", e);
        }
    }


    @Override
    public BatchTaskFinishResult onFinish(int successCount, int failCount) {
        return buildDefaultFinishResult(successCount, failCount, BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_RESULT);
    }
}
