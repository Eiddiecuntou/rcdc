package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBatDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
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
    private CollectLogCacheManager collectLogCacheManager;

    @Injectable
    private TerminalDetectService detectService;

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;
    
    @Injectable
    private CbbTerminalBasicInfoAPI basicInfoAPI;

    /**
     * 测试关机
     * @throws BusinessException 业务异常
     */
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

    /**
     * 测试重启
     * 
     * @throws BusinessException 业务异常
     */
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

    /**
     * 测试收集日志
     * @throws BusinessException 业务异常
     */
    @Test
    public void testCollectLog() throws BusinessException {
        try {
            String terminalId = "123";
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.collectLog(request);
        } catch (Exception e) {
            fail();
        }
        new Verifications() {
            {
                operatorService.collectLog(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试批量检测
     * @throws BusinessException 业务异常
     */
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

    /**
     * 测试获取终端检测列表
     * @param resolver mock LocaleI18nResolver
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListDetect(@Mocked LocaleI18nResolver resolver) throws BusinessException {
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
        List<TerminalDetectionEntity> detectionList = new ArrayList<>();
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setDetectState(DetectStateEnums.CHECKING);
        detectionList.add(entity);
        return new PageImpl<>(detectionList, PageRequest.of(1, 10), 1);
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
    
    /**
     * 测试changePassword，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testChangePasswordArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.changePassword(null), "CbbChangePasswordRequest不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试changePassword，
     * @throws Exception 异常
     */
    @Test
    public void testChangePassword() throws Exception {
        CbbChangePasswordRequest request = new CbbChangePasswordRequest();
        request.setPassword("password");
        DefaultResponse response = terminalOperatorAPI.changePassword(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                operatorService.changePassword(request.getPassword());
                times = 1;
            }
        };
    }
    
    /**
     * 测试detect，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testDetectArgumentIsNull() throws Exception {
        CbbTerminalDetectRequest request = null;
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.detect(request), "CbbTerminalIdRequest不能为空");
        assertTrue(true);
    }
    
    /**
     * 测试detect，
     * @throws BusinessException 异常
     */
    @Test
    public void testDetect() throws BusinessException {
        CbbTerminalDetectRequest request = new CbbTerminalDetectRequest("123");
        DefaultResponse response = terminalOperatorAPI.detect(request);
        assertEquals(Status.SUCCESS, response.getStatus());
        new Verifications() {
            {
                operatorService.detect("123");
                times = 1;
            }
        };
    }
    
    /**
     * 测试getRecentDetect，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetRecentDetectArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getRecentDetect(null), "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试getRecentDetect，
     * @throws Exception 异常
     */
    @Test
    public void testGetRecentDetect() throws Exception {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        CbbTerminalDetectDTO detectInfo = new CbbTerminalDetectDTO();
        new Expectations() {
            {
                detectService.getRecentDetect(request.getTerminalId());
                result = detectInfo;
            }
        };
        CbbDetectInfoResponse response = terminalOperatorAPI.getRecentDetect(request);
        assertEquals(detectInfo, response.getDetectInfo());
    }
    
    /**
     * 测试getTerminalBaiscInfo，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetTerminalBaiscInfoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getTerminalBaiscInfo(null), "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试getTerminalBaiscInfo，
     * @throws Exception 异常
     */
    @Test
    public void testGetTerminalBaiscInfo() throws Exception {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        CbbTerminalBasicInfoResponse response = new CbbTerminalBasicInfoResponse();
        new Expectations() {
            {
                basicInfoAPI.findBasicInfoByTerminalId(request);
                result = response;
            }
        };
        assertEquals(response, terminalOperatorAPI.getTerminalBaiscInfo(request));
    }
    
    /**
     * 测试getCollectLog，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLogArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getCollectLog(null), "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试getCollectLog，
     * @throws Exception 异常
     */
    @Test
    public void testGetCollectLog() throws Exception {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("logFileName");
        cache.setState(CollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        CbbTerminalCollectLogStatusResponse response = terminalOperatorAPI.getCollectLog(request);
        assertEquals("logFileName", response.getLogName());
        assertEquals(CollectLogStateEnums.DONE, response.getState());
    }
    
    /**
     * 测试getCollectLog，CollectLogCache为空
     */
    @Test
    public void testGetCollectLogCollectLogCacheIsNull() {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = null;
            }
        };
        try {
            terminalOperatorAPI.getCollectLog(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试getTerminalLogFileInfo，参数为空
     * @throws Exception 异常
     */
    @Test
    public void testGetTerminalLogFileInfoArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.getTerminalLogFileInfo(null), "request can not be null");
        assertTrue(true);
    }
    
    /**
     * 测试getTerminalLogFileInfo，收集失败
     */
    @Test
    public void testGetTerminalLogFileInfoCollectFail() {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("logFileName");
        cache.setState(CollectLogStateEnums.DOING);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        try {
            terminalOperatorAPI.getTerminalLogFileInfo(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试getTerminalLogFileInfo，收集失败
     */
    @Test
    public void testGetTerminalLogFileInfoCollectFail1() {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("");
        cache.setState(CollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        try {
            terminalOperatorAPI.getTerminalLogFileInfo(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST, e.getKey());
        }
    }
    
    /**
     * 测试getTerminalLogFileInfo，日志文件没有后缀名
     * @throws BusinessException 异常
     */
    @Test
    public void testGetTerminalLogFileInfoNotHasSuffix() throws BusinessException {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("logFileName");
        cache.setState(CollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(request);
        assertEquals("/opt/ftp/terminal/log/logFileName", response.getLogFilePath());
        assertEquals("logFileName", response.getLogFileName());
        assertEquals("", response.getSuffix());
    }
    
    /**
     * 测试getTerminalLogFileInfo，日志文件有后缀名
     * @throws BusinessException 异常
     */
    @Test
    public void testGetTerminalLogFileInfoHasSuffix() throws BusinessException {
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        CollectLogCache cache = new CollectLogCache();
        cache.setLogFileName("logFileName.log");
        cache.setState(CollectLogStateEnums.DONE);
        new Expectations() {
            {
                collectLogCacheManager.getCache("123");
                result = cache;
            }
        };
        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(request);
        assertEquals("/opt/ftp/terminal/log/logFileName.log", response.getLogFilePath());
        assertEquals("logFileName.log", response.getLogFileName());
        assertEquals("log", response.getSuffix());
    }
}
