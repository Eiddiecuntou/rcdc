#!/usr/bin/env python
# encoding=UTF-8
'''
Created on 2018年10月14日

@author: nt
'''
'''
    升级包安装流程

    1.升级包(rpm)解压到升级包目录的temp目录下

    2.获取updatelist,判断是否有旧版本的升级包, 有则遍历判断，版本不同的则进行差异包制作

    3.停止原种子的bt服务（根据原updatelist获取路径）

    4.删除base,原升级包(origin)重命名为base,temp重命名为origin

    5.根据updatelist进行bt种子制作

    6.update.list更新

    #8.开启bt服务（根据新生产的updatelist获取路径）

    9.成功响应

'''
import sys
import json
import os
import shutil
import hashlib
import traceback

from BtApiService import stopBtShare, btMakeSeedBlock, startBtShare
from Common import (readFile, createZip, copyTo, createDirectoty,
    getLogger, RJUpgradeException, FILE_SPERATOR, copyDirTo, shellCall, md5sum)
from Consts import *


# 日志
logger = getLogger()

#设置umask
os.umask(022)

# 路径
tempPath = "/opt/upgrade/app/"
originDirName = "origin"
tempDirName = "temp"
baseDirName = "base"
bsdiffCmdPath = "/data/web/bsdiff-4.3/bsdiff"
torrentPrePath = "/opt/ftp/terminal"
installPath = None
torrentPath = None
fullComponentDir = None
diffComponentDir = None
rpmPackageName = None
rpmUninstallName = None

# 入口函数
def VDIUpdate(terminalPlatform):
    try:
        logger.info("start upgrade terminal vdi " + terminalPlatform + " package update...")
        packageUpdate(terminalPlatform)
        logger.info("finish terminal vdi " + terminalPlatform + " package update")
    except RJUpgradeException as rjEx:
        logger.error("install failed with rj exception : %s" % rjEx.msg)
        return "fail"
    except:
        logger.error("install failed")
        logger.exception(traceback.format_exc())
        return "fail"

    return "success"


def packageUpdate(terminalPlatform):
    # 根据终端类型生成路径
    generatePath(installPath, torrentPath, terminalPlatform)
    # 根据终端类型生成Dir
    generateDir(fullComponentDir, diffComponentDir, terminalPlatform)
    # 根据终端类型生成包名
    generatePackageName(rpmPackageName, rpmUninstallName, terminalPlatform)
    logger.info("start update package...")
    # 升级包及包内updatelist路径
    originPath = '%s%s%s' % (installPath, FILE_SPERATOR, originDirName)
    originUpdateListPath = '%s%s' % (originPath, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH)
    # 临时升级包及包内updatelist路径目录
    tempDir = '%s%s%s' % (installPath, FILE_SPERATOR, tempDirName)
    srcUpdateListPath = '%s%s' % (tempDir, UPDATELIST_RELATIVE_PATH)
    # 基线版本目录，即备份目录
    basePath = '%s%s%s' % (installPath, FILE_SPERATOR, baseDirName)

    #判断temp目录是否存在，存在则进行更新操作，否则只进行bt种子制作和开启分享
    if not os.path.exists(tempDir):
        logger.info("temp file not exist, do not upgrade")
        if(os.path.exists(originPath)):
            logger.info("origin file exist, remake and start bt share")
            remakeBtShare(originPath, originUpdateListPath)
        return;

    # 获取升级包及目标安装路径update.list
    oldUpdateList = None
    logger.info("load updatelist。 path: %s" % srcUpdateListPath)
    updatelistStr = readFile(srcUpdateListPath)
    logger.info("load updatelist file string : %s" % updatelistStr)
    newUpdateList = json.loads(updatelistStr)
    if (os.path.exists(originUpdateListPath)):
        oldUpdateList = json.loads(readFile(originUpdateListPath))

    # 检验升级包版本是否相同，版本相同替换MD5不同的组件并制作种子，不同则进行升级
    oldVersion = None if (oldUpdateList == None) else oldUpdateList['version']
    if newUpdateList['version'] == oldVersion:
        logger.info("upgrade version is same")
        doSameVersionUpgrade(oldUpdateList, originPath, basePath)
        return;

    # 非初始安装则更新基线版本
    if(oldVersion != None):
        newUpdateList['baseVersion'] = oldVersion
    # 计算updatelist初始MD5
    newUpdateList['validateMd5'] = md5Calc(srcUpdateListPath);

    # 创建升级临时目录
    createUpdateTempDirectory(tempDir)

    # 完成新包的文件安装
    newComponentList = newUpdateList['componentList']
    oldComponentList = None if (oldUpdateList == None) else oldUpdateList['componentList']
    completeUpdatePackage(originPath, tempDir, newComponentList, oldComponentList)

    # 停止原种子的bt服务（根据原updatelist获取路径）
    stopOldBtShare(oldComponentList)

    # 删除base,原升级包(origin)重命名为base作为基线版本备份,temp重命名为origin
    if os.path.exists(basePath):
        shutil.rmtree(basePath)
    makeBakFile(originPath, basePath)
    os.rename(tempDir, originPath)

    # 遍历update.list，制作bt种子
    makeBtSeeds(originPath, newComponentList)

    #开启bt分享
    startAllBtShare(newComponentList)

    # 更新updatelist文件
    with open('%s%s' % (originPath, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH), 'w+') as updatelistFile:
        updatelistFile.write(json.dumps(newUpdateList))


'''

    相同版本组件包升级：
    1、 将原升级组件包的bt服务停止，并删除该组件包，然后若存在基线版本组件包，则将其重命名origin
    2、 用新的组件包重新制作差异文件及种子

'''
def doSameVersionUpgrade(oldUpdateList, originPath, basePath):
    componentList = oldUpdateList['componentList']

    # 关闭原bt分享
    stopOldBtShare(componentList)

    # 删除当前组件包，并将base包命名为origin
    if os.path.exists(originPath):
        shutil.rmtree(originPath)
    if os.path.exists(basePath):
        os.rename(basePath, originPath)

    # 重新制作差异文件及种子
    packageUpdate()



def remakeBtShare(originPath, originUpdateListPath):
    updatelistStr = readFile(originUpdateListPath)
    logger.info("load updatelist file string : %s" % updatelistStr)
    newUpdateList = json.loads(updatelistStr)
    if newUpdateList == None or newUpdateList['componentList'] == None:
        logger.info("updatelist file not exist or content incorrect : %s" % updatelistStr)
        return;

    componentList = newUpdateList['componentList']
    #关闭原bt分享
    stopOldBtShare(componentList)
    #重新制作bt种子
    makeBtSeeds(originPath, componentList)
    #开启bt分享
    startAllBtShare(componentList)
     # 更新updatelist文件
    with open('%s%s' % (originPath, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH), 'w+') as updatelistFile:
        updatelistFile.write(json.dumps(newUpdateList))


def startAllBtShare(componentList):
    for component in componentList:
        fullTorrentPath = component['completeTorrentUrl'] if ('completeTorrentUrl' in component.keys()) else None
        diffTorrentPath = component['incrementalTorrentUrl'] if ('incrementalTorrentUrl' in component.keys()) else None
        completePackageName = component['completePackageName'] if ('completePackageName' in component.keys()) else None
        incrementalPackageName = component['incrementalPackageName'] if ('incrementalPackageName' in component.keys()) else None
        fullFilePath = "%s%s" %(fullComponentDir, completePackageName)
        diffFilePath = "%s%s" %(diffComponentDir, incrementalPackageName)
        if os.path.exists(fullFilePath):
            startBtShare("%s%s" %(torrentPrePath, fullTorrentPath), fullFilePath)
        if os.path.exists(diffFilePath):
            startBtShare("%s%s" %(torrentPrePath, diffTorrentPath), diffFilePath)



def makeBakFile(originPath, basePath):
    if not os.path.exists(originPath):
        return;
    os.rename(originPath, basePath)

def clearDir(path):
    if os.path.isdir(path):
        shutil.rmtree(path)
    os.makedirs(path)

def completeUpdatePackage(originPath, tempDir, newComponentList, oldComponentList):

    # 获取updatelist组件信息,遍历判断，版本不同的则进行差异包制作
    # 差异包制作，调用shell命令，服务器系统要装bsdiff, 生成差异包指令 bsdiff oldfile newfile patchfile
    for newComp in newComponentList:
        newVersion = newComp['version']
        newComponentMd5 = newComp['md5']
        componentName = newComp['name']

        componentFileName = newComp['completePackageName']
        logger.info('deal with component : %s' % componentName)
        # 判断组件是否需要制作差异包，是则调用指令制作差异包并更新updatelist信息
        oldVersion = None
        oldComponentFileName = None
        if oldComponentList != None:
            oldVersion, oldComponentFileName,oldComponentFileMd5 = needMakeDiffFile(componentName, newVersion, newComponentMd5, oldComponentList)

        if oldVersion != None:
            logger.info("start make component[%s] vesion[%s] to version[%s] diff file" % (componentName, oldVersion, newVersion))
            patchfileName, md5 = doBsdiff(originPath, tempDir, componentName, componentFileName, oldComponentFileName)
            newComp['incrementalPackageName'] = patchfileName
            newComp['incrementalPackageMd5'] = md5
            newComp['basePackageName'] = oldComponentFileName
            newComp['basePackageMd5'] = oldComponentFileMd5


def needMakeDiffFile(componentName, newVersion, newComponentMd5, oldComponentList):
    for oldComp in oldComponentList:
        # 组件名不同跳过
        if componentName != oldComp['name']:
            continue

        # 版本不相同则需制作差异包
        oldVersion = oldComp['version']
        if newVersion != oldVersion:
            return oldVersion,oldComp['completePackageName'],oldComp['md5']

        #版本相同，MD5不同也需制作差异包
        oldComponentMd5 = oldComp['md5']
        if newComponentMd5 != oldComponentMd5:
            return oldVersion,oldComp['completePackageName'],oldComp['md5']

    return None,None,None


def getFTPRelatePath(torrentPath):
    return torrentPath.replace(torrentPrePath, "")

def makeTorrentDir():
    clearDir(torrentPath)
    fullSeedSavePath = '%s%s' % (torrentPath, RAINOS_UPDATE_FULL_COMPONENT_TORRENT_RELATIVE_PATH)
    diffSeedSavePath = '%s%s' % (torrentPath, RAINOS_UPDATE_DIFF_COMPONENT_TORRENT_RELATIVE_PATH)
    createDirectoty(fullSeedSavePath)
    createDirectoty(diffSeedSavePath)
    return fullSeedSavePath, diffSeedSavePath

def makeBtSeeds(targetPath, componentList):
    logger.info("start batch make bt seed...")
    fullSeedSavePath, diffSeedSavePath = makeTorrentDir()

    # 获取参数ip
    ip = sys.argv[1]
    logger.info("ip : %s" % ip)

    for component in componentList:
        fileName = component['completePackageName']
        diffFileName = component['incrementalPackageName'] if ('incrementalPackageName' in component.keys()) else None
        fullPath = '%s%s%s%s' % (targetPath, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, fileName)
        diffPath = '%s%s%s%s' % (targetPath, RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, diffFileName)
        completeTorrentUrl = btMakeSeedBlock(fullPath, fullSeedSavePath, ip)
        component['completeTorrentUrl'] = getFTPRelatePath(completeTorrentUrl)
        component['completeTorrentMd5'] = md5sum(completeTorrentUrl)
        if diffFileName != None or os.path.exists(diffPath):
            incrementalTorrentUrl = btMakeSeedBlock(diffPath, diffSeedSavePath, ip)
            component['incrementalTorrentUrl'] = getFTPRelatePath(incrementalTorrentUrl)
            component['incrementalTorrentMd5'] = md5sum(incrementalTorrentUrl)

    logger.info("finish batch make bt seed")


def stopOldBtShare(componentList):
    logger.info("start batch stop old bt share...")
    if componentList == None:
        logger.info("component list is none, no stop bt share task")
        return
    for component in componentList:
        fullTorrentPath = component['completeTorrentUrl'] if ('completeTorrentUrl' in component.keys()) else None
        diffTorrentPath = component['incrementalTorrentUrl'] if ('incrementalTorrentUrl' in component.keys()) else None
        stopBtShare("%s%s" %(torrentPrePath, fullTorrentPath))
        stopBtShare("%s%s" %(torrentPrePath, diffTorrentPath))
    logger.info("finish batch stop bt share")


def doBsdiff(originPath, tempDir, componentName, componentFileName, oldComponentFileName):
    newfilePath = '%s%s%s%s' % (tempDir, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, componentFileName)
    oldfilePath = '%s%s%s%s' % (originPath, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, oldComponentFileName)
    patchfileName = '%s%s' % (componentName, DIFF_COMPONENT_SUFFIX)
    patchfilePath = '%s%s%s%s' % (tempDir, RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, patchfileName)
    shellCmd = "%s %s %s %s" % (bsdiffCmdPath, oldfilePath, newfilePath, patchfilePath)
    shellCall(shellCmd)
    md5 = md5sum(patchfilePath)
    return patchfileName, md5


def createUpdateTempDirectory(dirPath):
    fullComponentDir = '%s%s' % (dirPath, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH)
    diffComponentDir = '%s%s' % (dirPath, RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH)
    logger.info("create update directory : fullComponentDir[%s], diffComponentDir[%s]"
                 % (fullComponentDir, diffComponentDir))
    createDirectoty(fullComponentDir)
    createDirectoty(diffComponentDir)

def md5Calc(file):
    md5Value=hashlib.md5()
    with open(file,'rb') as f:
        while True:
            dataFlow=f.read(4096)
            if not dataFlow:
                break
            md5Value.update(dataFlow)
    f.close()
    return md5Value.hexdigest()

'''
    根据终端类型生成路径
'''
def generatePath(installPath, torrentPath, terminalPlatform):
    installPathPrefix = "/opt/upgrade/app/terminal_component/terminal_vdi_"
    installPath = '%s%s' % (installPathPrefix, terminalPlatform)

    torrentPathPrefix = "/opt/ftp/terminal/terminal_component/linux_"
    torrentPathsuffix = "/torrent"
    torrentPath = '%s%s%s' % (torrentPathPrefix, terminalPlatform, torrentPathPrefix)
'''
    # 根据终端类型生成Dir
'''
def generateDir(fullComponentDir, diffComponentDir, terminalPlatform):
    fullComponentDirPrefix = "/opt/upgrade/app/terminal_component/terminal_vdi_"
    fullComponentDirSuffix = "/origin/full/component/"
    fullComponentDir = '%s%s%s' % (fullComponentDirPrefix, terminalPlatform, fullComponentDirSuffix)
    diffComponentDirPrefix = "/opt/upgrade/app/terminal_component/terminal_vdi_"
    diffComponentDirSuffix = "/origin/diff/component/"
    diffComponentDir = '%s%s%s' % (diffComponentDirPrefix, terminalPlatform, diffComponentDirSuffix)
'''
    # 根据终端类型生成包名,安卓终端：
    rpmPackageName = "rcos-rco-linux-android-1.0.0.rpm"
    rpmUninstallName = "rcos-rco-linux-android"

'''
def generatePackageName(rpmPackageName, rpmUninstallName, terminalPlatform):
    rpmPackageNamePrefix = "rcos-rco-linux-"
    rpmPackageNameSuffix = "-1.0.0.rpm"
    rpmPackageName = '%s%s' % (rpmPackageNamePrefix, terminalPlatform, rpmPackageNameSuffix)
    rpmUninstallNamePrefix = "rcos-rco-linux-"
    rpmUninstallName = '%s%s' % (rpmUninstallNamePrefix, terminalPlatform)
