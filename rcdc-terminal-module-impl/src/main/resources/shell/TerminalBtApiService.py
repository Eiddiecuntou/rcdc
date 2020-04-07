# encoding=UTF-8
'''
Created on 2018年12月11日
    bt服务
@author: nt
'''

from ctypes import c_char, string_at, cdll
import json
import os
import sys

from Common import getLogger
from Consts import *

# bt服务so库路径
BT_SERVICE_SO_PATH = "/usr/local/lib/libsysabslayer_linux.so"


#锐捷bt服务调用返回
RJ_API_RESPONSE_CODE = "code";
RJ_API_RESPONSE_CODE_SUCCESS = "RJSUCCESS-0";
RJ_API_RESPONSE_CODE_NO_TASK = "RJFAIL-15005";

#日志
logger = getLogger()




class BtSeedMakeRequest():

    def __init__(self, ipaddr, file_path, seed_save_path):
        self.ipaddr = ipaddr
        self.file_path = file_path
        self.seed_save_path = seed_save_path


class BtShareStopRequest():

    def __init__(self, seed_path):
        self.seed_path = seed_path


class BtShareStartRequest():

    def __init__(self, seed_path, file_path):
        self.seed_path = seed_path
        self.file_path = file_path

def getApi(srcPath):
    api = cdll.LoadLibrary(srcPath)
    return api


def getBtApi():
    logger.info("load bt service so lib")
    return getApi(BT_SERVICE_SO_PATH)

def buildCallCPointerParam(req):
    reqJsonStr = json.dumps(req, default=lambda o:o.__dict__, sort_keys=True, indent=4)
    return getReqBytes(reqJsonStr)

def getReqBytes(reqStr):
    reqbytes = (c_char * len(reqStr))()
    for i in range(0, len(reqStr)):
        reqbytes[i] = reqStr[i]
    return reqbytes

def getRespJsonStr(jsonStr):
    begin = jsonStr.find("{")
    end = jsonStr.rfind("}") + 1
    return jsonStr[begin:end]

api = getBtApi()

'''

    初始化
    
'''
def btServerInit(type):
    reqBytes = getReqBytes(type)
    code = api.abslayer_Init(reqBytes, None)
    logger.info("finish type[%s] bt init, response code [%s]" %(type, code))
    if (code != 0):
        raise Exception("api init invoke failed")

'''

    制作bt种子
    
'''
def btMakeSeedBlock(path, seedSavePath, ip):
    req = BtSeedMakeRequest(ip, path, seedSavePath)
    resp = (c_char * 1024)()
    api.abslayer_btMakeSeed_block(buildCallCPointerParam(req), resp, 1024)
    jsonStr = string_at(resp, 1024)
    jsonObj = checkSuccess(jsonStr)
    return jsonObj['seed_path']

'''

    关闭bt服务
    
'''
def stopBtShare(torrentPath):
    if torrentPath is None:
        logger.warn("torrent path is null")
        return
    if not (os.path.exists(torrentPath)):
        logger.warn("torrent path is invalid")
        return

    req = BtShareStopRequest(torrentPath)
    resp = (c_char * 1024)()
    api.abslayer_btShareStop(buildCallCPointerParam(req), resp, 1024)
    jsonStr = string_at(resp, 1024)
    checkSpecSuccess(jsonStr)

'''

    开启bt服务
    
'''
def startBtShare(torrentPath, fileSavePath):
    logger.info("start path[%s] bt share " % torrentPath)
    if torrentPath is None:
        logger.warn("torrent path is null")
        return
    if not (os.path.exists(torrentPath)):
        logger.warn("torrent path is invalid")
        return
    if not (os.path.exists(fileSavePath)):
        logger.warn("fileSavePath is invalid")
        return

    req = BtShareStartRequest(torrentPath, fileSavePath)
    resp = (c_char * 1024)()
    reqbytes = buildCallCPointerParam(req)
    api.abslayer_btShareStart(reqbytes, resp, 1024)
    jsonStr = string_at(resp, 1024)
    checkSpecSuccess(jsonStr)


def checkSpecSuccess(jsonStr):
    logger.info("spec api invoke response: %s" % getRespJsonStr(jsonStr))
    #非成功响应，检验错误是否RJFAIL-15005，是的话认为无异常，否则异常
    if (jsonStr.find(RJ_API_RESPONSE_CODE_SUCCESS) != -1):
        checkSuccess(jsonStr)
        return

    if (jsonStr.find(RJ_API_RESPONSE_CODE_NO_TASK) == -1):
        logger.error("api invoke failed, code is not NO_TASK ")
        raise Exception("api invoke failed")

def checkSuccess(jsonStr):
    logger.info("api invoke response: %s" % getRespJsonStr(jsonStr))
    if (jsonStr.find(RJ_API_RESPONSE_CODE_SUCCESS) == -1):
        raise Exception("api invoke failed, no success code")
    respStr = getRespJsonStr(jsonStr)
    jsonObj = json.loads(respStr)
    if jsonObj is None:
        logger.error("json object is None, can not invoke api")
        raise Exception("can not invoke api")
    if jsonObj[RJ_API_RESPONSE_CODE] != RJ_API_RESPONSE_CODE_SUCCESS:
        logger.error("api invoke failed %s", jsonObj[RJ_API_RESPONSE_CODE])
        raise Exception("api invoke failed, code error")
    logger.info("invoke api success")
    return jsonObj



if __name__ == '__main__':
    logger.info("start to make bt share")
    path = sys.argv[1]
    seedSavePath = sys.argv[2]
    seedSavePath = sys.argv[2]
    logger.info("param path : [%s], param seedSavePath : [%s]" %(path, seedSavePath))
    try:
        btMakeSeedBlock(path, seedSavePath)
        logger.error("make bt failed")
        print "success"
    except:
        logger.error("make bt failed")
        logger.exception(traceback.format_exc())
        print "fail"

