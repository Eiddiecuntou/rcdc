#!/usr/bin/env python
# encoding=UTF-8
'''
Created on 2018年12月7日

@author: nt
'''
'''
    升级包安装流程
    
    1.升级包(rpm)解压到升级包目录的temp目录下         
            
    2.获取updatelist,判断是否有旧版本的升级包, 有则遍历判断，版本不同的则进行差异包制作
                 
    3.停止原种子的bt服务（根据原updatelist获取路径）        
    
    4.删除base,原升级包(origin)重命名为base,temp重命名为origin
            
    5.根据updatelist进行bt种子制作
    
    6.update.list更新
    
    #8.开启bt服务（根据新生产的updatelist获取路径）      
    
    9.成功响应

'''
import os
import sys

from Common import getLogger
from VDICommonUpdate import VDIUpdate

# 日志
logger = getLogger()

# 设置umask
os.umask(022)

# 终端平台信息
terminalPlatform = "linux"


# 入口函数
def update():
    result = VDIUpdate(terminalPlatform)
    return result;


if __name__ == '__main__':

    # # 校验是否传递ip参数
    if len(sys.argv) < 2:
        logger.info("ip param can not be null")
        print "fail"
    else:
        result = update()
        logger.info("update result : %s" % result)
        print result
