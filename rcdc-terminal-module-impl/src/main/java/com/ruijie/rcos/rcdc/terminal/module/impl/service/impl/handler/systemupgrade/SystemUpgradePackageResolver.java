package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/18 14:56
 *
 * @author TING
 */
public interface SystemUpgradePackageResolver {

    /**
     *  类型校验
     *
     * @param fileName 文件名
     * @return 类型是否正确
     */
    boolean checkFileType(String fileName);

    /**
     *  获取升级包配置信息
     *
     * @param fileName 文件名
     * @param filePath 文件路径
     * @return 升级包文件信息
     * @throws BusinessException 业务异常
     */
    TerminalUpgradeVersionFileInfo getPackageConfig(String fileName, String filePath) throws BusinessException;

}
