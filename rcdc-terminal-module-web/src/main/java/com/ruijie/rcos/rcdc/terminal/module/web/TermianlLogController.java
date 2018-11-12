package com.ruijie.rcos.rcdc.terminal.module.web;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Description: 收集终端日志
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/12
 *
 * @author Jarman
 */
@RestController
public class TermianlLogController {

    private static final String TERMINAL_LOG_DIR = "/opt/ftp/terminal/log/";

    @Autowired
    private CbbTerminalOperatorAPI cbbTerminalOperatorAPI;

    /**
     * 下载日志文件
     *
     * @param terminalId
     */
    @GetMapping("/download/{terminalId}")
    public ResponseEntity<InputStreamResource> download(String terminalId) throws IOException, BusinessException {
        String logFileName = cbbTerminalOperatorAPI.getTerminalLogName(terminalId);
        String logFilePath = TERMINAL_LOG_DIR + logFileName;
        FileSystemResource file = new FileSystemResource(logFilePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

}
