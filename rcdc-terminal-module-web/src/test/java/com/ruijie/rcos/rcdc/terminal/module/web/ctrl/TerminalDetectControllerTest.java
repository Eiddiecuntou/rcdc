package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Map;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalDetectPageWebRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.StartBatDetectWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.TerminalDetectListContentVO;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.WebResponse.Status;
import com.ruijie.rcos.sk.webmvc.api.vo.ExactMatch;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月24日
 * 
 * @author ls
 */
@RunWith(JMockit.class)
public class TerminalDetectControllerTest {

    @Tested
    private TerminalDetectController controller;

    @Injectable
    private CbbTerminalOperatorAPI operatorAPI;

    /**
     * 测试startDetect,参数为空
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @throws Exception 异常
     */
    @Test
    public void testStartDetectArgumentIsNull(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder) throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.startDetect(null, optLogRecorder), "request can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.startDetect(new StartBatDetectWebRequest(), null),
                "optLogRecorder can not be null");
        assertTrue(true);
    }

    /**
     * 测试startDetect,
     * 
     * @param optLogRecorder mock日志记录对象
     * @param builder mock批量任务处理对象
     * @param utils mock TerminalIdMappingUtils
     * @throws BusinessException 异常
     */
    @Test
    public void testStartDetect(@Mocked ProgrammaticOptLogRecorder optLogRecorder, @Mocked BatchTaskBuilder builder,
            @Mocked TerminalIdMappingUtils utils) throws BusinessException {
        StartBatDetectWebRequest request = new StartBatDetectWebRequest();
        request.setIdArr(new String[] {"1111", "2222"});
        DefaultWebResponse response = controller.startDetect(request, optLogRecorder);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    /**
     * 测试list,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testListArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> controller.list(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试list,
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testList() throws BusinessException {
        TerminalDetectPageWebRequest request = new TerminalDetectPageWebRequest();
        ExactMatch[] exactMatchArr = new ExactMatch[1];
        ExactMatch exactMatch = new ExactMatch();
        String[] valueArr = new String[1];
        valueArr[0] = "TODAY";
        exactMatch.setValueArr(valueArr);
        exactMatchArr[0] = exactMatch;
        request.setExactMatchArr(exactMatchArr);

        DefaultPageResponse<CbbTerminalDetectDTO> listDetectResp = new DefaultPageResponse<>();
        CbbDetectResultResponse detectResultResp = new CbbDetectResultResponse();
        new Expectations() {
            {
                operatorAPI.listDetect((CbbTerminalDetectPageRequest) any);
                result = listDetectResp;
                operatorAPI.getDetectResult((CbbTerminalDetectResultRequest) any);
                result = detectResultResp;
            }
        };
        DefaultWebResponse response = controller.list(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        TerminalDetectListContentVO contentVO = (TerminalDetectListContentVO) response.getContent();
        assertArrayEquals(listDetectResp.getItemArr(), contentVO.getItemArr());
        assertEquals(listDetectResp.getTotal(), contentVO.getTotal());
        assertEquals(detectResultResp.getResult(), contentVO.getResult());
    }
}
