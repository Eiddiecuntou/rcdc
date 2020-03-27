package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseMakeBtSeedRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseStartBtShareRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseStopBtShareRequest;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.hciadapter.module.def.api.CloudPlatformMgmtAPI;
import com.ruijie.rcos.rcdc.hciadapter.module.def.dto.ClusterVirtualIpDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;

/**
 * Description: BT服务工具类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/19 16:23
 *
 * @author zhangyichi
 */
@Service
public class BtClientServiceImpl implements BtClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BtClientServiceImpl.class);

    private static final String SEED_FILE_FORMAT = ".torrent";

    @Autowired
    private CloudPlatformMgmtAPI cloudPlatformMgmtAPI;

    @Autowired
    private BtClientAPI btClientAPI;

    /**
     * 开启目标文件的BT分享服务
     * @param filePath 目标文件路径
     * @param seedPath 对应的种子文件路径
     * @throws BusinessException 业务异常
     */
    public void startBtShare(String filePath, String seedPath) throws BusinessException {
        Assert.hasText(filePath, "filePath cannot be blank!");
        Assert.hasText(seedPath, "seedPath cannot be blank!");

        LOGGER.info("开启BT分享服务，目标文件[{}]，种子文件[{}]", filePath, seedPath);
        try {
            validateSeedFile(seedPath);
            validateTargetFile(filePath);

            // 开启BT分享
            BaseStartBtShareRequest apiRequest = new BaseStartBtShareRequest();
            apiRequest.setSeedFilePath(seedPath);
            apiRequest.setFilePath(filePath);
            btClientAPI.startBtShare(apiRequest);
        } catch (Exception e) {
            LOGGER.error("开启BT分享失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_BT_START_SHARE_SEED_FILE_FAIL, e);
        }
    }

    /**
     * 停止BT分享服务
     * @param seedPath 种子文件路径
     * @throws BusinessException 业务异常
     */
    public void stopBtShare(String seedPath) throws BusinessException {
        Assert.hasText(seedPath, "seedPath cannot be blank!");

        LOGGER.info("关闭BT分享服务，种子文件[{}]", seedPath);
        try {
            validateSeedFile(seedPath);

            // 关闭BT分享
            BaseStopBtShareRequest apiRequest = new BaseStopBtShareRequest();
            apiRequest.setSeedFilePath(seedPath);
            btClientAPI.stopBtShare(apiRequest);
        } catch (Exception e) {
            LOGGER.error("关闭BT分享失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_BT_STOP_SHARE_SEED_FILE_FAIL, e);
        }
    }

    /**
     * 制作目标文件的BT种子
     * @param filePath 目标文件路径
     * @param seedSavePath 种子存放目录
     * @return 制作完成的种子文件信息
     * @throws BusinessException 业务异常
     */
    public SeedFileInfoDTO makeBtSeed(String filePath, String seedSavePath) throws BusinessException {
        Assert.hasText(filePath, "filePath cannot be blank!");
        Assert.hasText(seedSavePath, "seedSavePath cannot be blank!");

        LOGGER.info("制作BT种子，目标文件[{}]，种子文件目录[{}]", filePath, seedSavePath);
        try {
            validateTargetFile(filePath);
            createFilePath(seedSavePath);

            BaseMakeBtSeedRequest apiRequest = new BaseMakeBtSeedRequest();
            apiRequest.setFilePath(filePath);
            apiRequest.setSeedSavePath(seedSavePath);
            apiRequest.setIpAddr(getLocalIP());
            DtoResponse<SeedFileInfoDTO> apiResponse = btClientAPI.makeBtSeed(apiRequest);

            Assert.notNull(apiResponse, "BT API response is null!");
            Assert.isTrue(DtoResponse.Status.SUCCESS == apiResponse.getStatus(), "BT API response fail!");
            Assert.notNull(apiResponse.getDto(), "seedInfoDto is null!");
            return apiResponse.getDto();
        } catch (Exception e) {
            LOGGER.error("制作BT种子失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_BT_MAKE_SEED_FILE_FAIL, e);
        }
    }

    private void validateTargetFile(String targetFilePath) {
        File file = new File(targetFilePath);
        Assert.isTrue(file.exists(), "target file not exist");
    }

    private void validateSeedFile(String seedPath) {
        String lowerCaseSeedPath = seedPath.trim().toLowerCase();
        Assert.isTrue(lowerCaseSeedPath.endsWith(SEED_FILE_FORMAT), "illegal seed file format!");
        File file = new File(seedPath);
        Assert.isTrue(file.exists(), "seed file not exist");
    }

    /**
     * 获取ip
     *
     * @return ip
     */
    private String getLocalIP() throws BusinessException {
        DtoResponse<ClusterVirtualIpDTO> resp = cloudPlatformMgmtAPI.getClusterVirtualIp(new DefaultRequest());
        return resp.getDto().getClusterVirtualIpIp();
    }

    private void createFilePath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
            file.setReadable(true, false);
            file.setExecutable(true, false);
        }
    }
}
