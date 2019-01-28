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

from Common import getHostIp, getLogger, copyTo, FILE_SPERATOR
from Consts import *


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


# Init(char rcdc, null)
def btServerInit():
    reqBytes = getReqBytes("rcdc");
    getBtApi().Init(reqBytes, None)
        
'''

    制作bt种子
    
'''
def btMakeSeedBlock(path, seedSavePath):
    #调用Init
    btServerInit()
    req = BtSeedMakeRequest(getHostIp(), path, seedSavePath)
    resp = (c_char * 1024)()
    getBtApi().btMakeSeed_block(buildCallCPointerParam(req), resp, 1024)
    jsonStr = string_at(resp, 1024)
    print jsonStr
    jsonObj = checkSuccess(jsonStr)
    return jsonObj['seed_path']

# def btMakeSeedBlock(path, seedSavePath):
#     copyTo(path, seedSavePath + FILE_SPERATOR + os.path.split(path)[1] + TORRENT_SUFFIX)
#     req = BtSeedMakeRequest(getHostIp(), path, seedSavePath)
#     resp = (c_char * 1024)()
#     getBtApi().btMakeSeed_block(buildCallCPointerParam(req), resp, 1024)
#     jsonObj = json.loads(string_at(resp, 1024))
#     checkSuccess(jsonObj)
#     return seedSavePath + FILE_SPERATOR + os.path.split(path)[1] + TORRENT_SUFFIX

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
    
    btServerInit()     
    req = BtShareStopRequest(torrentPath)
    resp = (c_char * 1024)()
    getBtApi().btShareStop(buildCallCPointerParam(req), resp, 1024)
    jsonStr = string_at(resp, 1024)
    print jsonStr
    checkSpecSuccess(jsonStr)

'''

    开启bt服务
    
'''  
def startBtShare(torrentPath, fileSavePath):
    print torrentPath
    print fileSavePath
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
    
    btServerInit()  
    req = BtShareStartRequest(torrentPath, fileSavePath)
    resp = (c_char * 1024)()
    reqbytes = buildCallCPointerParam(req)
    print reqbytes
    getBtApi().btShareStart(reqbytes, resp, 1024)
    jsonStr = string_at(resp, 1024)
    print jsonStr
    checkSpecSuccess(jsonStr)


def getApi(srcPath):
    api = cdll.LoadLibrary(srcPath)
    return api

   
def getBtApi():
    logger.info("load bt service so lib")
    return getApi(BT_SERVICE_SO_PATH)

    
def checkSpecSuccess(jsonStr):
    #非成功响应，检验错误是否RJFAIL-15005，是的话认为无异常，否则异常
    if (jsonStr.find(RJ_API_RESPONSE_CODE_SUCCESS) != -1):
        checkSuccess(jsonStr)
        return
    
    if (jsonStr.find(RJ_API_RESPONSE_CODE_NO_TASK) == -1):
        logger.error("api invoke failed, code is not NO_TASK ")
        raise Exception("api invoke failed") 
    
def checkSuccess(jsonStr):
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

if __name__ == '__main__':
    logger.info("start to make bt share")
    path = sys.argv[1]
    seedSavePath = sys.argv[2]
    logger.info("param path : [%s], param seedSavePath : [%s]" %(path, seedSavePath))
    btMakeSeedBlock(path, seedSavePath)
