package com.ruijie.rcos.rcdc.terminal.module.impl.spi.maintenance;

import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseUpgradeDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.Response;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/17
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class MaintenanceModeNotifySPIImplTest {

    @Tested
    private MaintenanceModeNotifySPIImpl spi;

    @Injectable
    private BeforeEnteringMaintenanceHandler beforeEnteringMaintenanceHandler;

    /**
     * testBeforeEnteringMaintenance
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testBeforeEnteringMaintenance() throws BusinessException {

        new Expectations() {
            {
                beforeEnteringMaintenanceHandler.handle();
            }
        };

        spi.beforeEnteringMaintenance("aaa", new BaseUpgradeDTO());

        new Verifications() {
            {
                beforeEnteringMaintenanceHandler.handle();
                times = 1;
            }
        };
    }

    /**
     * testAfterEnteringMaintenance
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testAfterUnderMaintenance() throws BusinessException {

        Response response = spi.afterUnderMaintenance("aaa", new BaseUpgradeDTO());
        assertEquals(Response.Status.SUCCESS, response.getStatus());
    }

    /**
     * testAfterMaintenanceEnd
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testAfterMaintenanceEnd() throws BusinessException {

        Response response = spi.afterMaintenanceEnd("aaa", new BaseUpgradeDTO());
        assertEquals(Response.Status.SUCCESS, response.getStatus());
    }


    /**
     * testAfterEnteringMaintenance
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testAfterEnteringMaintenance() throws BusinessException {

        Response response = spi.afterEnteringMaintenance("aaa", new BaseUpgradeDTO());
        assertEquals(Response.Status.SUCCESS, response.getStatus());
    }
}
