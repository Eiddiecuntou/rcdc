package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCloseSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.AddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.AddUpgradeTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalUpgradeBatchTaskItem;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.AppendTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CloseSystemUpgradeTaskWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CreateTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.ListTerminalWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.CreateSystemUpgradeTaskContentVO;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
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
@RequestMapping("/cbb/system/upgrade")
@EnableCustomValidate(enable = false)
public class TerminalSystemUpgradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeController.class);

    @Autowired
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    @Autowired
    private CbbTerminalSystemUpgradePackageAPI cbbTerminalUpgradePackageAPI;

    /**
     * 上传系统升级文件
     * 
     * @param file 上传文件
     * @param optLogRecorder 日志记录对象
     * @return 上传响应返回
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/package/create")
    public DefaultWebResponse uploadPackage(ChunkUploadFile file, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        Assert.notNull(file, "file can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");

        CbbTerminalUpgradePackageUploadRequest request =
                new CbbTerminalUpgradePackageUploadRequest(file.getFilePath(), file.getFileName(), file.getFileMD5());
        try {
            cbbTerminalUpgradePackageAPI.uploadUpgradeFile(request);
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
     * @param request 分页请求信息
     * @return 分页列表信息
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/package/list")
    public DefaultWebResponse listPackage(PageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        PageSearchRequest apiRequest = new PageSearchRequest(request);
        DefaultPageResponse<CbbTerminalSystemUpgradePackageInfoDTO> resp =
                cbbTerminalUpgradePackageAPI.listSystemUpgradePackage(apiRequest);
        return DefaultWebResponse.Builder.success(resp);
    }

    /**
     * 
     * 添加终端系统升级任务
     * 
     * @param request 添加升级请求
     * @param optLogRecorder 日志记录对象
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "create")
    public DefaultWebResponse create(CreateTerminalSystemUpgradeWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");

        UUID packageId = request.getPackageId();
        String[] terminalIdArr = request.getTerminalIdArr();
        UUID upgradeTaskId = createUpgradeTask(packageId, terminalIdArr, optLogRecorder);

        CreateSystemUpgradeTaskContentVO contentVO = new CreateSystemUpgradeTaskContentVO();
        contentVO.setUpgradeTaskId(upgradeTaskId);
        return DefaultWebResponse.Builder.success(contentVO);
    }

    /**
     * 创建刷机任务
     * 
     * @param packageId 刷机包id
     * @param optLogRecorder 日志记录对象
     * @return 刷机任务id
     * @throws BusinessException 业务异常
     */
    private UUID createUpgradeTask(UUID packageId, String[] terminalIdArr, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        CbbAddSystemUpgradeTaskRequest addTaskRequest = new CbbAddSystemUpgradeTaskRequest();
        addTaskRequest.setPackageId(packageId);
        addTaskRequest.setTerminalIdArr(terminalIdArr);
        AddSystemUpgradeTaskResponse response;
        try {
            response = cbbTerminalUpgradeAPI.addSystemUpgradeTask(addTaskRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_SUCCESS_LOG, response.getImgName());
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_FAIL_LOG, e.getI18nMessage());
            throw e;
        }
        return response.getUpgradeTaskId();
    }

    /**
     * 追加刷机终端
     * 
     * @param request 请求参数
     * @param optLogRecorder 操作日志对象
     * @param builder 批任务对象
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "append")
    public DefaultWebResponse append(AppendTerminalSystemUpgradeWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        String[] terminalIdArr = request.getTerminalIdArr();
        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<TerminalUpgradeBatchTaskItem> iterator =
                Stream.of(idArr).map(id -> buildAppendTaskItem(upgradeTaskId, id)).iterator();
        AddUpgradeTerminalBatchTaskHandler handler =
                new AddUpgradeTerminalBatchTaskHandler(this.cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);

        builder.setTaskName(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_TASK_NAME, new String[] {})
                .setTaskDesc(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_TASK_DESC, new String[] {}) //
                .registerHandler(handler).start();

        return DefaultWebResponse.Builder.success();
    }

    /**
     * 构建追加刷机任务终端任务项
     * 
     * @param upgradeTaskId 刷机任务id
     * @param id 终端id映射uuid
     * @return 任务项对象
     */
    private TerminalUpgradeBatchTaskItem buildAppendTaskItem(final UUID upgradeTaskId, UUID id) {
        return new TerminalUpgradeBatchTaskItem(id,
                LocaleI18nResolver.resolve(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_ITEM_NAME), upgradeTaskId);
    }

    /**
     * 
     * 获取刷机任务列表
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "list")
    public DefaultWebResponse list(PageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        PageSearchRequest apiRequest = new PageSearchRequest(request);
        convertListTaskMatchEqual(apiRequest);
        final DefaultPageResponse<CbbSystemUpgradeTaskDTO> resp =
                cbbTerminalUpgradeAPI.listSystemUpgradeTask(apiRequest);
        return DefaultWebResponse.Builder.success(resp);
    }

    private void convertListTaskMatchEqual(PageSearchRequest apiRequest) {
        apiRequest.coverMatchEqualForUUID("packageId");
        final MatchEqual[] matchEqualArr = apiRequest.getMatchEqualArr();
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            return;
        }
        for (MatchEqual me : matchEqualArr) {
            if ("upgradeTaskState".equals(me.getName())) {
                Object[] valueArr = me.getValueArr();
                CbbSystemUpgradeTaskStateEnums[] stateArr = new CbbSystemUpgradeTaskStateEnums[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    stateArr[i] = CbbSystemUpgradeTaskStateEnums.valueOf(String.valueOf(valueArr[i]));
                }
                me.setValueArr(stateArr);
            }
        }
    }

    /**
     * 
     * 获取刷机任务终端列表
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/terminal/list")
    public DefaultWebResponse listTerminal(PageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        PageSearchRequest apiRequest = new PageSearchRequest(request);
        convertListTerminalMatchEqual(apiRequest);
        final DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> resp =
                cbbTerminalUpgradeAPI.listSystemUpgradeTaskTerminal(apiRequest);
        return DefaultWebResponse.Builder.success(resp);
    }

    private void convertListTerminalMatchEqual(PageSearchRequest apiRequest) {
        apiRequest.coverMatchEqualForUUID("upgradeTaskId");
        final MatchEqual[] matchEqualArr = apiRequest.getMatchEqualArr();
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            return;
        }
        for (MatchEqual me : matchEqualArr) {
            if ("terminalUpgradeState".equals(me.getName())) {
                Object[] valueArr = me.getValueArr();
                CbbSystemUpgradeStateEnums[] stateArr = new CbbSystemUpgradeStateEnums[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    stateArr[i] = CbbSystemUpgradeStateEnums.valueOf(String.valueOf(valueArr[i]));
                }
                me.setValueArr(stateArr);
            }
        }
    }
    
    /**
     * 
     * 关闭刷机任务终端
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "close")
    public DefaultWebResponse close(CloseSystemUpgradeTaskWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbCloseSystemUpgradeTaskRequest cbbRequest = new CbbCloseSystemUpgradeTaskRequest();
        cbbRequest.setUpgradeTaskId(request.getUpgradeTaskId());
        cbbTerminalUpgradeAPI.closeSystemUpgradeTask(cbbRequest);

        return DefaultWebResponse.Builder.success();
    }
}
