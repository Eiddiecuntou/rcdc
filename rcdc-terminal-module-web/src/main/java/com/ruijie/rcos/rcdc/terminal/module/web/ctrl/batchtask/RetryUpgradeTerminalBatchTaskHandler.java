package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRetryUpgradeTerminalRequest;
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
 * Description: 批量重试终端升级任务处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月28日
 * 
 * @author nt
 */
public class RetryUpgradeTerminalBatchTaskHandler extends AbstractBatchTaskHandler {

    private ProgrammaticOptLogRecorder optLogRecorder;

    private Map<UUID, String> idMap;

    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    public RetryUpgradeTerminalBatchTaskHandler(CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI,
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

        TerminalUpgradeBatchTaskItem upgradeItem = (TerminalUpgradeBatchTaskItem) taskItem;
        String terminalId = idMap.get(upgradeItem.getItemID());
        UUID upgradeTaskId = upgradeItem.getUpgradeTaskId();

        return retryUpgradeTerminalAddOptLog(terminalId, upgradeTaskId);
    }

    private BatchTaskItemResult retryUpgradeTerminalAddOptLog(String terminalId, UUID upgradeTaskId)
            throws BusinessException {
        CbbRetryUpgradeTerminalRequest retryRequest = new CbbRetryUpgradeTerminalRequest();
        retryRequest.setTerminalId(terminalId);
        retryRequest.setUpgradeTaskId(upgradeTaskId);
        try {
            cbbTerminalUpgradeAPI.retryUpgradeTerminal(retryRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_SUCCESS_LOG, terminalId);
            return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.SUCCESS)
                    .msgKey(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_RESULT_SUCCESS).msgArgs(new String[] {terminalId})
                    .build();
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_FAIL_LOG, terminalId, e.getI18nMessage());
            return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.FAILURE)
                    .msgKey(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_RESULT_FAIL)
                    .msgArgs(new String[] {terminalId, e.getI18nMessage()}).build();
        }
    }

    @Override
    public BatchTaskFinishResult onFinish(int successCount, int failCount) {
        return buildDefaultFinishResult(successCount, failCount, BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_RESULT);
    }
}
