import sys

from BtApiService import btMakeSeedBlock
from Common import getLogger

logger = getLogger()

def make_bt():
    filePath = sys.argv[1]
    seedSavePath = sys.argv[2]
    ip = sys.argv[3]
    seedPath = btMakeSeedBlock(filePath, seedSavePath, ip)
    return seedPath


if __name__ == '__main__':

    if len(sys.argv) < 4:
        logger.info("param is not be null")
        print "fail"
    else:
        result = make_bt()
        logger.info("result : %s" % result)
        print result