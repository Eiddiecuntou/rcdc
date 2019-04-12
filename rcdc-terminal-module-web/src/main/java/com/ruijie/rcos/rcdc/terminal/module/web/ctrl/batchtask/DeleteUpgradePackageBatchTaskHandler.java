package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask;

import java.util.Iterator;
import java.util.UUID;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbDeleteTerminalUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbUpgradePackageIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbUpgradePackageNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.sk.base.batch.AbstractBatchTaskHandler;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItemResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;

/**
 * 
 * Description: 批量重启终端任务处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月18日
 * 
 * @author nt
 */
public class DeleteUpgradePackageBatchTaskHandler extends AbstractBatchTaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUpgradePackageBatchTaskHandler.class);

    private ProgrammaticOptLogRecorder optLogRecorder;

    private CbbTerminalSystemUpgradePackageAPI terminalUpgradePackageAPI;

    public DeleteUpgradePackageBatchTaskHandler(CbbTerminalSystemUpgradePackageAPI terminalUpgradePackageAPI,
            Iterator<? extends BatchTaskItem> iterator, ProgrammaticOptLogRecorder optLogRecorder) {
        super(iterator);
        this.optLogRecorder = optLogRecorder;
        this.terminalUpgradePackageAPI = terminalUpgradePackageAPI;
    }

    @Override
    public BatchTaskItemResult processItem(BatchTaskItem taskItem) throws BusinessException {
        Assert.notNull(taskItem, "taskItem不能为null");

        CbbDeleteTerminalUpgradePackageRequest deleteRequest = new CbbDeleteTerminalUpgradePackageRequest(taskItem.getItemID());
        try {
            final CbbUpgradePackageNameResponse response = terminalUpgradePackageAPI.deleteUpgradePackage(deleteRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_SUCCESS_LOG, response.getPackageName());
            return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.SUCCESS)
                    .msgKey(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_RESULT_SUCCESS).msgArgs(new String[] {response.getPackageName()})
                    .build();
        } catch (BusinessException ex) {
            LOGGER.error("delete terminal system package fail", ex);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_FAIL_LOG, getPackageName(taskItem.getItemID()),
                    ex.getI18nMessage());
            return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.FAILURE)
                    .msgKey(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_RESULT_FAIL).msgArgs(new String[] {ex.getI18nMessage()}).build();
        }
    }

    private String getPackageName(UUID packageId) {
        CbbUpgradePackageIdRequest idRequest = new CbbUpgradePackageIdRequest(packageId);
        try {
            final CbbUpgradePackageNameResponse response = terminalUpgradePackageAPI.getTerminalUpgradePackageName(idRequest);
            return response.getPackageName();
        } catch (BusinessException e) {
            LOGGER.info("获取升级包名称异常", e);
            return packageId.toString();
        }
    }

    @Override
    public BatchTaskFinishResult onFinish(int successCount, int failCount) {
        return buildDefaultFinishResult(successCount, failCount, BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_RESULT);
    }
}
