package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

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
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;

@Controller
@RequestMapping("/terminal/detect")
@EnableCustomValidate(enable = false)
public class TerminalDetectController {

    @Autowired
    private CbbTerminalOperatorAPI operatorAPI;

    /**
     * 批量开启终端检测
     * 
     * @param request 请求参数
     * @param optLogRecorder 日志记录
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "start")
    public DefaultWebResponse startDetect(StartBatDetectWebRequest request, ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");

        String[] idArr = request.getIdArr();
        for (String id : idArr) {
            startDetectAddOptLog(optLogRecorder, id);
        }
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 开始终端检测并记录操作日志
     * 
     * @param optLogRecorder
     * @param terminalId
     * @throws BusinessException
     */
    private void startDetectAddOptLog(ProgrammaticOptLogRecorder optLogRecorder, String terminalId) throws BusinessException {
        CbbTerminalDetectRequest detectReq = new CbbTerminalDetectRequest(terminalId);
        try {
            operatorAPI.detect(detectReq);
            // 添加检测成功日志
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_SUCCESS_LOG, terminalId);
        } catch (BusinessException ex) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_START_DETECT_FAIL_LOG, terminalId, ex.getI18nMessage());
            throw ex;
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

}
