#encoding=UTF-8

import sys

from BtApiService import stopBtShare
from Common import getLogger

logger = getLogger()

reload(sys)
sys.setdefaultencoding("utf-8")

def stop_bt_share():
    seedSavePath = sys.argv[1]
    stopBtShare(seedSavePath)


if __name__ == '__main__':

    if len(sys.argv) < 2:
        logger.info("seedSavePath param can not be null")
        print "fail"
        return

    try:
        result = stop_bt_share()
        logger.info("result : %s" % result)
        print result
    except:
        logger.error("stop bt failed")
        logger.exception(traceback.format_exc())
        print "fail"