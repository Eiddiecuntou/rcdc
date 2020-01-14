#encoding=UTF-8

import sys
import traceback

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

def main():
    if len(sys.argv) < 3:
        logger.info(" seedSavePath param is not be null")
        print "fail"
        return

    try:
        result = start_bt_share()
        logger.info("result : %s" % result)
        print result
    except:
        logger.error("start bt failed")
        logger.exception(traceback.format_exc())
        print "fail"


if __name__ == '__main__':

    main()

