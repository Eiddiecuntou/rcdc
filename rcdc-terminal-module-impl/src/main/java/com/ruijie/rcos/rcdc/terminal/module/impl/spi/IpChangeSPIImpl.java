package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * 
 * Description: ip变更
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月16日
 * 
 * @author nt
 */
@DispatcherImplemetion("123456")
public class IpChangeSPIImpl implements CbbDispatcherHandlerSPI{

    /**
     * TODO ip变更spi还未提供
     */
    public void ipChange(String ip) {
        
    }

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        // TODO Auto-generated method stub
        
    }
}
