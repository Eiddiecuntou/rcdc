# !/usr/bin/env python
# -*- coding: utf-8 -*-
# @Time    : 2020/11/20 16:52
# @Author  : nting
# @File    : update_component_package.py

import hashlib
import json
import os
import shutil
import sys
import traceback

from terminal_bt_api import bt_server_init, start_bt_share, bt_make_seed_block, \
    stop_bt_share
from terminal_common import LOGGER, RJUpgradeException, FILE_SPERATOR, read_file, create_directory, md5sum, shell_call
from terminal_consts import RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH, UPDATELIST_RELATIVE_PATH, \
    RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, RAINOS_UPDATE_FULL_COMPONENT_TORRENT_RELATIVE_PATH, \
    RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH, DIFF_COMPONENT_SUFFIX, \
    RAINOS_UPDATE_DIFF_COMPONENT_TORRENT_RELATIVE_PATH

# 设置umask
os.umask(022)

PATH_UNDERLINE = "_"
TEMP_PATH = "/opt/upgrade/app/"
ORIGIN_DIR_NAME = "origin"
ORIGIN_COMPONENT_DIR_NAME = "component"
TEMP_DIR_NAME = "temp"
BASE_DIR_NAME = "base"
BSDIFF_CMD = "bsdiff"
TORRENT_PRE_PATH = "/opt/ftp/terminal"
COMPONENT_RELATIVE_PATH = "/component"
INSTALL_PATH = None
TORRENT_PATH = None
FULL_COMPONENT_DIR = None
DIFF_COMPONENT_DIR = None
LIMIT_FILE_SIZE = 20 * 1024 * 1024


# 入口函数
def update(os_type):
    try:
        LOGGER.info("init bt server %s" % os_type)
        bt_server_init(os_type)
        LOGGER.info("deal_with_old_package %s" % os_type)
        deal_with_old_package(os_type)
        LOGGER.info("finish init bt server %s " % os_type)
        LOGGER.info("start upgrade terminal [%s] package update..." % os_type)
        package_update(os_type)
        LOGGER.info("finish terminal [%s] package update" % os_type)
    except RJUpgradeException as rjEx:
        LOGGER.error("install failed with rj exception : %s" % rjEx.msg)
        return "fail"
    except:
        LOGGER.error("install failed")
        LOGGER.exception(traceback.format_exc())
        return "fail"

    return "success"


def set_work_mode(new_component_list):
    for component in new_component_list:
        support_work_modes = component['platform']
        component['workModeArr'] = support_work_modes.split("-")


def package_update(os_type):
    # 根据终端类型生成路径
    generate_path(os_type)
    # 根据终端类型生成Dir
    generate_dir()

    LOGGER.info("start update package...")
    # 升级包及包内updatelist路径
    origin_path = '%s%s%s' % (install_path, FILE_SPERATOR, ORIGIN_DIR_NAME)
    origin_update_list_path = '%s%s' % (origin_path, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH)
    # 临时升级包及包内updatelist路径目录
    temp_dir = '%s%s%s' % (install_path, FILE_SPERATOR, TEMP_DIR_NAME)
    src_update_list_path = '%s%s' % (temp_dir, UPDATELIST_RELATIVE_PATH)
    # 基线版本目录，即备份目录
    base_path = '%s%s%s' % (install_path, FILE_SPERATOR, BASE_DIR_NAME)

    # 判断temp目录是否存在，存在则进行更新操作，否则只进行bt种子制作和开启分享
    LOGGER.info("temp dir %s" % temp_dir)
    if not os.path.exists(temp_dir):
        LOGGER.info("temp file not exist, do not upgrade")
        if os.path.exists(origin_path):
            LOGGER.info("origin file exist, remake and start bt share")
            remake_bt_share(origin_path, origin_update_list_path)
        return

    # 获取升级包及目标安装路径update.list
    old_update_list = None
    LOGGER.info("load updatelist。 path: %s" % src_update_list_path)
    update_list_str = read_file(src_update_list_path)
    LOGGER.info("load updatelist file string : %s" % update_list_str)
    new_update_list = json.loads(update_list_str)
    if os.path.exists(origin_update_list_path):
        old_update_list = json.loads(read_file(origin_update_list_path))

    # 检验升级包版本是否相同，版本相同替换MD5不同的组件并制作种子，不同则进行升级
    old_version = None if (old_update_list is None) else old_update_list['version']
    if new_update_list['version'] == old_version:
        LOGGER.info("upgrade version is same")
        do_same_version_upgrade(old_update_list, origin_path, base_path, os_type)
        return

    # 非初始安装则更新基线版本
    if old_version is not None:
        new_update_list['baseVersion'] = old_version
    # 计算updatelist初始MD5
    new_update_list['validateMd5'] = md5_sum(src_update_list_path)

    # 创建升级临时目录
    create_update_temp_directory(temp_dir)

    # 完成新包的文件安装
    new_component_list = new_update_list['componentList']
    old_component_list = None if (old_update_list is None) else old_update_list['componentList']
    complete_update_package(origin_path, temp_dir, new_component_list, old_component_list)

    # 设置workModeArr
    set_work_mode(new_component_list)

    update_updatelist_file_content(new_update_list, temp_dir)

    # 停止原种子的bt服务（根据原updatelist获取路径）
    stop_old_bt_share(new_update_list, old_component_list)

    # 删除base,原升级包(origin)重命名为base作为基线版本备份,temp重命名为origin
    if os.path.exists(base_path):
        shutil.rmtree(base_path)
    make_bak_file(origin_path, base_path)
    os.rename(temp_dir, origin_path)

    # 遍历update.list，制作bt种子
    make_bt_seeds(origin_path, new_update_list, new_component_list)

    # 开启bt分享
    start_all_bt_share(new_update_list, new_component_list)

    # 更新updatelist文件
    update_updatelist_file_content(new_update_list, origin_path)


def update_updatelist_file_content(update_list, path):
    with open('%s%s' % (path, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH), 'w+') as update_list_file:
        update_list_file.write(json.dumps(update_list))


'''

    相同版本组件包升级：
    1、 将原升级组件包的bt服务停止，并删除该组件包，然后若存在基线版本组件包，则将其重命名origin
    2、 用新的组件包重新制作差异文件及种子

'''


def do_same_version_upgrade(old_update_list, origin_path, base_path, os_type):
    component_list = old_update_list['componentList']

    # 关闭原bt分享
    stop_old_bt_share(old_update_list, component_list)

    # 删除当前组件包，并将base包命名为origin
    if os.path.exists(origin_path):
        shutil.rmtree(origin_path)
    if os.path.exists(base_path):
        os.rename(base_path, origin_path)

    # 重新制作差异文件及种子
    package_update(os_type)


def remake_bt_share(origin_path, origin_update_list_path):
    update_list_str = read_file(origin_update_list_path)
    LOGGER.info("load updatelist file string : %s" % update_list_str)
    new_update_list = json.loads(update_list_str)
    if new_update_list is None or new_update_list['componentList'] is None:
        LOGGER.info("updatelist file not exist or content incorrect : %s" % update_list_str)
        return

    component_list = new_update_list['componentList']
    # 关闭原bt分享
    stop_old_bt_share(new_update_list, component_list)
    # 重新制作bt种子
    make_bt_seeds(origin_path, new_update_list, component_list)
    # 开启bt分享
    start_all_bt_share(new_update_list, component_list)
    # 更新updatelist文件
    with open('%s%s' % (origin_path, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH), 'w+') as update_list_file:
        update_list_file.write(json.dumps(new_update_list))


def start_all_bt_share(new_update_list, component_list):
    origin_path = '%s%s%s' % (install_path, FILE_SPERATOR, ORIGIN_DIR_NAME)
    component_path = '%s%s' % (origin_path, COMPONENT_RELATIVE_PATH)
    # 开启目录分享
    if 'componentPackageDirTorrentUrl' in new_update_list:
        LOGGER.info("TORRENT_PRE_PATH path : %s ; relative path : %s ; origin path : %s " % (
        TORRENT_PRE_PATH, new_update_list['componentPackageDirTorrentUrl'], component_path))
        start_bt_share("%s%s" % (TORRENT_PRE_PATH, new_update_list['componentPackageDirTorrentUrl']), component_path)

    for component in component_list:
        full_torrent_path = component['completeTorrentUrl'] if ('completeTorrentUrl' in component.keys()) else None
        diff_torrent_path = component['incrementalTorrentUrl'] if (
                    'incrementalTorrentUrl' in component.keys()) else None
        complete_package_name = component['completePackageName'] if (
                    'completePackageName' in component.keys()) else None
        incremental_package_name = component['incrementalPackageName'] if (
                'incrementalPackageName' in component.keys()) else None
        full_file_path = "%s%s" % (FULL_COMPONENT_DIR, complete_package_name)
        diff_file_path = "%s%s" % (DIFF_COMPONENT_DIR, incremental_package_name)
        LOGGER.info("开启bt分享，组件：%s" % complete_package_name)
        if os.path.exists(full_file_path):
            LOGGER.info("开启完整包bt分享，路径：%s" % full_file_path)
            start_bt_share("%s%s" % (TORRENT_PRE_PATH, full_torrent_path), full_file_path)

        if os.path.exists(diff_file_path):
            LOGGER.info("开启差异包bt分享，路径：%s" % diff_file_path)
            start_bt_share("%s%s" % (TORRENT_PRE_PATH, diff_torrent_path), diff_file_path)


def make_bak_file(origin_path, base_path):
    if not os.path.exists(origin_path):
        return
    os.rename(origin_path, base_path)


def complete_update_package(origin_path, temp_dir, new_component_list, old_component_list):
    # 获取updatelist组件信息,遍历判断，版本不同的则进行差异包制作
    # 差异包制作，调用shell命令，服务器系统要装bsdiff, 生成差异包指令 bsdiff oldfile newfile patchfile
    for newComp in new_component_list:
        new_version = newComp['version']
        new_component_md5 = newComp['md5']
        component_name = newComp['name']

        component_file_name = newComp['completePackageName']
        LOGGER.info('deal with component : %s' % component_name)
        # 判断组件是否需要制作差异包，是则调用指令制作差异包并更新updatelist信息
        old_version = None
        old_component_file_name = None
        if old_component_list is not None:
            old_version, old_component_file_name, old_component_file_md5 = need_make_diff_file(origin_path,
                                                                                               component_name,
                                                                                               new_version,
                                                                                               new_component_md5,
                                                                                               old_component_list)

        if old_version is not None:
            LOGGER.info("start make component[%s] vesion[%s] to version[%s] diff file" % (
                component_name, old_version, new_version))
            patch_file_name, md5 = do_bsdiff(origin_path, temp_dir, component_name, component_file_name,
                                             old_component_file_name)
            newComp['incrementalPackageName'] = patch_file_name
            newComp['incrementalPackageMd5'] = md5
            newComp['basePackageName'] = old_component_file_name
            newComp['basePackageMd5'] = old_component_file_md5


def need_make_diff_file(origin_path, component_name, new_version, new_component_md5, old_component_list):
    for old_comp in old_component_list:
        # 组件名不同跳过
        if component_name != old_comp['name']:
            continue

        # 组件包制作差异bsdiff非常消耗内存，最大可能达到17倍文件大小
        # 因此限制制作差异的文件大小，超过的不制作差异
        old_file_path = '%s%s%s%s' % (
            origin_path, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, old_comp['completePackageName'])
        file_size = get_file_size(old_file_path)
        if file_size > LIMIT_FILE_SIZE:
            LOGGER.info("component[%s] file size is [%s], big than [%s],skip make diff file" % (
                component_name, file_size, LIMIT_FILE_SIZE))
            return None, None, None

        # 版本不相同则需制作差异包
        old_version = old_comp['version']
        if new_version != old_version:
            return old_version, old_comp['completePackageName'], old_comp['md5']

            # 版本相同，MD5不同也需制作差异包
        old_component_md5 = old_comp['md5']
        if new_component_md5 != old_component_md5:
            return old_version, old_comp['completePackageName'], old_comp['md5']

    return None, None, None


def get_file_size(file_path):
    return os.path.getsize(file_path)


def get_ftp_relative_path(target_torrent_path):
    return target_torrent_path.replace(TORRENT_PRE_PATH, "")


def make_torrent_dir():
    full_seed_save_path = '%s%s' % (torrent_path, RAINOS_UPDATE_FULL_COMPONENT_TORRENT_RELATIVE_PATH)
    diff_seed_save_path = '%s%s' % (torrent_path, RAINOS_UPDATE_DIFF_COMPONENT_TORRENT_RELATIVE_PATH)
    create_directory(full_seed_save_path)
    create_directory(diff_seed_save_path)
    return full_seed_save_path, diff_seed_save_path


def get_relative_path(full_path, target_path):
    return full_path.replace(target_path, "")


def make_bt_seeds(origin_path, new_update_list, component_list):
    LOGGER.info("start batch make bt seed...")
    full_seed_save_path, diff_seed_save_path = make_torrent_dir()

    # 获取参数ip
    ip = sys.argv[1]
    LOGGER.info("ip : %s" % ip)

    # 目录制种
    component_path = '%s%s' % (origin_path, COMPONENT_RELATIVE_PATH)
    LOGGER.info("component path : %s ; torrent_path : %s" % (component_path, torrent_path))
    dir_torrent_url = bt_make_seed_block(component_path, torrent_path, ip)
    new_update_list['componentPackageDirName'] = ORIGIN_COMPONENT_DIR_NAME
    new_update_list['componentPackageDirTorrentUrl'] = get_ftp_relative_path(dir_torrent_url)
    new_update_list['componentPackageDirTorrentMd5'] = md5sum(dir_torrent_url)

    for component in component_list:
        file_name = component['completePackageName']
        diff_file_name = component['incrementalPackageName'] if ('incrementalPackageName' in component.keys()) else None
        full_path = '%s%s%s%s' % (origin_path, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, file_name)
        diff_path = '%s%s%s%s' % (
            origin_path, RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, diff_file_name)
        LOGGER.info(full_path)
        LOGGER.info(diff_path)
        if not os.path.exists(full_path):
            raise Exception("make file bt seed failed, file not exist, file path : " + full_path)

        complete_torrent_url = bt_make_seed_block(full_path, full_seed_save_path, ip)
        component['completeTorrentUrl'] = get_ftp_relative_path(complete_torrent_url)
        component['completeTorrentMd5'] = md5sum(complete_torrent_url)
        component['completePackageNameRelativePath'] = get_relative_path(full_path, origin_path)

        if diff_file_name is not None or os.path.exists(diff_path):
            incremental_torrent_url = bt_make_seed_block(diff_path, diff_seed_save_path, ip)
            component['incrementalTorrentUrl'] = get_ftp_relative_path(incremental_torrent_url)
            component['incrementalTorrentMd5'] = md5sum(incremental_torrent_url)
            component['incrementalPackageRelativePath'] = get_relative_path(diff_path, origin_path)

    LOGGER.info("finish batch make bt seed")


def stop_old_bt_share(new_update_list, component_list):
    LOGGER.info("start batch stop old bt share...")

    # 停止目录分享
    if 'componentPackageDirTorrentUrl' in new_update_list:
        stop_bt_share("%s%s" % (TORRENT_PRE_PATH, new_update_list['componentPackageDirTorrentUrl']))

    if component_list is None:
        LOGGER.info("component list is none, no stop bt share task")
        return
    for component in component_list:
        full_torrent_path = component['completeTorrentUrl'] if ('completeTorrentUrl' in component.keys()) else None
        diff_torrent_path = component['incrementalTorrentUrl'] if (
                'incrementalTorrentUrl' in component.keys()) else None
        stop_bt_share("%s%s" % (TORRENT_PRE_PATH, full_torrent_path))
        stop_bt_share("%s%s" % (TORRENT_PRE_PATH, diff_torrent_path))
    LOGGER.info("finish batch stop bt share")


def do_bsdiff(origin_path, temp_dir, component_name, component_file_name, old_component_file_name):
    new_file_path = '%s%s%s%s' % (
        temp_dir, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, component_file_name)
    old_file_path = '%s%s%s%s' % (
        origin_path, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, old_component_file_name)
    patch_file_name = '%s%s' % (component_name, DIFF_COMPONENT_SUFFIX)
    patch_file_path = '%s%s%s%s' % (
        temp_dir, RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH, FILE_SPERATOR, patch_file_name)
    shell_cmd = "%s %s %s %s" % (BSDIFF_CMD, old_file_path, new_file_path, patch_file_path)
    shell_call(shell_cmd)
    md5 = md5sum(patch_file_path)
    return patch_file_name, md5


def create_update_temp_directory(dir_path):
    full_temp_dir = '%s%s' % (dir_path, RAINOS_UPDATE_FULL_COMPONENT_RELATIVE_PATH)
    diff_temp_dir = '%s%s' % (dir_path, RAINOS_UPDATE_DIFF_COMPONENT_RELATIVE_PATH)
    LOGGER.info("create update directory : full_temp_dir[%s], diff_temp_dir[%s]"
                % (full_temp_dir, diff_temp_dir))
    create_directory(full_temp_dir)
    create_directory(diff_temp_dir)


def md5_sum(target_file):
    md5_value = hashlib.md5()
    with open(target_file, 'rb') as f:
        while True:
            data_flow = f.read(4096)
            if not data_flow:
                break
            md5_value.update(data_flow)
    f.close()
    return md5_value.hexdigest()


'''
    根据终端类型生成路径
'''


def generate_path(os_type):
    global install_path, torrent_path
    install_path_prefix = "/opt/upgrade/app/terminal_component/terminal"
    install_path = '%s%s%s' % (install_path_prefix, PATH_UNDERLINE, os_type)

    torrent_path_prefix = "/opt/ftp/terminal/terminal_component/"
    torrent_path_suffix = "/torrent"
    torrent_path = '%s%s%s' % (torrent_path_prefix, os_type, torrent_path_suffix)


'''
    # 根据终端类型生成Dir
'''


def generate_dir():
    global FULL_COMPONENT_DIR, DIFF_COMPONENT_DIR, install_path
    full_component_dir_suffix = "/origin/component/full/"
    FULL_COMPONENT_DIR = '%s%s' % (install_path, full_component_dir_suffix)
    diff_component_dir_suffix = "/origin/component/diff/"
    DIFF_COMPONENT_DIR = '%s%s' % (install_path, diff_component_dir_suffix)


def stop_and_remove_package(old_package_path, share_file_path):
    old_origin_dir = '%s%s%s' % (old_package_path, FILE_SPERATOR, ORIGIN_DIR_NAME)
    LOGGER.info("old_origin_dir: %s" % old_origin_dir)
    if not os.path.exists(old_origin_dir):
        LOGGER.info("old package not exit, skip")
        return

    old_update_list_path = '%s%s' % (old_origin_dir, RAINOS_UPDATE_UPDATE_LIST_RELATIVE_PATH)
    LOGGER.info("old_update_list_path: %s" % old_update_list_path)
    old_update_list = json.loads(read_file(old_update_list_path))
    old_component_list = None if (old_update_list is None) else old_update_list['componentList']
    LOGGER.info("stop old package bt share")
    try:
        stop_old_bt_share(old_update_list, old_component_list)
    except:
        LOGGER.error("stop bt share failed")
        LOGGER.exception(traceback.format_exc())
    LOGGER.info("delete old package")
    shutil.rmtree(old_package_path)
    LOGGER.info("delete old package bt share file")
    shutil.rmtree(share_file_path)


def deal_with_old_package(package_os_type):
    try:
        old_component_path_pre = "/opt/upgrade/app/terminal_component/terminal"
        torrent_path_prefix = "/opt/ftp/terminal/terminal_component/"
        if package_os_type == 'linux':
            stop_and_remove_package('%s%s%s' % (old_component_path_pre, '_vdi_', package_os_type),
                                    '%s%s%s' % (torrent_path_prefix, package_os_type, '_vdi'))
            stop_and_remove_package('%s%s%s' % (old_component_path_pre, '_idv_', package_os_type),
                                    '%s%s%s' % (torrent_path_prefix, package_os_type, '_idv'))
        if package_os_type == 'android':
            stop_and_remove_package('%s%s%s' % (old_component_path_pre, '_vdi_', package_os_type),
                                    '%s%s%s' % (torrent_path_prefix, package_os_type, '_vdi'))
    except:
        LOGGER.error("deal with old package failed，package_os_type ： %s" % package_os_type)
        LOGGER.exception(traceback.format_exc())


if __name__ == '__main__':

    # # 校验是否传递参数
    if len(sys.argv) < 3:
        LOGGER.info("param can not be null")
        print "fail"
    else:
        os_type = sys.argv[2]
        result = update(os_type)
        LOGGER.info("update result : %s" % result)
        print result
