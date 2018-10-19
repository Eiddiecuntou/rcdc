package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.DispatcherRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherInterface;

@DispatcherInterface
public interface DispatcherHandlerSPI {
	void dispatch(DispatcherRequest request);
}
