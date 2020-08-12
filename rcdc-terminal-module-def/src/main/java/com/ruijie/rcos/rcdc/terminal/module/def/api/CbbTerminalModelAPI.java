package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
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
     * @apiParam (请求体字段说明) {CbbTerminalPlatformEnums[]="VDI","IDV","APP","PC"} platformArr platformArr
     *
     * @apiSuccess (响应字段说明) {CbbTerminalModelDTO[]} result CbbTerminalModelDTO[]
     * @apiSuccess (响应字段说明) {String} result.productModel 型号
     * @apiSuccess (响应字段说明) {String} result.productId 终端型号id
     * @apiSuccess (响应字段说明) {String} result.cpuType cpu类型
     */
    /**
     *  查询终端类型列表
     *
     * @param platformArr 请求参数
     * @return 终端类型列表
     */
    
    CbbTerminalModelDTO[] listTerminalModel(CbbTerminalPlatformEnums[] platformArr);

    /**
     * @api {POST} CbbTerminalModelAPI.queryByProductId 根据终端型号id查询终端型号
     * @apiName queryByProductId
     * @apiGroup CbbTerminalModelAPI
     * @apiDescription 根据终端型号id查询终端型号
     * @apiParam (请求体字段说明) {String} productId 终端型号id
     *
     * @apiSuccess (响应字段说明) {CbbTerminalModelDTO} itemArr 响应实体
     * @apiSuccess (响应字段说明) {String} itemArr.productModel 型号
     * @apiSuccess (响应字段说明) {String} itemArr.productId 终端型号id
     * @apiSuccess (响应字段说明) {String} itemArr.cpuType cpu类型
     */
    /**
     *  根据终端型号id查询终端型号
     *
     * @param productId 请求参数
     * @return 终端型号信息
     * @throws BusinessException 业务异常
     */
    CbbTerminalModelDTO queryByProductId(String productId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalModelAPI.listTerminalOsType 查询终端运行平台类型
     * @apiName listTerminalOsType
     * @apiGroup CbbTerminalModelAPI
     * @apiDescription 查询终端运行平台类型
     * @apiParam (请求体字段说明) {CbbTerminalPlatformEnums[]="VDI","IDV","APP","PC"} platformArr 终端类型数组
     *
     * @apiSuccess (响应字段说明) {List<String>} List<String> 操作系统数组
     */
    /**
     * 查询终端运行平台类型
     * @param platformArr 终端平台类型
     * @return 终端运行平台类型
     */
    List<String> listTerminalOsType(CbbTerminalPlatformEnums[] platformArr);
}
