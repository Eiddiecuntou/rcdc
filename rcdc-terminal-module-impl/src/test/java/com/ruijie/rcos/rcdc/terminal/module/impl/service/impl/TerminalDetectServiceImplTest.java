package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.*;

import java.util.*;

import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectItemStateEnums;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectStatisticsDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbDetectDateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalDetectResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectSpecification;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;

import mockit.*;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class TerminalDetectServiceImplTest {

    @Tested
    private TerminalDetectServiceImpl detectService;

    @Injectable
    private TerminalDetectionDAO detectionDAO;

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    /**
     * 测试检测信息,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testUpdateBasicInfoAndDetectArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.updateTerminalDetect("", new TerminalDetectResult()),
                "terminalId不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.updateTerminalDetect("123", null), "TerminalDetectResult不能为null");
        assertTrue(true);
    }

    /**
     * 测试检测信息,entityList为空
     */
    @Test
    public void testUpdateBasicInfoAndDetectEntityListIsEmpty() {
        String terminalId = "123";
        TerminalDetectResult result = new TerminalDetectResult();
        new Expectations() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                result = Collections.emptyList();
            }
        };
        detectService.updateTerminalDetect(terminalId, result);

        new Verifications() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                times = 1;
                detectionDAO.save((TerminalDetectionEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试检测信息,
     */
    @Test
    public void testUpdateBasicInfoAndDetect() {
        String terminalId = "123";
        TerminalDetectResult result = new TerminalDetectResult();
        result.setBandwidth(12.3);
        result.setAccessInternet(1);
        result.setIpConflict(1);
        result.setIpConflictMac("123");
        result.setDelay(999.0);
        result.setPacketLossRate(100.0);
        List<TerminalDetectionEntity> entityList = new ArrayList<>();
        entityList.add(new TerminalDetectionEntity());
        new Expectations() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                result = entityList;
            }
        };
        detectService.updateTerminalDetect(terminalId, result);

        new Verifications() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                times = 1;
                TerminalDetectionEntity entity;
                detectionDAO.save(entity = withCapture());
                times = 1;
                assertEquals(12.3, entity.getBandwidth().doubleValue(), 0.1);
                assertEquals(1, entity.getAccessInternet().intValue());
                assertEquals(1, entity.getIpConflict().intValue());
                assertEquals("123", entity.getIpConflictMac());
                assertEquals(999, entity.getNetworkDelay().intValue());
                assertEquals(100.0, entity.getPacketLossRate().doubleValue(), 0.1);
                assertEquals(DetectStateEnums.SUCCESS, entity.getDetectState());
            }
        };
    }

    /**
     * 测试detectFailure,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDetectFailureArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.detectFailure(""), "terminalId不能为空");
        assertTrue(true);
    }

    /**
     * 测试detectFailure,entityList为空
     */
    @Test
    public void testDetectFailureEntityListIsEmpty() {
        String terminalId = "123";
        new Expectations() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                result = null;
            }
        };
        detectService.detectFailure(terminalId);
        new Verifications() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                times = 1;
                detectionDAO.save((TerminalDetectionEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试detectFailure,entityList为空
     */
    @Test
    public void testDetectFailure() {
        String terminalId = "123";
        List<TerminalDetectionEntity> entityList = new ArrayList<>();
        entityList.add(new TerminalDetectionEntity());
        new Expectations() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                result = entityList;
            }
        };
        detectService.detectFailure(terminalId);
        new Verifications() {
            {
                detectionDAO.findByTerminalIdAndDetectState(terminalId, DetectStateEnums.CHECKING);
                times = 1;
                TerminalDetectionEntity entity;
                detectionDAO.save(entity = withCapture());
                times = 1;
                assertEquals(DetectStateEnums.ERROR, entity.getDetectState());
                assertEquals("检测失败", entity.getDetectFailMsg());
            }
        };
    }

    /**
     * 测试save,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testSaveArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.save(""), "terminalId can not be null");
        assertTrue(true);
    }

    /**
     * 测试save,
     */
    @Test
    public void testSave() {
        String terminalId = "123";
        detectService.save(terminalId);
        new Verifications() {
            {
                TerminalDetectionEntity entity;
                detectionDAO.save(entity = withCapture());
                times = 1;
                assertEquals(terminalId, entity.getTerminalId());
                assertEquals(DetectStateEnums.WAIT, entity.getDetectState());
            }
        };
    }

    /**
     * 测试delete,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDeleteArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.delete(null), "terminal detection id can not be null");
        assertTrue(true);
    }

    /**
     * 测试delete,
     */
    @Test
    public void testDelete() {
        UUID uuid = UUID.randomUUID();
        detectService.delete(uuid);
        new Verifications() {
            {
                detectionDAO.deleteById(uuid);
                times = 1;
            }
        };
    }

    /**
     * 测试findInCurrentDate,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testFindInCurrentDateArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.findInCurrentDate(""), "terminal id can not be blank");
        assertTrue(true);
    }

    /**
     * 测试findInCurrentDate,detectionList为空
     */
    @Test
    public void testFindInCurrentDateDetectionListIsEmpty() {
        String terminalId = "123";
        new Expectations() {
            {
                detectionDAO.findByTerminalIdAndCreateTimeBetween(terminalId, (Date) any, (Date) any);
                result = Collections.emptyList();
            }
        };
        assertNull(detectService.findInCurrentDate(terminalId));
    }

    /**
     * 测试findInCurrentDate,
     */
    @Test
    public void testFindInCurrentDate() {
        String terminalId = "123";
        List<TerminalDetectionEntity> detectionList = new ArrayList<>();
        TerminalDetectionEntity entity = new TerminalDetectionEntity();
        detectionList.add(entity);
        detectionList.add(new TerminalDetectionEntity());
        new Expectations() {
            {
                detectionDAO.findByTerminalIdAndCreateTimeBetween(terminalId, (Date) any, (Date) any);
                result = detectionList;
            }
        };
        assertEquals(entity, detectService.findInCurrentDate(terminalId));
    }

    /**
     * 测试pageQuery,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testPageQueryArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.pageQuery(null), "request can not be null");
        assertTrue(true);
    }

    /**
     * 测试pageQuery,
     * 
     * @param page mock page
     */
    @Test
    public void testPageQuery(@Mocked Page<TerminalDetectionEntity> page) {
        CbbTerminalDetectPageRequest request = new CbbTerminalDetectPageRequest();

        new Expectations() {
            {
                detectionDAO.findAll((TerminalDetectSpecification) any, (Pageable) any);
                result = page;
            }
        };
        assertEquals(page, detectService.pageQuery(request));
    }

    /**
     * 测试getDetectResult,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetDetectResultArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.getDetectResult(null), "detect date can not be null");
        assertTrue(true);
    }

    /**
     * 测试getDetectResult,Today
     */
    @Test
    public void testGetDetectResultToday() {
        List<TerminalDetectionEntity> detectList = new ArrayList<>();
        TerminalDetectionEntity detectionEntity = new TerminalDetectionEntity();
        detectionEntity.setDetectState(DetectStateEnums.SUCCESS);
        detectionEntity.setAccessInternet(1);
        detectionEntity.setBandwidth(50.0);
        detectionEntity.setPacketLossRate(0.0);
        detectionEntity.setIpConflict(0);
        detectionEntity.setNetworkDelay(2.0);


        detectList.add(detectionEntity);
        new Expectations() {
            {
                detectionDAO.findByCreateTimeBetween((Date) any, (Date) any);
                result = detectList;
            }
        };
        CbbTerminalDetectStatisticsDTO result = detectService.getDetectResult(CbbDetectDateEnums.TODAY);
        assertEquals(0, result.getAccessInternet());
        assertEquals(0, result.getBandwidth());
        assertEquals(0, result.getIpConflict());
        assertEquals(0, result.getPacketLossRate());
        assertEquals(0, result.getDelay());
        assertEquals(0, result.getChecking());
        assertEquals(0, result.getAll());
    }


    /**
     * 测试getDetectResult,Today,异常列表为空
     */
    @Test
    public void testGetDetectResultTodayAbnormalListIsEmpty() {
        List<TerminalDetectionEntity> detectList = new ArrayList<>();

        new Expectations() {
            {
                detectionDAO.findByCreateTimeBetween((Date) any, (Date) any);
                result = detectList;
            }
        };
        CbbTerminalDetectStatisticsDTO result = detectService.getDetectResult(CbbDetectDateEnums.TODAY);
        assertEquals(0, result.getAccessInternet());
        assertEquals(0, result.getBandwidth());
        assertEquals(0, result.getIpConflict());
        assertEquals(0, result.getPacketLossRate());
        assertEquals(0, result.getDelay());
        assertEquals(0, result.getChecking());
        assertEquals(0, result.getAll());
    }

    /**
     * 测试getDetectResult,异常总数
     */
    @Test
    public void testGetDetectResultAbnormal() {
        List<TerminalDetectionEntity> detectList = new ArrayList<>();
        // ip冲突
        TerminalDetectionEntity detectionEntity = new TerminalDetectionEntity();
        detectionEntity.setIpConflict(1);

        // 带宽检测异常
        TerminalDetectionEntity detectionEntity1 = new TerminalDetectionEntity();
        detectionEntity1.setDetectState(DetectStateEnums.SUCCESS);
        detectionEntity1.setAccessInternet(DetectItemStateEnums.TRUE.getState());
        detectionEntity1.setBandwidth(15.0);
        detectionEntity1.setIpConflict(null);

        // 外网连通异常
        TerminalDetectionEntity detectionEntity2 = new TerminalDetectionEntity();
        detectionEntity2.setDetectState(DetectStateEnums.SUCCESS);
        detectionEntity2.setAccessInternet(0);
        detectionEntity2.setPacketLossRate(-1.0);
        detectionEntity2.setNetworkDelay(-1.0);
        detectionEntity2.setBandwidth(50.0);
        detectionEntity2.setIpConflict(Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE);

        // 丢包异常
        TerminalDetectionEntity detectionEntity3 = new TerminalDetectionEntity();
        detectionEntity3.setDetectState(DetectStateEnums.SUCCESS);
        detectionEntity3.setAccessInternet(Constants.TERMINAL_DETECT_ABNORMAL_COMMON_CODE);
        detectionEntity3.setBandwidth(-1.0);
        detectionEntity3.setPacketLossRate(11.0);
        detectionEntity3.setIpConflict(0);
        detectionEntity3.setNetworkDelay(10.0);

        // 时延异常
        TerminalDetectionEntity detectionEntity4 = new TerminalDetectionEntity();
        detectionEntity4.setDetectState(DetectStateEnums.SUCCESS);
        detectionEntity4.setAccessInternet(1);
        detectionEntity4.setBandwidth(50.0);
        detectionEntity4.setPacketLossRate(0.0);
        detectionEntity4.setIpConflict(0);
        detectionEntity4.setNetworkDelay(20.0);

        detectList.add(detectionEntity);
        detectList.add(detectionEntity1);
        detectList.add(detectionEntity2);
        detectList.add(detectionEntity3);
        detectList.add(detectionEntity4);
        new Expectations() {
            {
                detectionDAO.findByCreateTimeBetween((Date) any, (Date) any);
                result = detectList;
            }
        };
        CbbTerminalDetectStatisticsDTO result = detectService.getDetectResult(CbbDetectDateEnums.YESTERDAY);
        assertEquals(3, result.getAccessInternet());
        assertEquals(3, result.getBandwidth());
        assertEquals(1, result.getIpConflict());
        assertEquals(4, result.getPacketLossRate());
        assertEquals(4, result.getDelay());
        assertEquals(0, result.getChecking());
        assertEquals(5, result.getAll());
    }

    /**
     * 测试getRecentDetect,参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testGetRecentDetectArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> detectService.getRecentDetect(""), "terminalId can not be null");
        assertTrue(true);
    }

    /**
     * 测试getRecentDetect,TerminalDetectionEntity为空
     */
    @Test
    public void testGetRecentDetectTerminalDetectionEntityIsNull() {
        String terminalId = "123";
        new Expectations() {
            {
                detectionDAO.findFirstByTerminalIdOrderByDetectTimeDesc(terminalId);
                result = null;
            }
        };
        assertNull(detectService.getRecentDetect(terminalId));
    }

    /**
     * 测试getRecentDetect,
     * 
     * @param resolver mock LocaleI18nResolver
     */
    @Test
    public void testGetRecentDetect(@Mocked LocaleI18nResolver resolver) {
        String terminalId = "123";
        TerminalDetectionEntity recentDetect = new TerminalDetectionEntity();
        recentDetect.setTerminalId(terminalId);
        recentDetect.setDetectState(DetectStateEnums.SUCCESS);
        new Expectations() {
            {
                detectionDAO.findFirstByTerminalIdOrderByDetectTimeDesc(terminalId);
                result = recentDetect;
            }
        };
        CbbTerminalDetectDTO cbbTerminalDetectDTO = detectService.getRecentDetect(terminalId);
        assertEquals(terminalId, cbbTerminalDetectDTO.getTerminalId());
    }
}
