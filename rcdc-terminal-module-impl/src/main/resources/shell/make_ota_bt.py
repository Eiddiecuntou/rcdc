#encoding=UTF-8

import sys
import json

from BtApiService import btMakeSeedBlock
from Common import getLogger, md5sum

logger = getLogger()

reload(sys)
sys.setdefaultencoding("utf-8")

class BtInfo():

    def __init__(self, seedFilePath, seedFileMD5):
        self.seedFilePath = seedFilePath
        self.seedFileMD5 = seedFileMD5

def make_bt():
    filePath = sys.argv[1]
    seedSavePath = sys.argv[2]
    ip = sys.argv[3]
    seedPath = btMakeSeedBlock(filePath, seedSavePath, ip)
    md5 = md5sum(seedPath)
    return BtInfo(seedPath, md5)


if __name__ == '__main__':

    if len(sys.argv) < 4:
        logger.info("param is not be null")
        print "fail"
        return

    try:
        result = make_bt()
        reqJsonStr = json.dumps(result, default=lambda o:o.__dict__, sort_keys=True, indent=4)
        logger.info("result : %s" % reqJsonStr)
        print reqJsonStr
    except:
        logger.error("make bt failed")
        logger.exception(traceback.format_exc())
        print "fail"



