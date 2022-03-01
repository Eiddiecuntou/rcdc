package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseNumDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 终端授权管理
 * <br>
 * Description: Function Description <br>
 * Copyright: Copyright (c) 2020 <br>
 * Company: Ruijie Co., Ltd. <br>
 * Create Time: 2020年9月21日 <br>
 *
 * @author jarman
 */
public interface CbbTerminalLicenseMgmtAPI {


    /**
     * @api {POST} CbbTerminalLicenseMgmtAPI.setIDVTerminalLicenseNum 设置IDV终端授权数
     * @apiName setDesktopLicenseNum
     * @apiGroup CbbLicenseMgmtAPI
     * @apiDescription 设置IDV终端授权数，限制IDV终端接入数量
     * @apiParam (请求体字段说明) {CbbTerminalLicenseTypeEnums[]="VDI","VOI","VOI_PLUS_UPGRADED"} licenseType 授权类型
     * @apiParam (请求体字段说明) {Integer} licenseNum 授权数
     *
     * @apiErrorExample {json} 异常码列表
     *                  {code:rcdc_terminal_not_allow_reduce_terminal_license_num message:终端正式授权证书个数不允许减少}
     */
    /**
     * 设置IDV终端授权数，限制IDV终端接入数量
     *
     * @param licenseType 授权类型
     * @param licenseInfoList 授权信息
     * @throws BusinessException 业务异常
     */
    void setTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, List<CbbTerminalLicenseInfoDTO> licenseInfoList) throws BusinessException;

    /**
     * @api {GET} CbbTerminalLicenseMgmtAPI.getIDVTerminalLicenseNum 获取IDV终端授权信息
     * @apiName getIDVTerminalLicenseNum
     * @apiGroup CbbTerminalLicenseMgmtAPI
     * @apiDescription 获取IDV终端授权数
     *
     * @apiParam (请求体字段说明) {CbbTerminalLicenseTypeEnums[]="VDI","VOI","VOI_PLUS_UPGRADED"} licenseType 授权类型
     *
     * @apiSuccess (响应字段说明) {CbbIDVTerminalLicenseNumDTO} response CbbIDVTerminalLicenseNumDTO
     * @apiSuccess (响应字段说明) {Integer} response.licenseNum 证书授权数
     * @apiSuccess (响应字段说明) {Integer} response.usedNum 已用授权数
     */
    /**
     * 获取终端授权信息
     *
     * @param licenseType 授权类型
     * @param licenseCodeList 证书码列表
     * @return IDV终端授权信息
     */
    CbbTerminalLicenseNumDTO getTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, @Nullable List<String> licenseCodeList);

    /**
     * @api {GET} CbbTerminalLicenseMgmtAPI.cancelTerminalAuth 取消终端授权
     * @apiName cancelTerminalAuth
     * @apiGroup CbbTerminalLicenseMgmtAPI
     * @apiDescription 取消终端授权
     *
     * @apiParam (请求体字段说明) {String} terminalId 终端id
     * @apiErrorExample {json} 异常码列表
     *                 {code:rcdc_terminal_cancel_auth_fail message:取消终端授权失败}
     */
    /**
     * 取消终端授权
     *
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void cancelTerminalAuth(String terminalId) throws BusinessException;

    /**
     * @api {GET} CbbTerminalLicenseMgmtAPI.checkEnableAuthTerminal 校验是否能够授权终端
     * @apiName checkEnableAuthTerminal
     * @apiGroup CbbTerminalLicenseMgmtAPI
     * @apiDescription 校验是否能够授权终端
     *
     * @apiParam (请求体字段说明) {String} authMode 授权模式
     */
    /**
     *  校验是否能够授权终端
     *
     * @param authMode 工作模式
     * @return Boolean 是否允许授权
     * @throws BusinessException 业务异常
     */
    Boolean checkEnableAuthTerminal(CbbTerminalPlatformEnums authMode) throws BusinessException;

    /**
     * 校验终端证书可用数量是否足够
     * @param licenseType 证书类型
     * @return 证书数量是否足够
     */
    boolean checkTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType);

    /**
     * 校验终端当前使用的授权证书是否为指定的证书类型
     * @param terminalId 终端id
     * @param licenseType 证书类型
     * @return 终端使用的是否为指定的证书类型
     */
    boolean checkTerminalCurrentLicenseType(String terminalId, CbbTerminalLicenseTypeEnums licenseType);

    /**
     * 增加终端云应用授权
     * @param terminalId 终端id
     * @throws BusinessException 业务异常
     */
    void addTerminalCvaAuth(String terminalId) throws BusinessException;
}
