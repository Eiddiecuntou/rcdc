package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBatDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CbbTerminalOperatorAPIImplTest {

    @Tested
    private CbbTerminalOperatorAPIImpl terminalOperatorAPI;

    @Injectable
    private TerminalOperatorService operatorService;

    @Injectable
    private GatherLogCacheManager gatherLogCacheManager;

    @Injectable
    private TerminalDetectService detectService;

    @Test
    public void testShutdown() throws BusinessException {

        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.shutdown(request);
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                operatorService.shutdown(anyString);
                times = 1;
            }
        };

    }

    @Test
    public void testRestart() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.shutdown(request);
            terminalOperatorAPI.restart(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.restart(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testChangePassword() throws BusinessException {
        try {
            String terminalId = "123";
            String password = "adf";
            CbbChangePasswordRequest request = new CbbChangePasswordRequest();
            request.setTerminalId(terminalId);
            request.setPassword(password);
            terminalOperatorAPI.changePassword(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.changePassword(anyString, anyString);
                times = 1;
            }
        };

    }

    @Test
    public void testGatherLog() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.gatherLog(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.gatherLog(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testDetect() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalDetectRequest request = new CbbTerminalDetectRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.detect(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.detect(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testDetectForArr() throws BusinessException {
        String[] terminalIdArr = {"1", "2", "3"};

        try {
            CbbTerminalBatDetectRequest request = new CbbTerminalBatDetectRequest();
            request.setTerminalIdArr(terminalIdArr);
            DefaultResponse resp = terminalOperatorAPI.detect(request);
            Assert.assertEquals(Status.SUCCESS, resp.getStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetTerminalLogNameIsNull() {
        new Expectations() {
            {
                gatherLogCacheManager.getCache(anyString);
                result = null;
            }
        };
        String terminalId = "123";
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            terminalOperatorAPI.getTerminalLogName(request);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
    }

    @Test
    public void testGetTerminalLogNameStateIsFailure() {
        GatherLogCache cache = new GatherLogCache();
        cache.setState(GatherLogStateEnums.FAILURE);
        new Expectations() {
            {
                gatherLogCacheManager.getCache(anyString);
                result = cache;
            }
        };
        String terminalId = "123";
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            terminalOperatorAPI.getTerminalLogName(request);
        } catch (BusinessException e) {
            Assert.assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
    }

    @Test
    public void testGetTerminalLogName() {
        String logName = "shine.zip";
        GatherLogCache cache = new GatherLogCache();
        cache.setState(GatherLogStateEnums.DONE);
        cache.setLogFileName(logName);
        new Expectations() {
            {
                gatherLogCacheManager.getCache(anyString);
                result = cache;
            }
        };
        String terminalId = "123";
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            CbbTerminalNameResponse result = terminalOperatorAPI.getTerminalLogName(request);
            Assert.assertEquals(result.getTerminalName(), logName);

        } catch (BusinessException e) {
            fail();
        }
    }

    /**
     * 测试获取终端检测列表
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListDetect() throws BusinessException {
        CbbTerminalDetectPageRequest pageReq = new CbbTerminalDetectPageRequest();
        pageReq.setDate(CbbDetectDateEnums.TODAY);
        pageReq.setLimit(10);
        pageReq.setPage(1);

        Page<TerminalDetectionEntity> page = buildRespPage();

        new Expectations() {
            {
                detectService.pageQuery((CbbTerminalDetectPageRequest) any);
                result = page;
            }
        };

        DefaultPageResponse<CbbTerminalDetectDTO> resp = terminalOperatorAPI.listDetect(pageReq);
        Assert.assertEquals(Status.SUCCESS, resp.getStatus());
        Assert.assertEquals(1, resp.getItemArr().length);

    }

    /**
     * 测试获取终端检测列表-返回结果中列表数量为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListDetectResultListIsEmpty() throws BusinessException {
        CbbTerminalDetectPageRequest pageReq = new CbbTerminalDetectPageRequest();
        pageReq.setDate(CbbDetectDateEnums.TODAY);
        pageReq.setLimit(10);
        pageReq.setPage(1);

        Page<TerminalDetectionEntity> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 10), 1);

        new Expectations() {
            {
                detectService.pageQuery((CbbTerminalDetectPageRequest) any);
                result = page;
            }
        };

        DefaultPageResponse<CbbTerminalDetectDTO> resp = terminalOperatorAPI.listDetect(pageReq);
        Assert.assertEquals(Status.SUCCESS, resp.getStatus());
        Assert.assertEquals(0, resp.getItemArr().length);

    }

    /**
     * 测试获取终端检测列表-请求为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListDetectRequestIsNull() throws BusinessException {
        CbbTerminalDetectPageRequest pageReq = null;

        try {
            terminalOperatorAPI.listDetect(pageReq);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("request can not be null", e.getMessage());
        }

    }

    private Page<TerminalDetectionEntity> buildRespPage() {
        List<TerminalDetectionEntity> list = new ArrayList<>();
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setDetectState(DetectStateEnums.CHECKING);
        entity.setTerminal(new TerminalEntity());
        list.add(entity);
        return new PageImpl<>(list, PageRequest.of(1, 10), 1);
    }

    /**
     * 测试获取终端检测结果
     */
    @Test
    public void testGetDetectResult() {
        CbbTerminalDetectResultRequest request = new CbbTerminalDetectResultRequest();
        request.setDetectDate(CbbDetectDateEnums.TODAY);
        
        CbbDetectResultResponse detectResult = terminalOperatorAPI.getDetectResult(request);
        Assert.assertEquals(Status.SUCCESS, detectResult.getStatus());
    }

    /**
     * 测试获取终端检测结果-请求参数为空
     */
    @Test
    public void testGetDetectResultRequestIsNull() {
        CbbTerminalDetectResultRequest request = null;
        
        try {
            terminalOperatorAPI.getDetectResult(request);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("request can not be null", e.getMessage());
        }
    }
}
