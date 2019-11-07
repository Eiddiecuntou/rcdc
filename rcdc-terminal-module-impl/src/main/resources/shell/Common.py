#encoding=UTF-8
'''
Created on 2018年12月11日

@author: nt
'''

import hashlib
import logging
import os, shutil, socket, subprocess, zipfile
import sys
import time

from Consts import RJ_EXCEPTION_COMMON_ERROR_CODE, ZIP_SUFFIX


FILE_SPERATOR = "/"

LOGGER_PATH = "/var/log/rcdc";

LOGGER_FILE_PATH = "/var/log/rcdc/upgradeLog.log" 

'''
    创建文件夹
'''
def createDirectoty(dirPath):  
    if not os.path.exists(dirPath):
        os.makedirs(dirPath, 0755)


'''
    获取logger
'''
def getLogger():
    reload(sys)
    sys.setdefaultencoding('gbk')  # @UndefinedVariable
    logger = logging.getLogger('rj_upgrade')
    logger.setLevel(logging.DEBUG)
    if not logger.handlers:
        formatter = logging.Formatter(
            "%(asctime)s %(levelname)s %(message)s",
            "%Y-%m-%d %H:%M:%S %Z")
        console = logging.StreamHandler()
        console.setFormatter(formatter)
        logger.addHandler(console)
        #将日志写入文件
        if not os.path.exists(LOGGER_PATH):
            createDirectoty(LOGGER_PATH)
        filehandler = logging.FileHandler(LOGGER_FILE_PATH)
        filehandler.setLevel(logging.INFO)
        filehandler.setFormatter(formatter)
        logger.addHandler(filehandler)
    return logger

#日志
logger = getLogger()

if __name__ == '__main__':

    logger1 = getLogger()
    logger.info("logger1: hahahaha")

    logger2 = getLogger()
    logger.info("logger2: wawawawa")

    logger1 = getLogger()
    logger.info("logger3: xixixixi")

class RJUpgradeException(Exception):
    '''自定义异常'''
    
    def __init__(self, code, msg):
        self.code = code
        self.msg = msg
    
    
'''
        执行脚本指令
'''   
def shellCall(shellCmd):
    ret = subprocess.check_output(shellCmd, shell=True)
    return ret

'''
          压缩文件
    #path 文件或文件夹的地址
    #zipPath 将压缩后的zip保存到哪个目录下，不传则是当前目录
    #suffix 压缩后缀名，默认为zip
'''   
def createZip(path='', zipPath=''):
    compress = ['.zip', '.rar']
    if os.path.isdir(path):
        # 如果传入的是目录
        if(os.path.splitext(zipPath)[1] not in compress):
            # 以目录名作为压缩文件的名称
            dirname = os.path.split(path)[1]
            zipPath = os.path.join(zipPath, (dirname + ZIP_SUFFIX))
        newZip = zipfile.ZipFile(zipPath, 'w')
        for dirpath, dirnames, filenames in os.walk(path):
            for dirname in dirnames:
                # 这个循环是为了保证空目录也可以被压缩
                dp = filepath = os.path.join(dirpath, dirname)
                newZip.write(dp, dp[len(path):])  # 重命名
            for filename in filenames:
                filepath = os.path.join(dirpath, filename)
                newZip.write(filepath, filepath[len(path):])  # 重命名(去掉文件名前面的绝对路径）
                
    elif os.path.isfile(path):
        # 如果传入的是文件
        if(os.path.splitext(zipPath)[1] not in compress):
            filename = os.path.splitext(path)
            filename = os.path.split(filename[0])[1]
            zipPath = os.path.join(zipPath, (filename + ZIP_SUFFIX))
            
        newZip = zipfile.ZipFile(zipPath, 'w')  # 以添加模式打开压缩文件
        newZip.write(path, path[len(os.path.split(path)[0]):])  # 重命名(去掉文件名前面的绝对路径）
        
    else:
        logger.error("path[%s] is not file or directory for compress" % path)
    newZip.close()
    
'''
        解压文件
'''  
def unZip(filePath,unzipPath):
    '''
            解压zip文件到指定路径
    :param filePath: 待解压文件
    :param unzipPath: 解压路径
    :return: 
    '''
    srcfile = zipfile.ZipFile(filePath)
    srcfile.extractall(unzipPath)
    logger.info('unzip successfully to %s' %unzipPath)
   

"""
            查询本机ip地址
    :return: ip
"""
def getHostIp():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(('8.8.8.8', 80))
        ip = s.getsockname()[0]
    finally:
        s.close()

    return ip

'''
        读取文件
'''  
def readFile(filePath):
    if not os.path.exists(filePath):
        logger.warn('[%s] file not exist' % filePath)
        return None 
    
    fileObj = open(filePath, 'r')
    try:
        fileContent = fileObj.read()
        return fileContent
    finally:
        fileObj.close()

'''
        复制文件
'''       
def copyTo(srcPath, destPath):
    if not os.path.isfile(srcPath):
        logger.error('[%s] source file not exist' % srcPath)
        raise RJUpgradeException(RJ_EXCEPTION_COMMON_ERROR_CODE, "source file not exist, file path : %s" % srcPath) 
    
    fpath = os.path.split(destPath)[0]
    if not os.path.exists(fpath):
        os.makedirs(fpath)
    shutil.copyfile(srcPath, destPath)
    
def copyDirTo(srcPath, destPath):
    if not os.path.isdir(srcPath):
        logger.error('[%s] source direction not exist' % srcPath)
        raise RJUpgradeException(RJ_EXCEPTION_COMMON_ERROR_CODE, "source direction not exist, file path : %s" % srcPath) 
    if os.path.exists(destPath):
        shutil.rmtree(destPath)
    
    shutil.copytree(srcPath, destPath)
    
def md5hex(word):  
    """ MD5加密算法，返回32位小写16进制符号 """  
    if isinstance(word, unicode):  
        word = word.encode("utf-8")  
    elif not isinstance(word, str):  
        word = str(word)  
    m = hashlib.md5()  
    m.update(word)  
    return m.hexdigest()  
  
  
def md5sum(fname):  
    """ 计算文件的MD5值 """  
    def read_chunks(fh):  
        fh.seek(0)  
        chunk = fh.read(8096)  
        while chunk:  
            yield chunk  
            chunk = fh.read(8096)  
        else: #最后要将游标放回文件开头  
            fh.seek(0)  
    m = hashlib.md5()  
    if isinstance(fname, basestring) and os.path.exists(fname):  
        with open(fname, "rb") as fh:  
            for chunk in read_chunks(fh):  
                m.update(chunk)  
    #上传的文件缓存 或 已打开的文件流  
    elif fname.__class__.__name__ in ["StringIO", "StringO"] or isinstance(fname, file):  
        for chunk in read_chunks(fname):  
            m.update(chunk)  
    else:  
        return ""  
    return m.hexdigest() 
    
    