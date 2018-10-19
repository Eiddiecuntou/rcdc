package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.ShutdownRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.ShutdownResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.ShutdownCallback;

public interface TerminalMgmtAPI {
	ShutdownResponse shutdown(ShutdownRequest request, ShutdownCallback callback);
}
