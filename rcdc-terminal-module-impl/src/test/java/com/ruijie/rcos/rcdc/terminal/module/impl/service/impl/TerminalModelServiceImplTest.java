package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalModelDriverDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/1/9
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class TerminalModelServiceImplTest {

    @Tested
    TerminalModelServiceImpl terminalModelService;

    @Injectable
    private TerminalModelDriverDAO terminalModelDriverDAO;

    @Injectable
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    private static final CbbTerminalPlatformEnums PLATFORM = CbbTerminalPlatformEnums.IDV;

    /**
     * 测试根据平台类型查询终端类型 - 列表为空
     *
     */
    @Test
    public void testQueryTerminalModelByPlatform() {
        CbbTerminalPlatformEnums[] platformArr = new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.APP};

        new Expectations() {
            {
                terminalModelDriverDAO.findByPlatformIn(platformArr);
                result = Lists.newArrayList();
            }
        };

        CbbTerminalModelDTO[] cbbTerminalModelDTOArr = terminalModelService.queryTerminalModelByPlatform(platformArr);

        assertEquals(0, cbbTerminalModelDTOArr.length);

        new Verifications() {
            {
                terminalModelDriverDAO.findByPlatformIn(platformArr);
                times = 1;
            }
        };
    }

    /**
     * 测试根据平台类型查询终端类型
     *
     */
    @Test
    public void testQueryTerminalModelByPlatformHasDuplicate() {
        CbbTerminalPlatformEnums[] platformArr = new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.VDI};

        TerminalModelDriverEntity modelDriverEntity = new TerminalModelDriverEntity();
        modelDriverEntity.setId(UUID.randomUUID());
        modelDriverEntity.setProductId("123");
        modelDriverEntity.setProductModel("456");
        modelDriverEntity.setCpuType("bbb");

        TerminalModelDriverEntity modelDriverEntity2 = new TerminalModelDriverEntity();
        modelDriverEntity2.setId(UUID.randomUUID());
        modelDriverEntity2.setProductId("123");
        modelDriverEntity2.setProductModel("456");

        TerminalModelDriverEntity modelDriverEntity3 = new TerminalModelDriverEntity();
        modelDriverEntity3.setId(UUID.randomUUID());
        modelDriverEntity3.setProductId(null);
        modelDriverEntity3.setProductModel("xxx");

        new Expectations() {
            {
                terminalModelDriverDAO.findByPlatformIn(platformArr);
                result = Lists.newArrayList(modelDriverEntity, modelDriverEntity2, modelDriverEntity3);
            }
        };

        CbbTerminalModelDTO[] cbbTerminalModelDTOArr = terminalModelService.queryTerminalModelByPlatform(platformArr);

        assertEquals(1, cbbTerminalModelDTOArr.length);

        CbbTerminalModelDTO expectDTO = new CbbTerminalModelDTO();
        expectDTO.setCpuType("bbb");
        expectDTO.setProductId("123");
        expectDTO.setProductModel("456");
        assertEquals(expectDTO, cbbTerminalModelDTOArr[0]);

        new Verifications() {
            {
                terminalModelDriverDAO.findByPlatformIn(platformArr);
                times = 1;
            }
        };
    }

    /**
     * 测试根据productId获取终端类型信息
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testqueryByProductIdAndPlatform() throws BusinessException {
        String productId = "123";

        TerminalModelDriverEntity modelDriverEntity = new TerminalModelDriverEntity();
        modelDriverEntity.setId(UUID.randomUUID());
        modelDriverEntity.setProductId("123");
        modelDriverEntity.setProductModel("456");
        modelDriverEntity.setCpuType("bbb");

        new Expectations() {
            {
                terminalModelDriverDAO.findByProductIdAndPlatform(productId, PLATFORM);
                result = Lists.newArrayList(modelDriverEntity);
            }
        };

        CbbTerminalModelDTO result = terminalModelService.queryByProductIdAndPlatform(productId, PLATFORM);

        CbbTerminalModelDTO expectDTO = new CbbTerminalModelDTO();
        expectDTO.setProductId("123");
        expectDTO.setCpuType("bbb");
        expectDTO.setProductModel("456");
        assertEquals(expectDTO, result);

        new Verifications() {
            {
                terminalModelDriverDAO.findByProductIdAndPlatform(productId, PLATFORM);
                times = 1;
            }
        };

    }

    /**
     * 测试根据productId获取终端类型信息 - 不存在
     *
     * @throws BusinessException 业务异常
     */
    @Test
    public void testqueryByProductIdAndPlatformNotExist() throws BusinessException {
        String productId = "123";

        new Expectations() {
            {
                terminalModelDriverDAO.findByProductIdAndPlatform(productId, PLATFORM);
                result = Lists.newArrayList();
            }
        };

        try {
            terminalModelService.queryByProductIdAndPlatform(productId, PLATFORM);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_MODEL_NOT_EXIST_ERROR, e.getKey());
        }

        new Verifications() {
            {
                terminalModelDriverDAO.findByProductIdAndPlatform(productId, PLATFORM);
                times = 1;
            }
        };

    }


    /**
     * testQueryTerminalOsTypeByPlatform
     */
    @Test
    public void testQueryTerminalOsTypeByPlatform() {

        new Expectations() {
            {
                terminalBasicInfoDAO.getTerminalOsTypeByPlatform(new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.APP});
                result = Lists.newArrayList("Windows");
            }
        };
        List<String> osTypeList = terminalModelService.queryTerminalOsTypeByPlatform(new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.APP});
        Assert.assertEquals(osTypeList.get(0), "Windows");
    }

    /**
     * testQueryTerminalOsTypeByPlatform
     */
    @Test
    public void testQueryTerminalOsTypeByPlatformIsEmpty() {

        new Expectations() {
            {
                terminalBasicInfoDAO.getTerminalOsTypeByPlatform(new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.APP});
                result = null;
            }
        };
        List<String> osTypeList = terminalModelService.queryTerminalOsTypeByPlatform(new CbbTerminalPlatformEnums[] {CbbTerminalPlatformEnums.APP});
        Assert.assertEquals(osTypeList.size(), 0);
    }
}
