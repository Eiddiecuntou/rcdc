package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradePackageAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCancelUpgradeTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCheckAllowUploadPackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbCloseSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbDeleteTerminalUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbGetUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRetryUpgradeTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbUpgradePackageIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.PageSearchRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbAddSystemUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbCheckAllowUploadPackageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbGetTerminalUpgradeTaskResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbUpgradePackageNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.AddUpgradeTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.CancelUpgradeTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.DeleteUpgradePackageBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.RetryUpgradeTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalUpgradeBatchTaskItem;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.AppendTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CancelTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CheckPackageAllowUploadWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CloseSystemUpgradeTaskWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.CreateTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.DeleteTerminalUpgradePackageWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.RetryTerminalSystemUpgradeWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.response.CheckAllowUploadUpgradePackageWebResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.CheckAllowUploadContentVO;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.CreateSystemUpgradeTaskContentVO;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.vo.UpgradeTerminalListContentVO;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.BatchTaskSubmitResult;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItem;
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
import com.ruijie.rcos.sk.webmvc.api.response.WebResponse.Status;

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

    private static final int SYSTEM_UPGRADE_PACKAGE_NAME_MAX_LENGTH = 128;

    private static final String SYSTEM_UPGRADE_PACKAGE_ID_FIELD_NAME = "packageId";

    private static final String SYSTEM_UPGRADE_UPGRADE_TASK_STATE_FIELD_NAME = "upgradeTaskState";

    private static final String SYSTEM_UPGRADE_UPGRADE_TASK_ID_FIELD_NAME = "upgradeTaskId";

    private static final String SYSTEM_UPGRADE_TERMINAL_UPGRADE_STATE_FIELD_NAME = "terminalUpgradeState";
    
    private static final String ERROR_MSG_SPERATOR = "，";

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

        String fileName = file.getFileName();
        checkFileName(fileName);
        CbbTerminalUpgradePackageUploadRequest request =
                new CbbTerminalUpgradePackageUploadRequest(file.getFilePath(), file.getFileName(), file.getFileMD5());
        try {
            cbbTerminalUpgradePackageAPI.uploadUpgradePackage(request);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG,
                    file.getFileName());
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_SUCCESS,
                    new String[] {});
        } catch (BusinessException ex) {
            // 上传文件处理失败
            LOGGER.error("upload terminal system package fail, file name is [{}]", file.getFileName(), ex);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
                    file.getFileName(), ex.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_FAIL, new String[] {ex.getI18nMessage()});
        }
    }

    private void checkFileName(String fileName) throws BusinessException {
        if (fileName.length() > SYSTEM_UPGRADE_PACKAGE_NAME_MAX_LENGTH) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FILE_NAME_LENGTH_EXCEED,
                    String.valueOf(SYSTEM_UPGRADE_PACKAGE_NAME_MAX_LENGTH));
        }
    }
    
    /**
     * 上传系统升级文件
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/package/checkAllowUpload")
    public CheckAllowUploadUpgradePackageWebResponse checkAllowUploadPackage(CheckPackageAllowUploadWebRequest request)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbCheckAllowUploadPackageRequest checkRequest = new CbbCheckAllowUploadPackageRequest(request.getFileSize());
        CbbCheckAllowUploadPackageResponse response =
                cbbTerminalUpgradePackageAPI.checkAllowUploadPackage(checkRequest);

        CheckAllowUploadUpgradePackageWebResponse webResponse = new CheckAllowUploadUpgradePackageWebResponse();
        webResponse.setStatus(Status.SUCCESS);
        if (response.getAllowUpload()) {
            return buildSuccessResponse(webResponse);
        }
        
        return buildErrorResponse(response, webResponse);
    }

    private CheckAllowUploadUpgradePackageWebResponse buildSuccessResponse(
            CheckAllowUploadUpgradePackageWebResponse webResponse) {
        webResponse.setContent(new CheckAllowUploadContentVO(false, null));
        return webResponse;
    }

    private CheckAllowUploadUpgradePackageWebResponse buildErrorResponse(CbbCheckAllowUploadPackageResponse response,
            CheckAllowUploadUpgradePackageWebResponse webResponse) {
        final List<String> errorList = response.getErrorList();
        String errorMsg = "";
        if (!CollectionUtils.isEmpty(errorList)) {
            errorMsg = LocaleI18nResolver.resolve(BusinessKey.RCDC_PACKAGE_UPLOAD_NOT_ALLOWED, new String[] {})
                    + StringUtils.join(errorList, ERROR_MSG_SPERATOR);
        }
        webResponse.setContent(new CheckAllowUploadContentVO(true, errorMsg));
        return webResponse;
    }

    /**
     * 上传系统升级文件
     * 
     * @param request 请求参数
     * @param optLogRecorder 日志记录对象
     * @param builder 批任务对象
     * @return 上传响应返回
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/package/delete")
    public DefaultWebResponse deletePackage(DeleteTerminalUpgradePackageWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        final UUID[] packageIdArr = request.getIdArr();

        if (packageIdArr.length == 1) {
            return deleteSingleUpgradePackage(packageIdArr[0], optLogRecorder);
        } else {
            final Iterator<DefaultBatchTaskItem> iterator =
                    Stream.of(packageIdArr)
                            .map(id -> DefaultBatchTaskItem.builder().itemId(id)
                                    .itemName(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_ITEM_NAME).build())
                            .iterator();
            DeleteUpgradePackageBatchTaskHandler handler = new DeleteUpgradePackageBatchTaskHandler(
                    this.cbbTerminalUpgradePackageAPI, iterator, optLogRecorder);

            BatchTaskSubmitResult result =
                    builder.setTaskName(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_TASK_NAME, new String[] {})
                            .setTaskDesc(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_TASK_DESC, new String[] {}) //
                            .registerHandler(handler).start();

            return DefaultWebResponse.Builder.success(result);
        }
    }

    private DefaultWebResponse deleteSingleUpgradePackage(UUID packageId, ProgrammaticOptLogRecorder optLogRecorder) {
        String packageName = packageId.toString();
        try {
            packageName = getPackageName(packageId);
            CbbDeleteTerminalUpgradePackageRequest deleteRequest =
                    new CbbDeleteTerminalUpgradePackageRequest(packageId);
            cbbTerminalUpgradePackageAPI.deleteUpgradePackage(deleteRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_SUCCESS_LOG,
                    packageName);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_SUCCESS,
                    new String[] {});
        } catch (BusinessException ex) {
            LOGGER.error("delete terminal system package fail", ex);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_FAIL_LOG,
                    packageName, ex.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_DELETE_TERMINAL_UPGRADE_PACKAGE_FAIL,
                    new String[] {ex.getI18nMessage()});
        }
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
        return createUpgradeTask(packageId, terminalIdArr, optLogRecorder);
    }

    /**
     * 创建刷机任务
     * 
     * @param packageId 刷机包id
     * @param optLogRecorder 日志记录对象
     * @return 刷机任务id
     * @throws BusinessException 业务异常
     */
    private DefaultWebResponse createUpgradeTask(UUID packageId, String[] terminalIdArr, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        CbbAddSystemUpgradeTaskRequest addTaskRequest = new CbbAddSystemUpgradeTaskRequest();
        addTaskRequest.setPackageId(packageId);
        addTaskRequest.setTerminalIdArr(terminalIdArr);
        CbbAddSystemUpgradeTaskResponse response;
        String packageName = packageId.toString();
        try {
            packageName = getPackageName(packageId);
            response = cbbTerminalUpgradeAPI.addSystemUpgradeTask(addTaskRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_SUCCESS_LOG, packageName);
            CreateSystemUpgradeTaskContentVO contentVO = new CreateSystemUpgradeTaskContentVO();
            contentVO.setUpgradeTaskId(response.getUpgradeTaskId());
            return DefaultWebResponse.Builder.success(contentVO);
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_CREATE_UPGRADE_TERMINAL_TASK_FAIL_LOG, packageName, e.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_FAIL, new String[] {e.getI18nMessage()});
        }
        
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
        apiRequest.coverMatchEqualForUUID(SYSTEM_UPGRADE_PACKAGE_ID_FIELD_NAME);
        final MatchEqual[] matchEqualArr = apiRequest.getMatchEqualArr();
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            return;
        }
        for (MatchEqual me : matchEqualArr) {
            if (SYSTEM_UPGRADE_UPGRADE_TASK_STATE_FIELD_NAME.equals(me.getName())) {
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
        UUID upgradeTaskId = convertListTerminalMatchEqual(apiRequest);
        final DefaultPageResponse<CbbSystemUpgradeTaskTerminalDTO> resp =
                cbbTerminalUpgradeAPI.listSystemUpgradeTaskTerminal(apiRequest);

        final CbbGetTerminalUpgradeTaskResponse getUpgradeTaskResp =
                cbbTerminalUpgradeAPI.getTerminalUpgradeTaskById(new CbbGetUpgradeTaskRequest(upgradeTaskId));

        return DefaultWebResponse.Builder.success(
                buildUpgradeTerminalListVO(resp.getItemArr(), resp.getTotal(), getUpgradeTaskResp.getUpgradeTask()));
    }

    private UpgradeTerminalListContentVO buildUpgradeTerminalListVO(CbbSystemUpgradeTaskTerminalDTO[] itemArr,
            long total, CbbSystemUpgradeTaskDTO upgradeTask) {
        UpgradeTerminalListContentVO contentVO = new UpgradeTerminalListContentVO();
        contentVO.setItemArr(itemArr);
        contentVO.setTotal(total);
        contentVO.setUpgradeTask(upgradeTask);
        countTerminalNumByState(contentVO, itemArr);
        
        return contentVO;
    }

    private void countTerminalNumByState(UpgradeTerminalListContentVO contentVO, CbbSystemUpgradeTaskTerminalDTO[] itemArr) {
        if (itemArr == null || itemArr.length == 0) {
            return;
        }
        for (CbbSystemUpgradeTaskTerminalDTO upgradeTerminal : itemArr) {
            upgradeTerminal.getTerminalUpgradeState();
            switch (upgradeTerminal.getTerminalUpgradeState()) {
                case WAIT:
                    contentVO.setWaitNum(contentVO.getWaitNum() + 1);
                    break;
                case UPGRADING:
                    contentVO.setUpgradingNum(contentVO.getUpgradingNum() + 1);
                    break;
                case SUCCESS:
                    contentVO.setSuccessNum(contentVO.getSuccessNum() + 1);
                    break;
                case FAIL:
                    contentVO.setFailNum(contentVO.getFailNum() + 1);
                    break;
                case UNSUPPORTED:
                    contentVO.setUnsupportNum(contentVO.getUnsupportNum() + 1);
                    break;
                case UNDO:
                    contentVO.setUndoNum(contentVO.getUndoNum() + 1);
                    break;
                default:
                    break;
            }
        }
        
    }

    private UUID convertListTerminalMatchEqual(PageSearchRequest apiRequest) throws BusinessException {
        apiRequest.coverMatchEqualForUUID(SYSTEM_UPGRADE_UPGRADE_TASK_ID_FIELD_NAME);
        final MatchEqual[] matchEqualArr = apiRequest.getMatchEqualArr();
        if (ArrayUtils.isEmpty(matchEqualArr)) {
            throw new BusinessException(BusinessKey.RCDC_COMMON_REQUEST_PARAM_ERROR);
        }
        UUID upgradeTaskId = null;
        for (MatchEqual me : matchEqualArr) {
            final String name = me.getName();
            final Object[] valueArr = me.getValueArr();
            if (SYSTEM_UPGRADE_UPGRADE_TASK_ID_FIELD_NAME.equals(name) && valueArr.length > 0) {
                upgradeTaskId = UUID.fromString(String.valueOf(valueArr[0]));
            }
            if (SYSTEM_UPGRADE_TERMINAL_UPGRADE_STATE_FIELD_NAME.equals(name)) {
                CbbSystemUpgradeStateEnums[] stateArr = new CbbSystemUpgradeStateEnums[valueArr.length];
                for (int i = 0; i < valueArr.length; i++) {
                    stateArr[i] = CbbSystemUpgradeStateEnums.valueOf(String.valueOf(valueArr[i]));
                }
                me.setValueArr(stateArr);
            }
        }
        return upgradeTaskId;
    }

    /**
     * 
     * 关闭刷机任务
     * 
     * @param request 请求参数
     * @param optLogRecorder 操作日志记录对象
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "close")
    public DefaultWebResponse close(CloseSystemUpgradeTaskWebRequest request, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        CbbCloseSystemUpgradeTaskRequest cbbRequest = new CbbCloseSystemUpgradeTaskRequest();
        cbbRequest.setUpgradeTaskId(upgradeTaskId);
        CbbGetUpgradeTaskRequest getRequest = new CbbGetUpgradeTaskRequest(upgradeTaskId);
        String packageName = upgradeTaskId.toString();
        try {
            final CbbGetTerminalUpgradeTaskResponse upgradeTaskResp = cbbTerminalUpgradeAPI.getTerminalUpgradeTaskById(getRequest);
            packageName = upgradeTaskResp.getUpgradeTask().getPackageName();
            cbbTerminalUpgradeAPI.closeSystemUpgradeTask(cbbRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_UPGRADE_TERMINAL_TASK_CLOSE_SUCCESS_LOG, packageName);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_SUCCESS,
                    new String[] {});
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_UPGRADE_TERMINAL_TASK_CLOSE_FAIL_LOG, packageName,
                    e.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_FAIL, new String[] {e.getI18nMessage()});
        }
    }

    /**
     * 取消刷机任务（等待中的升级终端）
     * 
     * @param request 请求参数
     * @param optLogRecorder 日志操作对象
     * @param builder 批任务对象
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/cancel")
    public DefaultWebResponse cancel(CancelTerminalSystemUpgradeWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        String[] terminalIdArr = request.getTerminalIdArr();

        if (terminalIdArr.length == 1) {
            return cancelSingleUpgradeTerminal(terminalIdArr[0], upgradeTaskId, optLogRecorder);
        }

        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<TerminalUpgradeBatchTaskItem> iterator = Stream.of(idArr).map(
            id -> buildTerminalUpgradeItem(upgradeTaskId, BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_ITEM_NAME, id))
                .iterator();
        CancelUpgradeTerminalBatchTaskHandler handler =
                new CancelUpgradeTerminalBatchTaskHandler(this.cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);

        BatchTaskSubmitResult result =
                builder.setTaskName(BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_TASK_NAME, new String[] {})
                        .setTaskDesc(BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_TASK_DESC, new String[] {}) //
                        .registerHandler(handler).start();

        return DefaultWebResponse.Builder.success(result);
    }

    private DefaultWebResponse cancelSingleUpgradeTerminal(String terminalId, UUID upgradeTaskId,
            ProgrammaticOptLogRecorder optLogRecorder) {
        CbbCancelUpgradeTerminalRequest cancelRequest = new CbbCancelUpgradeTerminalRequest();
        cancelRequest.setTerminalId(terminalId);
        cancelRequest.setUpgradeTaskId(upgradeTaskId);
        try {
            cbbTerminalUpgradeAPI.cancelUpgradeTerminal(cancelRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_SUCCESS_LOG, terminalId);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_SUCCESS,
                    new String[] {});
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_FAIL_LOG, terminalId,
                    e.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_CANCEL_UPGRADE_TERMINAL_FAIL, new String[] {e.getI18nMessage()});
        }
    }

    /**
     * 重试（失败的升级终端）
     * 
     * @param request 请求参数
     * @param optLogRecorder 日志操作对象
     * @param builder 批任务对象
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/retry")
    public DefaultWebResponse retry(RetryTerminalSystemUpgradeWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        final UUID upgradeTaskId = request.getUpgradeTaskId();
        String[] terminalIdArr = request.getTerminalIdArr();

        if (terminalIdArr.length == 1) {
            return retrySingleUpgradeTerminal(terminalIdArr[0], upgradeTaskId, optLogRecorder);
        }

        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<TerminalUpgradeBatchTaskItem> iterator = Stream.of(idArr).map(
            id -> buildTerminalUpgradeItem(upgradeTaskId, BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_ITEM_NAME, id))
                .iterator();
        RetryUpgradeTerminalBatchTaskHandler handler =
                new RetryUpgradeTerminalBatchTaskHandler(this.cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);

        BatchTaskSubmitResult result =
                builder.setTaskName(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_TASK_NAME, new String[] {})
                        .setTaskDesc(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_TASK_DESC, new String[] {}) //
                        .registerHandler(handler).start();

        return DefaultWebResponse.Builder.success(result);
    }

    private DefaultWebResponse retrySingleUpgradeTerminal(String terminalId, UUID upgradeTaskId,
            ProgrammaticOptLogRecorder optLogRecorder) {
        CbbRetryUpgradeTerminalRequest retryRequest = new CbbRetryUpgradeTerminalRequest();
        retryRequest.setTerminalId(terminalId);
        retryRequest.setUpgradeTaskId(upgradeTaskId);
        try {
            cbbTerminalUpgradeAPI.retryUpgradeTerminal(retryRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_SUCCESS_LOG, terminalId);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_SUCCESS, new String[] {});
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_FAIL_LOG, terminalId, e.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_RETRY_UPGRADE_TERMINAL_FAIL, new String[] {e.getI18nMessage()});
        }
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
        
        if (terminalIdArr.length == 1) {
            return appendSingleTerminal(upgradeTaskId, terminalIdArr[0], optLogRecorder);
        }
        
        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<TerminalUpgradeBatchTaskItem> iterator = Stream.of(idArr)
                .map(id -> buildTerminalUpgradeItem(upgradeTaskId, BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_ITEM_NAME, id))
                .iterator();
        AddUpgradeTerminalBatchTaskHandler handler =
                new AddUpgradeTerminalBatchTaskHandler(this.cbbTerminalUpgradeAPI, idMap, iterator, optLogRecorder);

        BatchTaskSubmitResult result =
                builder.setTaskName(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_TASK_NAME, new String[] {})
                        .setTaskDesc(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_TASK_DESC, new String[] {}) //
                        .registerHandler(handler).start();

        return DefaultWebResponse.Builder.success(result);
    }

    private DefaultWebResponse appendSingleTerminal(UUID upgradeTaskId, String terminalId,
            ProgrammaticOptLogRecorder optLogRecorder) {
        CbbAddTerminalSystemUpgradeTaskRequest addRequest = new CbbAddTerminalSystemUpgradeTaskRequest();
        addRequest.setTerminalId(terminalId);
        addRequest.setUpgradeTaskId(upgradeTaskId);
        try {
            cbbTerminalUpgradeAPI.addSystemUpgradeTerminal(addRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_SUCCESS_LOG, terminalId);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_SUCCESS, new String[] {});
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_FAIL_LOG, terminalId, e.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_ADD_UPGRADE_TERMINAL_FAIL, new String[] {e.getI18nMessage()});
        }
    }

    /**
     * 构建刷机任务终端任务项
     * 
     * @param upgradeTaskId 刷机任务id
     * @param id 终端id映射uuid
     * @param businessKey 任务项key
     * @return 任务项对象
     */
    private TerminalUpgradeBatchTaskItem buildTerminalUpgradeItem(final UUID upgradeTaskId, String businessKey,
            UUID id) {
        return new TerminalUpgradeBatchTaskItem(id, LocaleI18nResolver.resolve(businessKey), upgradeTaskId);
    }

    /**
     * 
     * 获取可刷机的终端列表
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "/basicInfo/terminal/list")
    public DefaultWebResponse listTerminalBasicInfo(PageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        PageSearchRequest apiRequest = new PageSearchRequest(request);
        DefaultPageResponse<TerminalListDTO> pageResp = cbbTerminalUpgradeAPI.listUpgradeableTerminal(apiRequest);

        return DefaultWebResponse.Builder.success(pageResp);
    }
    

    private String getPackageName(UUID packageId) {
        CbbUpgradePackageIdRequest idRequest = new CbbUpgradePackageIdRequest(packageId);
        try {
            final CbbUpgradePackageNameResponse response =
                    cbbTerminalUpgradePackageAPI.getTerminalUpgradePackageName(idRequest);
            return response.getPackageName();
        } catch (BusinessException e) {
            LOGGER.info("获取升级包名称异常", e);
            return packageId.toString();
        }
    }
}
