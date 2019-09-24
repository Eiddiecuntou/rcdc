package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;

import mockit.*;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/9/15
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class CbbTerminalDetectRecordAPIImplTest {

    @Tested
    private CbbTerminalDetectRecordAPIImpl api;

    @Injectable
    private TerminalDetectService detectService;

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    /**
     * 获取检测列表-结果为空
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListDetectResultEmpty() throws BusinessException {

        Page page = new PageImpl<TerminalDetectionEntity>(Lists.newArrayList());
        new Expectations() {
            {
                detectService.pageQuery((CbbTerminalDetectPageRequest) any);
                result = page;

            }
        };

        DefaultPageResponse<CbbTerminalDetectDTO> response = api.listDetect(new CbbTerminalDetectPageRequest());
        assertEquals(0, response.getItemArr().length);
    }

    /**
     * 获取检测列表
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testListDetect() throws BusinessException {
        List<TerminalDetectionEntity> list = Lists.newArrayList();
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        entity.setId(UUID.randomUUID());
        entity.setTerminalId("123");
        entity.setDetectState(DetectStateEnums.SUCCESS);
        list.add(entity);
        Page page = new PageImpl<TerminalDetectionEntity>(list, new PageRequest(0, 10), 1);

        new Expectations() {
            {
                detectService.pageQuery((CbbTerminalDetectPageRequest) any);
                result = page;

            }
        };

        new MockUp<LocaleI18nResolver>() {

            @Mock
            public String resolve(String key, String... args) {
                return "123";
            }
        };

        DefaultPageResponse<CbbTerminalDetectDTO> response = api.listDetect(new CbbTerminalDetectPageRequest());
        assertEquals(1, response.getItemArr().length);
        assertEquals(entity.getTerminalId(), response.getItemArr()[0].getTerminalId());
        assertEquals(entity.getDetectState().name(), response.getItemArr()[0].getCheckState().getState());
    }

    /**
     * 测试获取最近检测记录
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetRecentDetect() throws BusinessException {
        CbbTerminalDetectDTO detectInfo = new CbbTerminalDetectDTO();
        detectInfo.setTerminalId("123456");
        new Expectations() {
            {
                detectService.getRecentDetect(anyString);
                result = detectInfo;
            }
        };

        CbbDetectInfoResponse response = api.getRecentDetect(new CbbTerminalIdRequest("123"));
        assertEquals(detectInfo, response.getDetectInfo());
    }

    /**
     * 测试获取最近检测记录
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testGetDetectResult() throws BusinessException {
        CbbTerminalDetectStatisticsDTO dto = new CbbTerminalDetectStatisticsDTO();
        new Expectations() {
            {
                detectService.getDetectResult((CbbDetectDateEnums) any);
                result = dto;
            }
        };

        CbbDetectResultResponse response = api.getDetectResult(new CbbTerminalDetectResultRequest());
        assertEquals(dto, response.getResult());
    }
}
