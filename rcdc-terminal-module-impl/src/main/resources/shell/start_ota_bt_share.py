#encoding=UTF-8

import sys

from BtApiService import startBtShare
from Common import getLogger

logger = getLogger()

reload(sys)
sys.setdefaultencoding("utf-8")

def start_bt_share():
    seedPath = sys.argv[1]
    packagePath = sys.argv[2]
    startBtShare(seedPath, packagePath)
    return seedPath


if __name__ == '__main__':

    if len(sys.argv) < 3:
        logger.info(" seedSavePath param is not be null")
        print "fail"
    else:
        result = start_bt_share()
        logger.info("result : %s" % result)
        print result