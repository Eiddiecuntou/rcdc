package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

public class DispatcherRequest implements Request{
	@DispatcherKey
	private String dispatcherKey;
}
