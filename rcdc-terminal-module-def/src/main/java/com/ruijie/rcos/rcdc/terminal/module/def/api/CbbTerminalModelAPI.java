package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import java.util.List;


/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
public interface CbbTerminalModelAPI {

    /**
     * @api {POST} CbbTerminalModelAPI.listTerminalModel 查询终端类型列表
     * @apiName listTerminalModel
     * @apiGroup CbbTerminalModelAPI
     * @apiDescription 查询终端类型列表
     * @apiParam (请求体字段说明) {CbbTerminalPlatformEnums[]="VDI","IDV","APP","PC"} platformArr 终端类型数组
     *
     * @apiSuccess (响应字段说明) {CbbTerminalModelDTO[]} response CbbTerminalModelDTO[]
     * @apiSuccess (响应字段说明) {String} response.productModel 终端型号
     * @apiSuccess (响应字段说明) {String} response.productId 终端型号id
     * @apiSuccess (响应字段说明) {String} response.cpuType cpu类型
     *
     */
    /**
     *  查询终端类型列表
     *
     * @param platformArr 请求参数
     * @return 终端类型列表
     */
    
    CbbTerminalModelDTO[] listTerminalModel(CbbTerminalPlatformEnums[] platformArr);

    /**
     * @api {POST} CbbTerminalModelAPI.findByProductId 根据终端型号id查询终端型号
     * @apiName findByProductId
     * @apiGroup CbbTerminalModelAPI
     * @apiDescription 根据终端型号id查询终端型号
     * @apiParam (请求体字段说明) {String} productId 终端型号id
     * @apiParam (请求体字段说明) {CbbTerminalPlatformEnums} platformEnums 终端类型
     *
     * @apiSuccess (响应字段说明) {CbbTerminalModelDTO} terminalModelDTO CbbTerminalModelDTO
     * @apiSuccess (响应字段说明) {String} terminalModelDTO.productModel 终端型号
     * @apiSuccess (响应字段说明) {String} terminalModelDTO.productId 终端型号id
     * @apiSuccess (响应字段说明) {String} terminalModelDTO.cpuType cpu类型
     *
     * @apiErrorExample {json} 异常码列表
     *  {code:rcdc_terminal_model_not_exist_error message:终端型号不存在，productId：{0}}
     *
     */
    /**
     *  根据终端型号id查询终端型号
     *
     * @param productId 请求参数
     * @return 终端型号信息
     * @throws BusinessException 业务异常
     */
    CbbTerminalModelDTO findByProductIdAndPlatform(String productId, CbbTerminalPlatformEnums platformEnums) throws BusinessException;

    /**
     * @api {POST} CbbTerminalModelAPI.listTerminalOsType 查询终端运行平台类型
     * @apiName listTerminalOsType
     * @apiGroup CbbTerminalModelAPI
     * @apiDescription 查询终端运行平台类型
     * @apiParam (请求体字段说明) {CbbTerminalPlatformEnums[]="VDI","IDV","APP","PC"} platformArr 终端类型数组
     *
     * @apiSuccess (响应字段说明) {List} terminalOsType 操作系统类型列表
     *
     */
    /**
     * 查询终端运行平台类型
     * @param platformArr 终端平台类型
     * @return 终端运行平台类型
     */
    List<String> listTerminalOsType(CbbTerminalPlatformEnums[] platformArr);
}
