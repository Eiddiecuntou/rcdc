package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DownloadLogRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.annotation.OptLog;
import com.ruijie.rcos.sk.webmvc.api.response.DownloadWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.WebResponse.Status;

/**
 * Description: 收集终端日志
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/12
 *
 * @author Jarman
 */
@Controller
@RequestMapping("/terminal")
@EnableCustomValidate(enable = false)
public class TermianlLogController {

    /**
     * 终端日志文件存储路径
     */
    private static final String TERMINAL_LOG_DIR = "/opt/ftp/terminal/log/";

    @Autowired
    private CbbTerminalOperatorAPI cbbTerminalOperatorAPI;

    /**
     * 下载日志文件
     *
     * @param request 请求参数
     * @return 返回下载
     * @throws BusinessException 业务异常
     */
    @RequestMapping("download")
    @OptLog(msgKey = BusinessKey.RCDC_TERMINAL_DOWNLOAD_TERMINAL_LOG_SUCCESS_LOG,
            msgArgs = {"request.userName", "request.terminalId"})
    @OptLog(msgKey = BusinessKey.RCDC_TERMINAL_DOWNLOAD_TERMINAL_LOG_FAIL_LOG,
            msgArgs = {"request.userName", "request.terminalId", "response.message"}, matchStatus = Status.ERROR)
    public DownloadWebResponse download(DownloadLogRequest request) throws BusinessException {
        Assert.notNull(request, "DownloadLogRequest 不能为null");
        // String logFileName = cbbTerminalOperatorAPI.getTerminalLogName(request.getTerminalId());
        // String logFilePath = TERMINAL_LOG_DIR + logFileName;
        // final DownloadWebResponse response = new DownloadWebResponse.Builder()
        // .setFile(new File(logFilePath))
        // .build();

        return null;
    }


}
