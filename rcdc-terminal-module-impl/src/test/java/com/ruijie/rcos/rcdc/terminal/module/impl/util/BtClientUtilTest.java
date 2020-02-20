package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import com.ruijie.rcos.base.sysmanage.module.def.api.BtClientAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.NetworkAPI;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseMakeBtSeedRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseStartBtShareRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.btclient.BaseStopBtShareRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.request.network.BaseDetailNetworkRequest;
import com.ruijie.rcos.base.sysmanage.module.def.api.response.network.BaseDetailNetworkInfoResponse;
import com.ruijie.rcos.base.sysmanage.module.def.dto.BaseNetworkDTO;
import com.ruijie.rcos.base.sysmanage.module.def.dto.SeedFileInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/19 20:57
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class BtClientUtilTest {

    private static final String ROOT_PATH = Thread.currentThread().getClass().getResource("/").getPath();

    @Tested
    private BtClientUtil btClientUtil;

    @Injectable
    private BtClientAPI btClientAPI;

    @Injectable
    private NetworkAPI networkAPI;

    /**
     * 开启BT分享，正常流程
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testStartBtShare() throws IOException, BusinessException {
        String filePath = ROOT_PATH + "file";
        File file = new File(filePath);
        file.createNewFile();

        String seedPath = ROOT_PATH + "seed.torrent";
        File seedFile = new File(seedPath);
        seedFile.createNewFile();

        try {
            BtClientUtil.startBtShare(filePath, seedPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            file.delete();
            seedFile.delete();
        }

        new Verifications() {
            {
                btClientAPI.startBtShare((BaseStartBtShareRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 开启BT分享，目标文件不存在
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testStartBtShareTargerFileNotExist() throws IOException, BusinessException {
        String filePath = ROOT_PATH + "file";

        String seedPath = ROOT_PATH + "seed.torrent";
        File seedFile = new File(seedPath);
        seedFile.createNewFile();

        try {
            BtClientUtil.startBtShare(filePath, seedPath);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_BT_START_SHARE_SEED_FILE_FAIL, e.getKey());
        } finally {
            seedFile.delete();
        }

        new Verifications() {
            {
                btClientAPI.startBtShare((BaseStartBtShareRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 开启BT分享，种子文件不存在
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testStartBtShareSeedFileNotExist() throws IOException, BusinessException {
        String filePath = ROOT_PATH + "file";
        File file = new File(filePath);
        file.createNewFile();

        String seedPath = ROOT_PATH + "seed.torrent";

        try {
            BtClientUtil.startBtShare(filePath, seedPath);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_BT_START_SHARE_SEED_FILE_FAIL, e.getKey());
        } finally {
            file.delete();
        }

        new Verifications() {
            {
                btClientAPI.startBtShare((BaseStartBtShareRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 开启BT分享，种子文件格式错误
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testStartBtShareSeedFileFormatError() throws IOException, BusinessException {
        String filePath = ROOT_PATH + "file";
        File file = new File(filePath);
        file.createNewFile();

        String seedPath = ROOT_PATH + "seed";
        File seedFile = new File(seedPath);
        seedFile.createNewFile();

        try {
            BtClientUtil.startBtShare(filePath, seedPath);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_BT_START_SHARE_SEED_FILE_FAIL, e.getKey());
        } finally {
            file.delete();
            seedFile.delete();
        }

        new Verifications() {
            {
                btClientAPI.startBtShare((BaseStartBtShareRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 停止BT分享，正常流程
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testStopBtShare() throws IOException, BusinessException {
        String seedPath = ROOT_PATH + "seed.torrent";
        File seedFile = new File(seedPath);
        seedFile.createNewFile();

        try {
            BtClientUtil.stopBtShare(seedPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            seedFile.delete();
        }

        new Verifications() {
            {
                btClientAPI.stopBtShare((BaseStopBtShareRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 停止BT分享，种子文件格式错误
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testStopBtShareSeedFileFormatError() throws IOException, BusinessException {
        String seedPath = ROOT_PATH + "seed";
        File seedFile = new File(seedPath);
        seedFile.createNewFile();

        try {
            BtClientUtil.stopBtShare(seedPath);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_BT_STOP_SHARE_SEED_FILE_FAIL, e.getKey());
        } finally {
            seedFile.delete();
        }

        new Verifications() {
            {
                btClientAPI.stopBtShare((BaseStopBtShareRequest) any);
                times = 0;
            }
        };
    }

    /**
     * 制作BT种子，正常流程，保存目录存在
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testMakeBtSeed() throws IOException, BusinessException {
        String seedSaveDirPath = ROOT_PATH + "seedSaveDir";
        File seedSaveDir = new File(seedSaveDirPath);
        seedSaveDir.mkdir();

        String filePath = ROOT_PATH + "seed.torrent";
        File file = new File(filePath);
        file.createNewFile();

        SeedFileInfoDTO seedFileInfoDTO = new SeedFileInfoDTO("seedPath", "seedMD5");
        DtoResponse<SeedFileInfoDTO> seedInfoResponse = DtoResponse.success(seedFileInfoDTO);

        BaseDetailNetworkInfoResponse networkInfoResponse = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO baseNetworkDTO = new BaseNetworkDTO();
        baseNetworkDTO.setIp("0.0.0.0");
        networkInfoResponse.setNetworkDTO(baseNetworkDTO);

        new Expectations() {
            {
                btClientAPI.makeBtSeed((BaseMakeBtSeedRequest) any);
                result = seedInfoResponse;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = networkInfoResponse;
            }
        };

        try {
            SeedFileInfoDTO resultDto = BtClientUtil.makeBtSeed(filePath, seedSaveDirPath);
            Assert.assertEquals("seedPath", resultDto.getSeedFilePath());
            Assert.assertEquals("seedMD5", resultDto.getSeedFileMD5());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            file.delete();
            seedSaveDir.delete();
        }

        new Verifications() {
            {
                btClientAPI.makeBtSeed((BaseMakeBtSeedRequest) any);
                times = 1;
            }
        };
    }

    /**
     * 制作BT种子，异常
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testMakeBtSeedException() throws IOException, BusinessException {
        String seedSaveDirPath = ROOT_PATH + "seedSaveDir";

        String filePath = ROOT_PATH + "seed.torrent";
        File file = new File(filePath);
        file.createNewFile();

        DtoResponse<SeedFileInfoDTO> seedInfoResponse = DtoResponse.fail("key");

        BaseDetailNetworkInfoResponse networkInfoResponse = new BaseDetailNetworkInfoResponse();
        BaseNetworkDTO baseNetworkDTO = new BaseNetworkDTO();
        baseNetworkDTO.setIp("0.0.0.0");
        networkInfoResponse.setNetworkDTO(baseNetworkDTO);

        new Expectations() {
            {
                btClientAPI.makeBtSeed((BaseMakeBtSeedRequest) any);
                result = seedInfoResponse;
                networkAPI.detailNetwork((BaseDetailNetworkRequest) any);
                result = networkInfoResponse;
            }
        };

        try {
            BtClientUtil.makeBtSeed(filePath, seedSaveDirPath);
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_BT_MAKE_SEED_FILE_FAIL, e.getKey());
        } finally {
            file.delete();
        }

        new Verifications() {
            {
                btClientAPI.makeBtSeed((BaseMakeBtSeedRequest) any);
                times = 1;
            }
        };
    }
}