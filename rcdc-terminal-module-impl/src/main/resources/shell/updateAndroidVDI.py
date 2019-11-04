#!/usr/bin/env python
# encoding=UTF-8
'''
Created on 2018年10月11日

@author: XiaoJiaXin
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
terminalPlatform = "vdi_android"


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
