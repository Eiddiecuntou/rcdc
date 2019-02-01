package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.DeleteTerminalUpgradeBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalUpgradeBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalUpgradeBatchTaskItem;
import com.ruijie.rcos.rcdc.terminal.module.web.request.CreateTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DeleteTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradeRequest;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItem;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;

/**
 * 
 * Description: 终端系统升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
@Controller
@RequestMapping("/cbb/terminal/system/upgrade")
@EnableCustomValidate(enable = false)
public class TerminalSystemUpgradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeController.class);

    @Autowired
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    /**
     * 上传系统升级文件
     * 
     * @param file 上传文件
     * @param optLogRecorder 日志记录
     * @return 上传响应返回
     * @throws BusinessException 业务异常
     */
    @RequestMapping("/package/upload")
    public DefaultWebResponse uploadPackage(ChunkUploadFile file, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        Assert.notNull(file, "file 不能为空");
        Assert.notNull(optLogRecorder, "optLogRecorder 不能为空");

        CbbTerminalUpgradePackageUploadRequest request =
                new CbbTerminalUpgradePackageUploadRequest(file.getFilePath(), file.getFileName(), file.getFileMD5());
        try {
            cbbTerminalUpgradeAPI.uploadUpgradeFile(request);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG,
                    file.getFileName());
        } catch (BusinessException ex) {
            // 上传文件处理失败
            LOGGER.error("upload terminal system package fail, file name is [{}]", file.getFileName());
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
                    file.getFileName(), ex.getI18nMessage());
            throw ex;
        }
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 
     * 系统终端升级包信息列表
     * 
     * @param listRequest 分页请求信息
     * @return 分页列表信息
     * @throws BusinessException 业务异常
     */
    @RequestMapping("/package/list")
    public DefaultWebResponse listPackage(ListTerminalSystemUpgradePackageRequest listRequest)
            throws BusinessException {
        Assert.notNull(listRequest, "listRequest can not be null");

        CbbTerminalSystemUpgradePackageListRequest request = new CbbTerminalSystemUpgradePackageListRequest();
        request.setPaltform(listRequest.getPlatform());
        CbbBaseListResponse<CbbTerminalSystemUpgradePackageInfoDTO> resp =
                cbbTerminalUpgradeAPI.listSystemUpgradePackage(request);
        return DefaultWebResponse.Builder.success(resp);
    }

    /**
     * 
     * 添加终端系统升级任务
     * 
     * @param request 添加升级请求
     * @param optLogRecorder 日志记录
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("create")
    public DefaultWebResponse create(CreateTerminalSystemUpgradeRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "CreateTerminalSystemUpgradeRequest can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder不能为null");

        String[] terminalIdArr = request.getTerminalIdArr();
        TerminalPlatformEnums platform = request.getPlatform();
        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<TerminalUpgradeBatchTaskItem> iterator =
                Stream.of(idArr).map(id -> new TerminalUpgradeBatchTaskItem(id, BusinessKey.RCDC_TERMINAL_UPGRADE_ITEM_NAME, platform)).iterator();
        TerminalUpgradeBatchTaskHandler handler =
                new TerminalUpgradeBatchTaskHandler(this.cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);

        builder.setTaskName(BusinessKey.RCDC_TERMINAL_UPGRADE_TASK_NAME, new String[] {})
                .setTaskDesc(BusinessKey.RCDC_TERMINAL_UPGRADE_TASK_DESC, new String[] {}) //
                .registerHandler(handler) //
                .start();

        return DefaultWebResponse.Builder.success();
    }

    /**
     * 
     * 移除系统升级
     * 
     * @param request 移除系统升级请求
     * @param optLogRecorder 日志记录
     * @param builder 批量任务builder
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("delete")
    public DefaultWebResponse delete(DeleteTerminalSystemUpgradeRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        LOGGER.warn("start remove system upgrade task...");

        String[] terminalIdArr = request.getIdArr();
        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);

        final Iterator<DefaultBatchTaskItem> iterator = Stream.of(idArr)
                .map(id -> DefaultBatchTaskItem.builder().itemId(id).itemName("删除终端升级").build()).iterator();
        DeleteTerminalUpgradeBatchTaskHandler handler =
                new DeleteTerminalUpgradeBatchTaskHandler(this.cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);

        builder.setTaskName(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_TASK_NAME, new String[] {})
                .setTaskDesc(BusinessKey.RCDC_TERMINAL_DELETE_UPGRADE_TASK_DESC, new String[] {}) //
                .registerHandler(handler) //
                .start();

        LOGGER.warn("finish remove system upgrade task");
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 
     * 终端升级任务列表
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("list")
    public DefaultWebResponse list(ListTerminalSystemUpgradeRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO> resp =
                cbbTerminalUpgradeAPI.listTerminalSystemUpgradeTask();
        return DefaultWebResponse.Builder.success(resp);
    }

}
