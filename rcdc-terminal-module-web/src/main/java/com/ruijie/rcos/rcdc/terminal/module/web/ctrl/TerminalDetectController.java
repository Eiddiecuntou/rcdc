package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.DetectPageWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.StartBatDetectWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.TerminalDetectListContentVO;
import com.ruijie.rcos.sk.base.batch.AbstractBatchTaskHandler;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskStatus;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItem;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;

/**
 * Description: 终端检测
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/15
 *
 * @author nt
 */
@Controller
@RequestMapping("/cbb/terminal/detect")
@EnableCustomValidate(enable = false)
public class TerminalDetectController {

    @Autowired
    private CbbTerminalOperatorAPI operatorAPI;

    /**
     * 批量开启终端检测
     *
     * @param request 请求参数
     * @param optLogRecorder 日志记录对象
     * @param builder 批量任务处理对象
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "start")
    public DefaultWebResponse startDetect(StartBatDetectWebRequest request, ProgrammaticOptLogRecorder optLogRecorder,
            BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        String[] idArr = request.getIdArr();
        UUID cbbIdArr = getCbbTerminalId(idArr);
        final Iterator<DefaultBatchTaskItem> iterator = Stream.of(cbbIdArr)
                .map(id -> DefaultBatchTaskItem.builder().itemId(id).itemName("终端检测").build()).iterator();
        // StartDetectBatchtaskHandler handler = new StartDetectBatchtaskHandler(operatorAPI, iterator, builder);
//        for (String id : idArr) {
//            startDetectAddOptLog(optLogRecorder, id);
//        }
        return DefaultWebResponse.Builder.success();
    }

    private UUID getCbbTerminalId(String[] idArr) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 获取终端检测分页列表
     *
     * @param request 请求参数
     * @return 检测分页列表
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "list")
    public DefaultWebResponse list(DetectPageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalDetectPageRequest pageRequest = new CbbTerminalDetectPageRequest();
        pageRequest.setDate(request.getDate());
        pageRequest.setLimit(request.getLimit());
        pageRequest.setPage(request.getPage());
        DefaultPageResponse<CbbTerminalDetectDTO> listDetectResp = operatorAPI.listDetect(pageRequest);

        CbbTerminalDetectResultRequest resultRequest = new CbbTerminalDetectResultRequest();
        resultRequest.setDetectDate(request.getDate());
        CbbDetectResultResponse detectResultResp = operatorAPI.getDetectResult(resultRequest);

        TerminalDetectListContentVO contentVO = new TerminalDetectListContentVO();
        contentVO.setItemArr(listDetectResp.getItemArr());
        contentVO.setTotal(listDetectResp.getTotal());
        contentVO.setResult(detectResultResp.getResult());

        return DefaultWebResponse.Builder.success(contentVO);
    }

    protected class StartDetectBatchtaskHandler extends AbstractBatchTaskHandler {

        private final ProgrammaticOptLogRecorder optLogRecorder;

        private final CbbTerminalOperatorAPI operatorAPI;

        protected StartDetectBatchtaskHandler(CbbTerminalOperatorAPI operatorAPI,
                Iterator<? extends BatchTaskItem> iterator, ProgrammaticOptLogRecorder optLogRecorder) {
            super(iterator);
            this.operatorAPI = operatorAPI;
            this.optLogRecorder = optLogRecorder;
        }

        @Override
        public BatchTaskFinishResult onFinish(int successCount, int failCount) {
            String[] argArr = new String[] {String.valueOf(successCount), String.valueOf(failCount)};
            BatchTaskStatus status = BatchTaskStatus.SUCCESS;
            if (failCount == 0) {
                return buildBatchTaskFinishResult(status, argArr);
            }
            if (successCount == 0) {
                status = BatchTaskStatus.FAILURE;
            } else {
                status = BatchTaskStatus.PARTIAL_SUCCESS;
            }
            return buildBatchTaskFinishResult(status, argArr);
        }

        private BatchTaskFinishResult buildBatchTaskFinishResult(BatchTaskStatus status, String[] argArr) {
            return DefaultBatchTaskFinishResult.builder().batchTaskStatus(status)
                    .msgKey(BusinessKey.RCDC_RCO_TERMINAL_DETECT_BATCH_TASK_RESULT).msgArgs(argArr).build();
        }

        @Override
        public BatchTaskItemResult processItem(BatchTaskItem taskItem) throws BusinessException {
            Assert.notNull(taskItem, "taskItem can not be null");
            return startDetectAddOptLog(taskItem.getItemID());
        }
        
        /**
         * 开始终端检测并记录操作日志
         *
         * @param optLogRecorder
         * @param terminalId
         * @throws BusinessException
         */
        private BatchTaskItemResult startDetectAddOptLog(UUID cbbTerminalId)
                throws BusinessException {
            CbbTerminalDetectRequest detectReq = new CbbTerminalDetectRequest(cbbTerminalId);
            return null;
//            try {
//                operatorAPI.detect(detectReq);
//                // 添加检测成功日志
//                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_SUCCESS_LOG, terminalId);
//            } catch (BusinessException ex) {
//                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_FAIL_LOG, terminalId, ex.getI18nMessage());
//            }
        }

    }
}
