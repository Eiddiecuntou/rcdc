package com.ruijie.rcos.rcdc.terminal.module.web;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DownloadLogRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.webmvc.api.response.DownloadWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Description: 收集终端日志
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/12
 *
 * @author Jarman
 */
@RestController
@RequestMapping("/terminal")
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
    public DownloadWebResponse download(DownloadLogRequest request) throws BusinessException {
        Assert.notNull(request, "DownloadLogRequest 不能为null");
//        String logFileName = cbbTerminalOperatorAPI.getTerminalLogName(request.getTerminalId());
//        String logFilePath = TERMINAL_LOG_DIR + logFileName;
//        final DownloadWebResponse response = new DownloadWebResponse.Builder()
//                .setFile(new File(logFilePath))
//                .build();

        return null;
    }


}
