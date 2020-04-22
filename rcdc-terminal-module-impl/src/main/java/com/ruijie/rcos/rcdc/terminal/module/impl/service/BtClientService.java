package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/25 21:49
 *
 * @author zhangyichi
 */
public interface BtClientService {

    /**
     * 开启目标文件的BT分享服务
     * @param filePath 目标文件路径
     * @param seedPath 对应的种子文件路径
     * @throws BusinessException 业务异常
     */
    void startBtShare(String filePath, String seedPath) throws BusinessException;

    /**
     * 停止BT分享服务
     * @param seedPath 种子文件路径
     * @throws BusinessException 业务异常
     */
    void stopBtShare(String seedPath) throws BusinessException;

    /**
     * 制作目标文件的BT种子
     * @param filePath 目标文件路径
     * @param seedSavePath 种子存放目录
     * @return 制作完成的种子文件信息
     * @throws BusinessException 业务异常
     */
    SeedFileInfoDTO makeBtSeed(String filePath, String seedSavePath) throws BusinessException;
}
