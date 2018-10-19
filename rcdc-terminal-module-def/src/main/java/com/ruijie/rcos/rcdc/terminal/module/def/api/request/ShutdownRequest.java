package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import javax.validation.constraints.NotNull;

import com.ruijie.rcos.sk.base.annotation.Nullable;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

public class ShutdownRequest implements Request {
	@NotNull
	private Long terminalId;
	
	private String xxx;

	public void setTerminalId(Long terminalId) {
		this.terminalId = terminalId;
	}

	public Long getTerminalId() {
		return terminalId;
	}
}
