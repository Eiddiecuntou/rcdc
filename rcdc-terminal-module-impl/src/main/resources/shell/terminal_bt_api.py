# !/usr/bin/env python
# -*- coding: utf-8 -*-
# @Time    : 2020/11/20 16:52
# @Author  : nting
# @File    : terminal_bt_api.py

import json
import os
import sys
import traceback
from ctypes import c_char, string_at, cdll

from terminal_common import LOGGER

# bt服务so库路径
BT_SERVICE_SO_PATH = "/usr/local/lib/libsysabslayer_linux.so"

# 锐捷bt服务调用返回
RJ_API_RESPONSE_CODE_SUCCESS = "RJSUCCESS-0"
RJ_API_RESPONSE_CODE_NO_TASK = "RJFAIL-15005"


class BtSeedMakeRequest:

    def __init__(self, ipaddr, file_path, seed_save_path):
        self.ipaddr = ipaddr
        self.file_path = file_path
        self.seed_save_path = seed_save_path


class BtShareStopRequest:

    def __init__(self, seed_path):
        self.seed_path = seed_path


class BtShareStartRequest:

    def __init__(self, seed_path, file_path):
        self.seed_path = seed_path
        self.file_path = file_path


def get_api(src_path):
    return cdll.LoadLibrary(src_path)


def get_bt_api():
    LOGGER.info("load bt service so lib")
    return get_api(BT_SERVICE_SO_PATH)


def build_call_c_pointer_param(req):
    req_json_str = json.dumps(req, default=lambda o: o.__dict__, sort_keys=True, indent=4)
    return get_req_bytes(req_json_str)


def get_req_bytes(req_str):
    reqbytes = (c_char * len(req_str))()
    for i in range(0, len(req_str)):
        reqbytes[i] = req_str[i]
    return reqbytes


def get_resp_json_str(json_str):
    begin = json_str.find("{")
    end = json_str.rfind("}") + 1
    return json_str[begin:end]


api = get_bt_api()

'''

    初始化
    
'''


def bt_server_init(call_type):
    req_bytes = get_req_bytes(call_type)
    code = api.abslayer_Init(req_bytes, None)
    LOGGER.info("finish call_type[%s] bt init, response code [%s]" % (call_type, code))
    if code != 0:
        raise Exception("bt server init api init invoke failed")


'''

    制作bt种子
    
'''


def bt_make_seed_block(file_path, seed_save_path, ip):
    req = BtSeedMakeRequest(ip, file_path, seed_save_path)
    resp = (c_char * 1024)()
    api.abslayer_btMakeSeed_block(build_call_c_pointer_param(req), resp, 1024)
    json_str = string_at(resp, 1024)
    json_obj = check_success(json_str)
    return json_obj['seed_path']


'''

    关闭bt服务
    
'''


def stop_bt_share(torrent_path):
    if torrent_path is None:
        LOGGER.warn("torrent path is null")
        return
    if not (os.path.exists(torrent_path)):
        LOGGER.warn("torrent path is invalid")
        return

    req = BtShareStopRequest(torrent_path)
    resp = (c_char * 1024)()
    api.abslayer_btShareStop(build_call_c_pointer_param(req), resp, 1024)
    json_str = string_at(resp, 1024)
    check_spec_success(json_str)


'''

    开启bt服务
    
'''


def start_bt_share(torrent_path, file_save_path):
    LOGGER.info("start path[%s] bt share " % torrent_path)
    if torrent_path is None:
        LOGGER.warn("torrent path is null")
        return
    if not (os.path.exists(torrent_path)):
        LOGGER.warn("torrent path is invalid")
        return
    if not (os.path.exists(file_save_path)):
        LOGGER.warn("fileSavePath is invalid")
        return

    req = BtShareStartRequest(torrent_path, file_save_path)
    resp = (c_char * 1024)()
    req_bytes = build_call_c_pointer_param(req)
    api.abslayer_btShareStart(req_bytes, resp, 1024)
    json_str = string_at(resp, 1024)
    check_spec_success(json_str)


def check_spec_success(json_str):
    LOGGER.info("spec api invoke response: %s" % get_resp_json_str(json_str))
    # 非成功响应，检验错误是否RJFAIL-15005，是的话认为无异常，否则异常
    if json_str.find(RJ_API_RESPONSE_CODE_SUCCESS) != -1:
        check_success(json_str)
        return

    if json_str.find(RJ_API_RESPONSE_CODE_NO_TASK) == -1:
        LOGGER.error("api invoke failed, code is not NO_TASK ")
        raise Exception("api invoke failed")


def check_success(json_str):
    if json_str.find(RJ_API_RESPONSE_CODE_SUCCESS) == -1:
        LOGGER.error("api invoke failed %s", json_str)
        raise Exception("api invoke failed, code error")
    resp_str = get_resp_json_str(json_str)
    json_obj = json.loads(resp_str)
    if json_obj is None:
        LOGGER.error("json object is None, can not invoke api")
        raise Exception("can not invoke api")
    LOGGER.info("invoke api success")
    return json_obj


if __name__ == '__main__':
    LOGGER.info("start to make bt share")
    path = sys.argv[1]
    seed_save_path = sys.argv[2]
    LOGGER.info("param path : [%s], param seedSavePath : [%s]" % (path, seed_save_path))
    try:
        bt_make_seed_block(path, seed_save_path)
        LOGGER.error("make bt failed")
        print "success"
    except:
        LOGGER.error("make bt failed")
        LOGGER.exception(traceback.format_exc())
        print "fail"
