package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNetworkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbModifyTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Injectable
    private TerminalGroupService terminalGroupService;

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
            terminalBasicInfoAPI.findBasicInfoByTerminalId(terminalId);
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
            CbbTerminalBasicInfoResponse dto = terminalBasicInfoAPI.findBasicInfoByTerminalId(terminalId);
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

        try {
            terminalBasicInfoAPI.delete("123");
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
            terminalBasicInfoAPI.delete("123");
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

        try {
            terminalBasicInfoAPI.delete("123");
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
            CbbModifyTerminalRequest request = new CbbModifyTerminalRequest();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalBasicInfoAPI.modifyTerminal(request);
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
            CbbModifyTerminalRequest request = new CbbModifyTerminalRequest();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalBasicInfoAPI.modifyTerminal(request);
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
            CbbModifyTerminalRequest request = new CbbModifyTerminalRequest();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalBasicInfoAPI.modifyTerminal(request);
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


        CbbModifyTerminalRequest request = new CbbModifyTerminalRequest();
        request.setCbbTerminalId("123");
        request.setGroupId(id);
        request.setTerminalName("testName");
        terminalBasicInfoAPI.modifyTerminal(request);

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
            CbbModifyTerminalRequest request = new CbbModifyTerminalRequest();
            request.setCbbTerminalId("123");
            request.setGroupId(UUID.randomUUID());
            request.setTerminalName("123");
            terminalBasicInfoAPI.modifyTerminal(request);
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


}
