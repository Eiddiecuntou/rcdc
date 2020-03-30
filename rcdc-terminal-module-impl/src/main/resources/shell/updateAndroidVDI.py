#!/usr/bin/env python
# encoding=UTF-8
'''
Created on 2018年10月11日

@author: XiaoJiaXin
'''

import os
import sys

from Common import getLogger
from CommonUpdate import update

# 日志
logger = getLogger()

# 设置umask
os.umask(022)

# 终端平台信息
terminalPlatform = "vdi"
osType = "android"

reload(sys)
sys.setdefaultencoding("utf-8")


# 入口函数
def do_update():
    result = update(terminalPlatform, osType)
    return result


if __name__ == '__main__':

    # # 校验是否传递ip参数
    if len(sys.argv) < 2:
        logger.info("ip param can not be null")
        print "fail"
    else:
        result = do_update()
        logger.info("update result : %s" % result)
        print result
