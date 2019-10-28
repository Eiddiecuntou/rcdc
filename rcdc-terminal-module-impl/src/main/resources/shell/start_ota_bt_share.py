import sys

from BtApiService import startBtShare
from Common import getLogger

logger = getLogger()

packagePath = "/opt/upgrade/ota/package/"

def start_bt_share():
    seedPath = sys.argv[1]
    startBtShare(seedPath, packagePath)
    return seedPath


if __name__ == '__main__':

    if len(sys.argv) < 2:
        logger.info(" seedSavePath param is not be null")
        print "fail"
    else:
        result = start_bt_share()
        logger.info("result : %s" % result)
        print result