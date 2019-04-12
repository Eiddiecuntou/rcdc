package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBatDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalDetectResultRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalLogNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;

/**
 * Description: 终端操作实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/5
 *
 * @author Jarman
 */
public class CbbTerminalOperatorAPIImpl implements CbbTerminalOperatorAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalOperatorAPIImpl.class);

    @Autowired
    private TerminalOperatorService operatorService;

    @Autowired
    private TerminalDetectService detectService;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private CbbTerminalBasicInfoAPI basicInfoAPI;

    @Autowired
    private CollectLogCacheManager collectLogCacheManager;

    @Override
    public DefaultResponse shutdown(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.shutdown(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse restart(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.restart(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse changePassword(CbbChangePasswordRequest request) throws BusinessException {
        Assert.notNull(request, "CbbChangePasswordRequest不能为空");

        operatorService.changePassword(request.getPassword());
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse collectLog(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        String terminalId = request.getTerminalId();
        operatorService.collectLog(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse detect(CbbTerminalDetectRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");

        String terminalId = request.getTerminalId();
        operatorService.detect(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse detect(CbbTerminalBatDetectRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalBatDetectRequest不能为null");
        operatorService.detect(request.getTerminalIdArr());
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultPageResponse<CbbTerminalDetectDTO> listDetect(CbbTerminalDetectPageRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        Page<TerminalDetectionEntity> page = detectService.pageQuery(request);
        if (page.getNumberOfElements() == 0) {
            LOGGER.debug("detect page query returns 0 element, detect date[{}], page[{}], limit[{}]", request.getDate(), request.getPage(),
                    request.getLimit());
            return buildEmptyResponse(page.getTotalElements());
        }

        List<TerminalDetectionEntity> detectionList = page.getContent();
        int size = detectionList.size();
        CbbTerminalDetectDTO[] detectDTOArr = new CbbTerminalDetectDTO[size];

        Stream.iterate(0, i -> i + 1).limit(size).forEach(i -> {
            CbbTerminalDetectDTO detectDTO = new CbbTerminalDetectDTO();
            TerminalDetectionEntity detectionEntity = detectionList.get(i);
            detectionEntity.convertTo(detectDTO);
            setThreshold(detectDTO);
            TerminalEntity terminalEntity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(detectionEntity.getTerminalId());
            if (terminalEntity != null) {
                detectDTO.setIp(terminalEntity.getIp());
                detectDTO.setTerminalName(terminalEntity.getTerminalName());
            }
            detectDTOArr[i] = detectDTO;
        });

        return DefaultPageResponse.Builder.success(page.getSize(), (int) page.getTotalElements(), detectDTOArr);
    }

    @Override
    public CbbDetectInfoResponse getRecentDetect(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalDetectDTO detectInfo = detectService.getRecentDetect(request.getTerminalId());
        CbbDetectInfoResponse response = new CbbDetectInfoResponse();
        response.setDetectInfo(detectInfo);
        return response;
    }

    private void setThreshold(CbbTerminalDetectDTO detectDTO) {
        detectDTO.setBandwidthThreshold(Constants.TERMINAL_DETECT_BINDWIDTH_NORM);
        detectDTO.setPacketLossRateThreshold(Constants.TERMINAL_DETECT_PACKET_LOSS_RATE);
        detectDTO.setDelayThreshold(Constants.TERMINAL_DETECT_DELAY_NORM);
    }

    /**
     * 构建空列表返回参数
     * 
     * @param total 总数
     * @return 空列表响应
     */
    private DefaultPageResponse<CbbTerminalDetectDTO> buildEmptyResponse(long total) {
        DefaultPageResponse<CbbTerminalDetectDTO> emptyResp = new DefaultPageResponse<CbbTerminalDetectDTO>();
        emptyResp.setItemArr(new CbbTerminalDetectDTO[0]);
        emptyResp.setTotal(total);
        emptyResp.setStatus(Status.SUCCESS);
        return emptyResp;
    }

    @Override
    public CbbDetectResultResponse getDetectResult(CbbTerminalDetectResultRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbTerminalDetectResultDTO result = detectService.getDetectResult(request.getDetectDate());
        CbbDetectResultResponse resp = new CbbDetectResultResponse(result);
        resp.setStatus(Status.SUCCESS);
        return resp;
    }

    @Override
    public CbbTerminalBasicInfoResponse getTerminalBaiscInfo(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        return basicInfoAPI.findBasicInfoByTerminalId(request);
    }

    @Override
    public CbbTerminalCollectLogStatusResponse getCollectLog(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CollectLogCache cache = getCollectLogCache(request.getTerminalId());
        CbbTerminalCollectLogStatusResponse response = new CbbTerminalCollectLogStatusResponse();
        response.setLogName(cache.getLogFileName());
        response.setState(cache.getState());
        return response;
    }

    @Override
    public CbbTerminalLogFileInfoResponse getTerminalLogFileInfo(CbbTerminalLogNameRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        String logFileName = request.getLogName();
        String logFilePath = Constants.STORE_TERMINAL_LOG_PATH + logFileName;
        checkFileExist(logFilePath);
        String logFileNameWithoutExtension = Files.getNameWithoutExtension(logFileName);
        String suffix = getFileSuffix(logFileName);
        CbbTerminalLogFileInfoResponse response = new CbbTerminalLogFileInfoResponse();
        response.setLogFilePath(logFilePath);
        response.setLogFileName(logFileNameWithoutExtension);
        response.setSuffix(suffix);

        return response;
    }

    private void checkFileExist(String logFilePath) throws BusinessException {
        File logFile = new File(logFilePath);
        if (logFile.isFile()) {
            return;
        }
        throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST);
    }

    private String getFileSuffix(String fileName) {
        String suffix = "";
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf > -1) {
            suffix = fileName.substring(lastIndexOf + 1);
        }
        return suffix;
    }

    /**
     * 获取日志缓存
     * 
     * @param terminalId 终端id
     * @return 日志收集缓存
     * @throws BusinessException 业务异常
     */
    private CollectLogCache getCollectLogCache(String terminalId) throws BusinessException {
        CollectLogCache cache = collectLogCacheManager.getCache(terminalId);
        if (cache == null) {
            // 日志不存在，构造失败状态信息
            cache = new CollectLogCache(CollectLogStateEnums.FAILURE);
        }
        return cache;
    }

}
