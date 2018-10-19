package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruijie.rcos.rcdc.terminal.module.def.api.TerminalMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.ShutdownRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.ShutdownResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.ShutdownCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalService;

public class TerminalMgmtAPIImpl implements TerminalMgmtAPI {
	@Autowired
	private TerminalService terminalService;
	
	@Override
	public ShutdownResponse shutdown(ShutdownRequest request, ShutdownCallback callback) {
		callback.execute();
		return null;
	}
}
