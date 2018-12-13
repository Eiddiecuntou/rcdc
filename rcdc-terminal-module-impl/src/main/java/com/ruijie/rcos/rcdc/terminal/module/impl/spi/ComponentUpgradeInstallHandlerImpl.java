package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * 
 * Description: 终端组件升级包安装处理
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月4日
 * 
 * @author nt
 */
@DispatcherImplemetion("")
public class ComponentUpgradeInstallHandlerImpl {
    
    
    /**
     * 
     *  XXXXXX改用脚本实现
     *          
     *          升级流程
     *              校验是否支持升级，不支持返回不支持响应
     *              差异包制作
     *              bt制作
     *              update.list更新
     *              升级包复制
     *              文件备份
     *              成功响应
     *          回退流程
     *              备份文件恢复
     *              
     *          ip变更通知，重新制作bt种子
     *              bt制作
     *              update.list更新
     *              
     *      
     *      升级脚本：
     *          1.升级入口， 参数 ： 各种路径 
     *          
     *          2.差异包制作模块(调用系统指令)， 参数： 两个文件路径，及差异包存放路径 
     *          
     *          3.bt种子制作模块  (调用c，动态链接库)  参数： ip, 文件路径， 种子存放路径
     *          
     *          4.update.list文件更新，文件对象,json格式数据生成,文件写入
     *          
     *          5.文件复制
     *          
     *          6.文件备份，打包压缩
     *       
     *      回退脚本：
     *          1.回退入口 参数：路径，回退版本，当前版本
     *      
     *          2.查找备份文件
     *          
     *          3.解压到升级目录
     *          
     *          4.删除当前版本文件
     *  
     *          5.重新制作bt种子(如果存在升级完成后的回退，则需要该步骤)
     *          
     *          
     * 指定路径
     *      1. 升级包上传解压路径   xxx/temp/
     *      2. 原升级包路径   xxx/history/
     *      3. 升级包备份路径   xxx/bak/
     *
     * 
     * 
     * 
     * 
     */
    
    
    
    /**
     * 升级安装
     */
    public void doInstall() {
        
        //1. 获取指定路径下包RainOS,获取组件清单信息update.list(以json格式构造，便于读取)
        
        
        //2. 判断升级包总体的版本号，与服务器上保存的终端版本号进行对比，版本号相同，则不进行安装
        
        //3. 更新base的版本号为差异包基线版本号
        
        //3. 开始逐个对升级组件版本号进行判断，版本号不同则需制作差异包， 
        
            //3.1   通过执行系统命令来制作差异包,并将差异包放入差异包文件目录，
            //3.2   通过调用bt种子制作接口制作bt种子,并将种子文件放入对应文件目录,
            //3.3   同时更新update.list,更新差异包及完整包的种子下载链接
        
        
    }
    
    
    /**
     * 升级失败回退
     */
    public void backOff() {
        
    }
    
    /**
     * 校验是否升级
     */
    public void check() {
        
    }

}
