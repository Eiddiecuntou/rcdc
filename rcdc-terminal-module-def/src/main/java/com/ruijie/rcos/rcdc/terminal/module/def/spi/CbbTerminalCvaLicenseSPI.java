package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/21 16:24
 *
 * @author yanlin
 */
public interface CbbTerminalCvaLicenseSPI {
    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.getCvaLicenseNum 获取应用虚拟化合并授权总数
     * @apiName getCvaLicenseNum
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 获取应用虚拟化合并授权总数
     *
     * @apiSuccess (响应字段说明) {Integer} cvaLicenseNum 应用虚拟化合并授权总数
     */
    /**
     * 获取应用虚拟化合并授权总数
     * @return 应用虚拟化授权总数
     */
    Integer getCvaLicenseNum();

    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.getCvaLicenseUsedNum 获取应用虚拟化合并授权使用数
     * @apiName getCvaLicenseUsedNum
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 获取应用虚拟化合并授权使用数
     *
     * @apiSuccess (响应字段说明) {Integer} cvaLicenseUsedNum 应用虚拟化合并授权使用数
     */
    /**
     * 获取应用虚拟化合并授权使用数
     * @return 应用虚拟化合并授权使用数
     */
    Integer getCvaLicenseUsedNum();

    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.canUseCvaLicense 判断IDV终端是否有应用虚拟化合并授权可以使用
     * @apiName canUseCvaLicense
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 判断IDV终端是否有应用虚拟化合并授权可以使用
     *
     * @apiSuccess (响应字段说明) {Boolean} 判断IDV终端是否有应用虚拟化合并授权可以使用
     */
    /**
     * 判断IDV终端是否有应用虚拟化合并授权可以使用
     * @return 判断结果
     */
    Boolean canUseCvaLicense();

    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.getCvaLicenseUsedNumLock 应用虚拟化授权使用数加锁
     * @apiName getCvaLicenseUsedNumLock
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 应用虚拟化授权使用数加锁
     */
    /**
     * 应用虚拟化授权使用数Lock
     * @return 加锁对象
     */
    Object getCvaLicenseUsedNumLock();

    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.getCvaLicenseNumLock 应用虚拟化授权总数加锁
     * @apiName getCvaLicenseNumLock
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 应用虚拟化授权总数加锁
     */
    /**
     * 应用虚拟化授权总数Lock
     * @return 加锁对象
     */
    Object getCvaLicenseNumLock();

    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.updateCvaLicenseNumCache 更新应用虚拟化合并授权总数缓存
     * @apiName updateCvaLicenseNumCache
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 更新应用虚拟化合并授权总数缓存
     *
     */
    /**
     * 更新应用虚拟化合并授权总数缓存
     * @param updateNum 更新的授权总数
     * @return DefaultResponse
     */
    DefaultResponse updateCvaLicenseNumCache(Integer updateNum);

    /**
     * @api {SPI} CbbTerminalCvaLicenseSPI.updateCvaLicenseUsedNumCache 更新应用虚拟化合并授权使用数缓存
     * @apiName updateCvaLicenseUsedNumCache
     * @apiGroup CbbTerminalCvaLicenseSPI
     * @apiDescription 更新应用虚拟化合并授权使用数缓存
     *
     */
    /**
     * 更新应用虚拟化合并授权使用数缓存
     * @param usedNum 更新的授权使用数
     * @return DefaultResponse
     */
    DefaultResponse updateCvaLicenseUsedNumCache(Integer usedNum);
}
