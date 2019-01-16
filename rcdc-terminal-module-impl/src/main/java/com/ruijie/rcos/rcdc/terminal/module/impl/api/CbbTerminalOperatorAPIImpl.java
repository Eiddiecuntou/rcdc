package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDetectResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.*;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbDetectResultResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Stream;

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
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        Assert.hasText(request.getPassword(), "password不能为空");
        operatorService.changePassword(request.getTerminalId(), request.getPassword());
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
        operatorService.detect(request.getTerminalId());
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse detect(CbbTerminalBatDetectRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalBatDetectRequest不能为null");
        operatorService.detect(request.getTerminalIdArr());
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbTerminalNameResponse getTerminalLogName(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        CollectLogCache cache = collectLogCacheManager.getCache(terminalId);
        if (cache == null) {
            LOGGER.warn("收集日志缓存中不存在日志文件");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST);
        }
        if (cache.getState() == CollectLogStateEnums.FAILURE) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST);
        }
        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(cache.getLogFileName());
        return response;
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
            TerminalEntity terminalEntity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(detectionEntity.getTerminalId());
            detectDTO.setIp(terminalEntity.getIp());
            detectDTO.setTerminalName(terminalEntity.getTerminalName());
            detectDTOArr[i] = detectDTO;
        });

        return DefaultPageResponse.Builder.success(page.getSize(), (int) page.getTotalElements(), detectDTOArr);
    }

    /**
     * 构建空列表返回参数
     * @param total 总数
     * @return
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

}
