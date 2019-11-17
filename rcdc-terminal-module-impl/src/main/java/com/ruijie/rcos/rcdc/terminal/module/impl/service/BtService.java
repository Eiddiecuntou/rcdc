package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.impl.model.SeedFileInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/23
 *
 * @author hs
 */
public interface BtService {

    /**
     *  制作bt种子
     *
     * @param filePath 文件路径
     * @param seedSavePath 种子文件保存路径
     * @param ipAddr ip地址
     * @return 种子信息
     * @throws BusinessException 业务异常
     */
    SeedFileInfo makeBtSeed (String filePath, String seedSavePath, String ipAddr) throws BusinessException;

    /**
     *  开启bt分享
     *
     * @param seedFilePath 种子文件路径
     * @param filePath 文件路径
     * @throws BusinessException 业务异常
     */
    void startBtShare (String seedFilePath, String filePath) throws BusinessException;

    /**
     * 关闭bt分享
     *
     * @param seedFilePath 种子文件路径
     * @throws BusinessException 业务异常
     */
    void stopBtShare (String seedFilePath) throws BusinessException;




}
