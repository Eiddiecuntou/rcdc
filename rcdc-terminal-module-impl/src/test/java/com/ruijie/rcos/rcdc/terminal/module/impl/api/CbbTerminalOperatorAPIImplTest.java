package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalStartMode;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.*;

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
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalBasicInfoServiceTx terminalBasicInfoServiceTx;

    @Injectable
    private TerminalGroupService terminalGroupService;

    @Injectable
    private TerminalLicenseService terminalLicenseService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalAuthHelper terminalAuthHelper;

    /**
     * 测试查询终端管理密码
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testQueryPassword() throws BusinessException {

        final String terminalPassword = "terminalPassword";
        new Expectations() {
            {
                operatorService.getTerminalPassword();
                result = terminalPassword;
            }
        };
        assertEquals(terminalPassword, terminalOperatorAPI.queryPassword());
    }

    /**
     * 查找不到数据
     */
    @Test
    public void testFindBasicInfoByTerminalIdNotFindBasicInfo() {
        String terminalId = "123";
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = null;
            }
        };

        try {
            terminalOperatorAPI.findBasicInfoByTerminalId(terminalId);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }

        try {
            new Verifications() {
                {
                    String tid;
                    basicInfoDAO.findTerminalEntityByTerminalId(tid = withCapture());
                    times = 1;
                    Assert.assertEquals(tid, terminalId);
                }
            };
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试返回结果
     */
    @Test
    public void testFindBasicInfoByTerminalIdReturnValue() {
        String terminalId = "123";
        String name = "t-box01";
        Date now = new Date();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId(terminalId);
        entity.setTerminalName(name);
        entity.setGetIpMode(CbbGetNetworkModeEnums.AUTO);
        entity.setCreateTime(now);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;
            }
        };

        try {
            CbbTerminalBasicInfoDTO dto = terminalOperatorAPI.findBasicInfoByTerminalId(terminalId);
            assertEquals(dto.getTerminalId(), terminalId);
            assertEquals(dto.getTerminalName(), name);
            assertEquals(dto.getCreateTime(), now);
            assertEquals(dto.getGetIpMode(), CbbGetNetworkModeEnums.AUTO);
        } catch (BusinessException e) {
            fail();
        }

        try {
            new Verifications() {
                {
                    basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                    times = 1;
                }
            };
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试返回结果
     */
    @Test
    public void testFindBasicInfoByTerminalIdReturnValue2() {
        String terminalId = "123";
        String name = "t-box01";
        Date now = new Date();
        TerminalEntity entity = new TerminalEntity();
        entity.setTerminalId(terminalId);
        entity.setTerminalName(name);
        entity.setGetIpMode(CbbGetNetworkModeEnums.AUTO);
        entity.setCreateTime(now);
        entity.setSupportWorkMode("[\"IDV\",\"VDI\",\"VOI\"]");
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = entity;
            }
        };

        try {
            CbbTerminalBasicInfoDTO dto = terminalOperatorAPI.findBasicInfoByTerminalId(terminalId);
            assertEquals(dto.getTerminalId(), terminalId);
            assertEquals(dto.getTerminalName(), name);
            assertEquals(dto.getCreateTime(), now);
            assertEquals(dto.getGetIpMode(), CbbGetNetworkModeEnums.AUTO);
            assertEquals(3, dto.getSupportWorkModeArr().length);
        } catch (BusinessException e) {
            fail();
        }

        try {
            new Verifications() {
                {
                    basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                    times = 1;
                }
            };
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试删除终端失败,在线终端
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDeleteFail() throws BusinessException {
        TerminalEntity entity = new TerminalEntity();
        entity.setVersion(1);
        new Expectations() {
            {
                basicInfoService.isTerminalOnline("123");
                result = true;
            }
        };

        try {
            terminalOperatorAPI.delete("123");
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_ONLINE_CANNOT_DELETE, e.getKey());
        }

        new Verifications() {
            {
                terminalBasicInfoServiceTx.deleteTerminal(anyString);
                times = 0;
            }
        };
    }

    /**
     * 测试删除终端
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDeleteSuccess() throws BusinessException {
        TerminalEntity entity = new TerminalEntity();
        entity.setVersion(1);
        new Expectations() {
            {
                terminalBasicInfoServiceTx.deleteTerminal(anyString);
            }
        };

        try {
            terminalOperatorAPI.delete("123");
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                terminalBasicInfoServiceTx.deleteTerminal(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试删除终端-数据不存在
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDeleteNoExistData() throws BusinessException {

        new MockUp<CbbTerminalOperatorAPIImpl>() {
            @Mock
            private Integer getVersion(String terminalId) throws BusinessException {
                return 1;
            }
        };

        try {
            terminalOperatorAPI.delete("123");
        } catch (Exception e) {
            fail();
        }

        new Verifications() {
            {
                terminalBasicInfoServiceTx.deleteTerminal(anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试修改终端名称成功
     */
    @Test
    public void testModifyTerminalNameSuccess() {
        new Expectations() {
            {
                try {
                    basicInfoService.modifyTerminalName(anyString, anyString);
                    basicInfoDAO.save((TerminalEntity) any);
                } catch (BusinessException e) {
                    fail();
                }
            }
        };

        try {
            CbbModifyTerminalDTO request = new CbbModifyTerminalDTO();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalOperatorAPI.modifyTerminal(request);
        } catch (BusinessException e) {
            fail();
        }

        modifyNameVerifications();
    }

    /**
     * 测试修改终端名称成功
     */
    @Test
    public void testModifyTerminalIdvModeIsNotNull() {
        new Expectations() {
            {
                try {
                    basicInfoService.modifyTerminalName(anyString, anyString);
                    basicInfoDAO.save((TerminalEntity) any);
                } catch (BusinessException e) {
                    fail();
                }
            }
        };

        try {
            CbbModifyTerminalDTO request = new CbbModifyTerminalDTO();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalOperatorAPI.modifyTerminal(request);
        } catch (BusinessException e) {
            fail();
        }

        modifyNameVerifications();
    }

    /**
     * 测试修改终端名称失败，TerminalEntity为空
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testModifyTerminalNameTerminalEntityIsNull() throws BusinessException {
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = null;
            }
        };
        try {
            CbbModifyTerminalDTO request = new CbbModifyTerminalDTO();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalOperatorAPI.modifyTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }
        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId("123");
                times = 1;
            }
        };
    }

    /**
     * 测试修改终端名称失败，ModifyTerminalName有BusinessException
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testModifyTerminalNameModifyTerminalNameHasBusinessException() throws BusinessException {
        TerminalEntity terminalEntity = new TerminalEntity();
        UUID id = UUID.randomUUID();
        terminalEntity.setId(id);
        terminalEntity.setTerminalName("testName");

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = terminalEntity;
            }
        };


        CbbModifyTerminalDTO request = new CbbModifyTerminalDTO();
        request.setCbbTerminalId("123");
        request.setGroupId(id);
        request.setTerminalName("testName");
        terminalOperatorAPI.modifyTerminal(request);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                times = 1;

                basicInfoDAO.save((TerminalEntity) any);
                times = 1;
            }
        };
    }

    /**
     * 测试终端名字无变更
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testModifyTerminalWithoutChange() throws BusinessException {
        new Expectations() {
            {
                basicInfoService.modifyTerminalName(anyString, anyString);
                result = new BusinessException("key");
            }
        };

        try {
            CbbModifyTerminalDTO request = new CbbModifyTerminalDTO();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalOperatorAPI.modifyTerminal(request);
            fail();
        } catch (BusinessException e) {
            assertEquals("key", e.getKey());
        }

        new Verifications() {
            {
                basicInfoService.modifyTerminalName(anyString, anyString);
                times = 1;
            }
        };
    }

    private void modifyNameVerifications() {
        new Verifications() {
            {
                try {
                    basicInfoDAO.save((TerminalEntity) any);
                    times = 1;

                } catch (Exception e) {
                    fail();
                }
            }
        };
    }


    private CbbTerminalNetworkInfoDTO[] getItemArr() {
        CbbTerminalNetworkInfoDTO[] itemArr = new CbbTerminalNetworkInfoDTO[1];
        CbbTerminalNetworkInfoDTO dto = new CbbTerminalNetworkInfoDTO();
        dto.setGateway("456");
        dto.setGetDnsMode(CbbGetNetworkModeEnums.AUTO);
        dto.setGetIpMode(CbbGetNetworkModeEnums.MANUAL);
        dto.setIp("789");
        dto.setMacAddr("abc");
        dto.setSsid("ssid");
        dto.setMainDns("mainDns");
        dto.setSecondDns("secondDns");
        dto.setNetworkAccessMode(CbbNetworkModeEnums.WIRED);
        dto.setSubnetMask("subnetMask");
        itemArr[0] = dto;

        return itemArr;
    }

    /**
     * 测试关机
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testShutdown() throws BusinessException {

        try {
            String terminalId = "123";

            terminalOperatorAPI.shutdown(terminalId);
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

            terminalOperatorAPI.shutdown(terminalId);
            terminalOperatorAPI.restart(terminalId);
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
     * 测试changePassword，参数为空
     *
     * @throws Exception 异常
     */
    @Test
    public void testChangePasswordArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.changePassword(null), "CbbChangePasswordRequest不能为空");
        assertTrue(true);
    }

    /**
     * 测试changePassword，
     *
     * @throws Exception 异常
     */
    @Test
    public void testChangePassword() throws Exception {
        CbbChangePasswordDTO request = new CbbChangePasswordDTO();
        request.setPassword("password123");
        terminalOperatorAPI.changePassword(request);
        new Verifications() {
            {
                operatorService.changePassword(request.getPassword());
                times = 1;
            }
        };
    }

    /**
     * 测试changePassword，
     *
     * @throws Exception 异常
     */
    @Test
    public void testChangePasswordError() throws Exception {
        CbbChangePasswordDTO request = new CbbChangePasswordDTO();
        request.setPassword("1");
        try {
            terminalOperatorAPI.changePassword(request);
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_ADMIN_PWD_ILLEGAL, e.getKey());
        }
    }

    /**
     * 测试清空数据盘
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testClearIdvTerminalDataDisk() throws BusinessException {
        terminalOperatorAPI.clearIdvTerminalDataDisk("123");
        new Verifications() {
            {
                operatorService.diskClear("123");
                times = 1;

            }
        };
    }


    /**
     * 测试IDV终端离线登录设置
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testIdvOfflineLoginSetting() throws BusinessException {
        CbbOfflineLoginSettingDTO request = new CbbOfflineLoginSettingDTO(0);
        terminalOperatorAPI.idvOfflineLoginSetting(request);
        new Verifications() {
            {
                operatorService.offlineLoginSetting(0);
                times = 1;
            }
        };
    }

    /**
     * 测试 relieveFault 方法入参
     *
     * @throws Exception
     */
    @Test
    public void testRelieveFaultValidateParams() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalOperatorAPI.relieveFault(null, null), "terminalId不能为空");
        assertTrue(true);
    }

    /**
     * 测试 relieveFault 方法
     */
    @Test
    public void testRelieveFault() {
        try {
            String terminalId = "123";

            terminalOperatorAPI.relieveFault(terminalId, null);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                try {
                    operatorService.relieveFault(anyString, null);
                    times = 1;
                } catch (BusinessException e) {
                    fail();
                }
            }
        };
    }


    /**
     * 测试queryOfflineLoginSetting
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testQueryOfflineLoginSetting() throws BusinessException {

        new Expectations() {
            {
                operatorService.queryOfflineLoginSetting();
                result = "0";
            }
        };

        Assert.assertEquals("0", terminalOperatorAPI.queryOfflineLoginSetting());
    }

    /**
     * 测试删除终端
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDeleteIDV() throws BusinessException {
        TerminalEntity entity = new TerminalEntity();
        entity.setVersion(1);
        entity.setAuthed(true);
        entity.setPlatform(CbbTerminalPlatformEnums.IDV);
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(anyString);
                result = entity;
            }
        };

        try {
            terminalOperatorAPI.delete("123");
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                terminalBasicInfoServiceTx.deleteTerminal(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testCloseTerminalConnection() {
        terminalOperatorAPI.closeTerminalConnection("123");
        new Verifications() {
            {
                sessionManager.getSessionByAlias("123");
                times = 1;
            }
        };
    }

    /**
     * testSetTerminalStartModeTerminalNotExist
     */
    @Test
    public void testSetTerminalStartModeTerminalNotExist() {
        String terminalId = "123";
        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = null;
            }
        };

        try {
            terminalOperatorAPI.setTerminalStartMode("123", CbbTerminalStartMode.AUTO);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }
    }

    /**
     * testSetTerminalStartModeTerminalNotExist
     */
    @Test
    public void testSetTerminalStartModeTerminalSaveException() {
        String terminalId = "123";
        TerminalEntity terminalEntity =  new TerminalEntity();

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = terminalEntity;

                basicInfoDAO.save(terminalEntity);
                result = new Exception();
            }
        };

        try {
            terminalOperatorAPI.setTerminalStartMode("123", CbbTerminalStartMode.AUTO);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SET_START_MODE_FAIL, e.getKey());
        }
    }

    /**
     * testSetTerminalStartMode
     */
    @Test
    public void testSetTerminalStartMode() throws BusinessException {
        String terminalId = "123";
        TerminalEntity terminalEntity =  new TerminalEntity();

        new Expectations() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                result = terminalEntity;

                basicInfoDAO.save(terminalEntity);
            }
        };


        terminalOperatorAPI.setTerminalStartMode("123", CbbTerminalStartMode.AUTO);

        new Verifications() {
            {
                basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
                times = 1;

                basicInfoDAO.save(terminalEntity);
                times = 1;
            }
        };

    }

    /**
     * 测试getTerminalList
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testgetTerminalList() throws BusinessException {
        terminalOperatorAPI.getTerminalList();
        List<String> terminalList = Arrays.asList();
        new Expectations() {
            {
                sessionManager.getOnlineTerminalId();
                result = terminalList;
            }
        };

        Assert.assertEquals(terminalList, terminalOperatorAPI.getTerminalList());
    }
}
