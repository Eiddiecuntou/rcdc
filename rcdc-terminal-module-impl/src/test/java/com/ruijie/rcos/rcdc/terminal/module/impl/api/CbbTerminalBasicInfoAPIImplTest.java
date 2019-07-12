package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Date;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNameRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/2
 *
 * @author Jarman
 */
@RunWith(JMockit.class)
public class CbbTerminalBasicInfoAPIImplTest {

    @Tested
    private CbbTerminalBasicInfoAPIImpl terminalBasicInfoAPI;

    @Injectable
    private TerminalBasicInfoDAO basicInfoDAO;

    @Injectable
    private TerminalBasicInfoService basicInfoService;

    @Injectable
    private TerminalBasicInfoServiceTx terminalBasicInfoServiceTx;

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
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            terminalBasicInfoAPI.findBasicInfoByTerminalId(request);
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
        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId(terminalId);
        try {
            CbbTerminalBasicInfoResponse dto = terminalBasicInfoAPI.findBasicInfoByTerminalId(request);
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

        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        try {
            terminalBasicInfoAPI.delete(request);
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

        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        try {
            terminalBasicInfoAPI.delete(request);
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

        new MockUp<CbbTerminalBasicInfoAPIImpl>() {
            @Mock
            private Integer getVersion(String terminalId) throws BusinessException {
                return 1;
            }
        };

        CbbTerminalIdRequest request = new CbbTerminalIdRequest();
        request.setTerminalId("123");
        try {
            terminalBasicInfoAPI.delete(request);
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
                    basicInfoDAO.modifyTerminalName(anyString, anyInt, anyString);
                    result = 1;
                } catch (BusinessException e) {
                    fail();
                }
            }
        };

        try {
            CbbTerminalNameRequest request = new CbbTerminalNameRequest();
            request.setTerminalId("123");
            terminalBasicInfoAPI.modifyTerminalName(request);
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
            CbbTerminalNameRequest request = new CbbTerminalNameRequest();
            request.setTerminalId("123");
            terminalBasicInfoAPI.modifyTerminalName(request);
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
        new Expectations() {
            {
                basicInfoService.modifyTerminalName(anyString, anyString);
                result = new BusinessException("key");
            }
        };

        try {
            CbbTerminalNameRequest request = new CbbTerminalNameRequest();
            request.setTerminalId("123");
            terminalBasicInfoAPI.modifyTerminalName(request);
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

    /**
     * 测试修改终端名称失败
     */
    @Test
    public void testModifyTerminalNameFail() {
        new Expectations() {
            {
                try {
                    basicInfoDAO.modifyTerminalName(anyString, anyInt, anyString);
                    result = 0;
                } catch (Exception e) {
                    fail();
                }
            }
        };

        try {
            CbbTerminalNameRequest request = new CbbTerminalNameRequest();
            request.setTerminalId("123");
            terminalBasicInfoAPI.modifyTerminalName(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(e.getKey(), BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        modifyNameVerifications();
    }

    private void modifyNameVerifications() {
        new Verifications() {
            {
                try {
                    basicInfoDAO.modifyTerminalName(anyString, anyInt, anyString);
                    times = 1;

                } catch (Exception e) {
                    fail();
                }
            }
        };
    }

    /**
     * 测试修改终端网络
     */
    @Test
    public void testModifyTerminalNetworkConfig() {
        new Expectations() {
            {
                try {
                    basicInfoService.modifyTerminalNetworkConfig(anyString, (ShineNetworkConfig) any);
                } catch (BusinessException e) {
                    fail();
                }
                basicInfoDAO.modifyTerminalNetworkConfig(anyString, anyInt, (CbbTerminalNetworkRequest) any);
                result = 1;
            }
        };
        CbbTerminalNetworkRequest request = new CbbTerminalNetworkRequest();
        String terminalId = "123";
        String gateway = "gateway";
        request.setTerminalId(terminalId);
        request.setGateway(gateway);
        request.setGetDnsMode(CbbGetNetworkModeEnums.AUTO);
        request.setGetIpMode(CbbGetNetworkModeEnums.MANUAL);
        try {

            terminalBasicInfoAPI.modifyTerminalNetworkConfig(request);
        } catch (BusinessException e) {
            fail();
        }

        new Verifications() {
            {
                try {
                    basicInfoService.modifyTerminalNetworkConfig(anyString, (ShineNetworkConfig) any);
                    ShineNetworkConfig shineNetworkConfig;
                    basicInfoService.modifyTerminalNetworkConfig(anyString, shineNetworkConfig = withCapture());
                    assertEquals(shineNetworkConfig.getTerminalId(), terminalId);
                    assertEquals(shineNetworkConfig.getGateway(), gateway);
                    assertEquals(shineNetworkConfig.getGetDnsMode(), (Integer) 0);
                    assertEquals(shineNetworkConfig.getGetIpMode(), (Integer) 1);

                } catch (BusinessException e) {
                    fail();
                }
                times = 1;
                basicInfoDAO.modifyTerminalNetworkConfig(anyString, anyInt, (CbbTerminalNetworkRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 测试修改终端网络，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testModifyTerminalNetworkConfigArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> terminalBasicInfoAPI.modifyTerminalNetworkConfig(null),
                "TerminalNetworkRequest不能为null");
        assertTrue(true);
    }

    /**
     * 测试修改终端网络失败
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testModifyTerminalNetworkConfigFail() throws BusinessException {
        new Expectations() {
            {
                basicInfoDAO.modifyTerminalNetworkConfig(anyString, anyInt, (CbbTerminalNetworkRequest) any);
                result = 0;
            }
        };
        CbbTerminalNetworkRequest request = new CbbTerminalNetworkRequest();
        String terminalId = "123";
        String gateway = "gateway";
        request.setTerminalId(terminalId);
        request.setGateway(gateway);
        request.setGetDnsMode(CbbGetNetworkModeEnums.AUTO);
        request.setGetIpMode(CbbGetNetworkModeEnums.MANUAL);
        try {

            terminalBasicInfoAPI.modifyTerminalNetworkConfig(request);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL, e.getKey());
        }
    }
}
