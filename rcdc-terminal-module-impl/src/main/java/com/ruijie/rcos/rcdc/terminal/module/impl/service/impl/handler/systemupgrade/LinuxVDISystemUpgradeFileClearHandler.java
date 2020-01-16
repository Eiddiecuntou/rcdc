package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import java.io.File;

import org.springframework.stereotype.Service;

import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 刷机任务文件清理处理器
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月23日
 * 
 * @author nt
 */
@Service
public class LinuxVDISystemUpgradeFileClearHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxVDISystemUpgradeFileClearHandler.class);

    /**
     * 刷机任务完成文件清理
     * 
     * @throws BusinessException 业务异常
     */
    public void clear() {
        // 清除刷机目录文件
        LOGGER.info("开始清理刷机目录文件");
        FileOperateUtil.deleteFile(new File(Constants.PXE_SAMBA_LINUX_VDI_UPGRADE_BEGIN_FILE_PATH));
        FileOperateUtil.deleteFile(new File(Constants.PXE_SAMBA_LINUX_VDI_UPGRADE_SUCCESS_FILE_PATH));
        LOGGER.info("完成清理刷机目录文件");
    }

}
