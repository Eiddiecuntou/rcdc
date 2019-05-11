package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalDetectPageWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.StartBatDetectWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.TerminalDetectListContentVO;
import com.ruijie.rcos.sk.base.batch.AbstractBatchTaskHandler;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemStatus;
import com.ruijie.rcos.sk.base.batch.BatchTaskSubmitResult;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItem;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItemResult;
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
     * 发起终端检测
     *
     * @param request 请求参数
     * @param optLogRecorder 日志记录对象
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "start")
    public DefaultWebResponse startDetect(StartBatDetectWebRequest request, ProgrammaticOptLogRecorder optLogRecorder) {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");

        String[] terminalIdArr = request.getIdArr();

        for (String terminalId : terminalIdArr) {
            startSingleDetect(terminalId, optLogRecorder);
        }

        return DefaultWebResponse.Builder.success();
    }

    private void startSingleDetect(String terminalId, ProgrammaticOptLogRecorder optLogRecorder) {
        try {
            CbbTerminalDetectRequest detectReq = new CbbTerminalDetectRequest(terminalId);
            operatorAPI.detect(detectReq);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_SUCCESS_LOG, terminalId);
        } catch (BusinessException ex) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_FAIL_LOG, terminalId, ex.getI18nMessage());
        }
    }

    /**
     * 获取终端检测分页列表
     *
     * @param request 请求参数
     * @return 检测分页列表
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "list")
    public DefaultWebResponse list(TerminalDetectPageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalDetectPageRequest pageRequest = buildCbbTerminalDetectPageRequest(request);
        DefaultPageResponse<CbbTerminalDetectDTO> listDetectResp = operatorAPI.listDetect(pageRequest);

        CbbTerminalDetectResultRequest resultRequest = new CbbTerminalDetectResultRequest();
        resultRequest.setDetectDate(CbbDetectDateEnums.TODAY);
        CbbDetectResultResponse detectResultResp = operatorAPI.getDetectResult(resultRequest);

        TerminalDetectListContentVO contentVO = new TerminalDetectListContentVO();
        contentVO.setItemArr(listDetectResp.getItemArr());
        contentVO.setTotal(listDetectResp.getTotal());
        contentVO.setResult(detectResultResp.getResult());

        return DefaultWebResponse.Builder.success(contentVO);
    }

    private CbbTerminalDetectPageRequest buildCbbTerminalDetectPageRequest(TerminalDetectPageWebRequest request) {
        CbbTerminalDetectPageRequest pageRequest = new CbbTerminalDetectPageRequest();
        pageRequest.setLimit(request.getLimit());
        pageRequest.setPage(request.getPage());
        pageRequest.setStartTime(request.getStartTime());
        pageRequest.setEndTime(request.getEndTime());
        return pageRequest;
    }

    /**
     * 
     * Description: 终端检测批量任务handler
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年1月17日
     * 
     * @author nt
     */
    protected class StartDetectBatchtaskHandler extends AbstractBatchTaskHandler {

        private final ProgrammaticOptLogRecorder optLogRecorder;

        private final CbbTerminalOperatorAPI operatorAPI;

        private Map<UUID, String> idMap;

        protected StartDetectBatchtaskHandler(Map<UUID, String> idMap, CbbTerminalOperatorAPI operatorAPI, Iterator<? extends BatchTaskItem> iterator,
                ProgrammaticOptLogRecorder optLogRecorder) {
            super(iterator);
            this.idMap = idMap;
            this.operatorAPI = operatorAPI;
            this.optLogRecorder = optLogRecorder;
        }

        @Override
        public BatchTaskFinishResult onFinish(int successCount, int failCount) {
            return buildDefaultFinishResult(successCount, failCount, BusinessKey.RCDC_TERMINAL_DETECT_BATCH_TASK_RESULT);
        }

        @Override
        public BatchTaskItemResult processItem(BatchTaskItem taskItem) throws BusinessException {
            Assert.notNull(taskItem, "taskItem can not be null");
            String terminalId = idMap.get(taskItem.getItemID());
            try {
                CbbTerminalDetectRequest detectReq = new CbbTerminalDetectRequest(terminalId);
                operatorAPI.detect(detectReq);
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_SUCCESS_LOG, terminalId);
                return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.SUCCESS)
                        .msgKey(BusinessKey.RCDC_TERMINAL_START_DETECT_SUCCESS_LOG).msgArgs(new String[] {terminalId}).build();
            } catch (BusinessException ex) {
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_FAIL_LOG, terminalId, ex.getI18nMessage());
                return DefaultBatchTaskItemResult.builder().batchTaskItemStatus(BatchTaskItemStatus.FAILURE)
                        .msgKey(BusinessKey.RCDC_TERMINAL_START_DETECT_FAIL_LOG).msgArgs(new String[] {terminalId, ex.getI18nMessage()}).build();
            }

        }
    }
}
