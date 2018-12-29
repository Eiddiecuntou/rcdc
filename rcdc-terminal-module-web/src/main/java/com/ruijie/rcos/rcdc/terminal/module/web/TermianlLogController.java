package com.ruijie.rcos.rcdc.terminal.module.web;

import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DownloadLogRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.webmvc.api.response.DownloadWebResponse;

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
    public DefaultWebResponse download(DownloadLogRequest request) throws BusinessException {
        Assert.notNull(request, "DownloadLogRequest 不能为null");
        //TODO
        return DefaultWebResponse.Builder.success();
    }


}
