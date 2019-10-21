import sys

from BtApiService import btMakeSeedBlock, startBtShare
from Common import getLogger

logger = getLogger()

packagePath = "/opt/upgrade/ota/package/"

def del_bt():
    filePath = sys.argv[1]
    seedSavePath = sys.argv[2]
    ip = sys.argv[3]
    seedPath = btMakeSeedBlock(filePath, seedSavePath, ip)
    startBtShare(seedPath, packagePath)
    return seedPath


if __name__ == '__main__':

    if len(sys.argv) < 4:
        logger.info("ip param can not be null")
        print "fail"
    else:
        result = del_bt()
        logger.info("result : %s" % result)
        print result