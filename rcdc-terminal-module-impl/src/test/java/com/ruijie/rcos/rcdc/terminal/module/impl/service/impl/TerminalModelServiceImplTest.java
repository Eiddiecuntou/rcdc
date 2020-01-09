package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
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


    /**
     * 测试根据平台类型查询终端类型 - 列表为空
     *
     */
    @Test
    public void testQueryTerminalModelByPlatform() {
        CbbTerminalPlatformEnums platform = CbbTerminalPlatformEnums.VDI;

        new Expectations() {
            {
                terminalModelDriverDAO.findByPlatform(platform);
                result = Lists.newArrayList();
            }
        };

        CbbTerminalModelDTO[] cbbTerminalModelDTOArr = terminalModelService.queryTerminalModelByPlatform(platform);

        assertEquals(0, cbbTerminalModelDTOArr.length);

        new Verifications() {
            {
                terminalModelDriverDAO.findByPlatform(platform);
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
        CbbTerminalPlatformEnums platform = CbbTerminalPlatformEnums.VDI;

        TerminalModelDriverEntity modelDriverEntity = new TerminalModelDriverEntity();
        modelDriverEntity.setId(UUID.randomUUID());
        modelDriverEntity.setProductId("123");
        modelDriverEntity.setProductModel("456");
        modelDriverEntity.setCpuType("bbb");

        TerminalModelDriverEntity modelDriverEntity2 = new TerminalModelDriverEntity();
        modelDriverEntity2.setId(UUID.randomUUID());
        modelDriverEntity2.setProductId("abc");
        modelDriverEntity2.setProductModel("456");

        TerminalModelDriverEntity modelDriverEntity3 = new TerminalModelDriverEntity();
        modelDriverEntity3.setId(UUID.randomUUID());
        modelDriverEntity3.setProductId("3123123");
        modelDriverEntity3.setProductModel(null);

        new Expectations() {
            {
                terminalModelDriverDAO.findByPlatform(platform);
                result = Lists.newArrayList(modelDriverEntity, modelDriverEntity2, modelDriverEntity3);
            }
        };

        CbbTerminalModelDTO[] cbbTerminalModelDTOArr = terminalModelService.queryTerminalModelByPlatform(platform);

        assertEquals(1, cbbTerminalModelDTOArr.length);

        CbbTerminalModelDTO expectDTO = new CbbTerminalModelDTO();
        expectDTO.setCpuType("bbb");
        expectDTO.setProductId("123");
        expectDTO.setProductModel("456");
        assertEquals(expectDTO, cbbTerminalModelDTOArr[0]);

        new Verifications() {
            {
                terminalModelDriverDAO.findByPlatform(platform);
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
    public void testQueryByProductId() throws BusinessException {
        String productId = "123";

        TerminalModelDriverEntity modelDriverEntity = new TerminalModelDriverEntity();
        modelDriverEntity.setId(UUID.randomUUID());
        modelDriverEntity.setProductId("123");
        modelDriverEntity.setProductModel("456");
        modelDriverEntity.setCpuType("bbb");

        new Expectations() {
            {
                terminalModelDriverDAO.findByProductId(productId);
                result = Lists.newArrayList(modelDriverEntity);
            }
        };

        CbbTerminalModelDTO result = terminalModelService.queryByProductId(productId);

        CbbTerminalModelDTO expectDTO = new CbbTerminalModelDTO();
        expectDTO.setProductId("123");
        expectDTO.setCpuType("bbb");
        expectDTO.setProductModel("456");
        assertEquals(expectDTO, result);

        new Verifications() {
            {
                terminalModelDriverDAO.findByProductId(productId);
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
    public void testQueryByProductIdNotExist() throws BusinessException {
        String productId = "123";

        new Expectations() {
            {
                terminalModelDriverDAO.findByProductId(productId);
                result = Lists.newArrayList();
            }
        };

        try {
            terminalModelService.queryByProductId(productId);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_MODEL_NOT_EXIST_ERROR, e.getKey());
        }

        new Verifications() {
            {
                terminalModelDriverDAO.findByProductId(productId);
                times = 1;
            }
        };

    }

}
