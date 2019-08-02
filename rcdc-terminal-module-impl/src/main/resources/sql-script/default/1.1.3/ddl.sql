/** 删除可刷机终端视图(终端分组移至cbb,所以视图从rco移至cbb) */
DROP VIEW IF EXISTS v_cbb_upgradeable_terminal;

CREATE VIEW v_cbb_upgradeable_terminal AS
 SELECT t_cbb_terminal.id,
    t_cbb_terminal.terminal_name,
    t_cbb_terminal.terminal_id,
    t_cbb_terminal.mac_addr,
    t_cbb_terminal.ip,
    t_cbb_terminal.subnet_mask,
    t_cbb_terminal.gateway,
    t_cbb_terminal.main_dns,
    t_cbb_terminal.second_dns,
    t_cbb_terminal.get_ip_mode,
    t_cbb_terminal.get_dns_mode,
    t_cbb_terminal.product_type,
    t_cbb_terminal.terminal_type,
    t_cbb_terminal.serial_number,
    t_cbb_terminal.cpu_type,
    t_cbb_terminal.memory_size,
    t_cbb_terminal.disk_size,
    t_cbb_terminal.terminal_os_type,
    t_cbb_terminal.terminal_os_version,
    t_cbb_terminal.rain_os_version,
    t_cbb_terminal.rain_upgrade_version,
    t_cbb_terminal.hardware_version,
    t_cbb_terminal.network_access_mode,
    t_cbb_terminal.create_time,
    t_cbb_terminal.last_online_time,
    t_cbb_terminal.last_offline_time,
    t_cbb_terminal.version,
    t_cbb_terminal.state,
    t_cbb_terminal.platform,
    sut_suc.last_upgrade_time,
    t_cbb_terminal.group_id
   FROM (t_cbb_terminal
     LEFT JOIN ( SELECT max(sut.start_time) AS last_upgrade_time,
            sut.terminal_id
           FROM t_cbb_sys_upgrade_terminal sut
          WHERE ((sut.state)::text = 'SUCCESS'::text)
          GROUP BY sut.terminal_id) sut_suc ON (((t_cbb_terminal.terminal_id)::text = (sut_suc.terminal_id)::text)))
  WHERE (NOT ((t_cbb_terminal.terminal_id)::text IN ( SELECT t_cbb_sys_upgrade_terminal.terminal_id
           FROM t_cbb_sys_upgrade_terminal
          WHERE (t_cbb_sys_upgrade_terminal.sys_upgrade_id IN ( SELECT t_cbb_sys_upgrade.id
                   FROM t_cbb_sys_upgrade
                  WHERE ((t_cbb_sys_upgrade.state)::text = 'UPGRADING'::text))))))
  ORDER BY sut_suc.last_upgrade_time NULLS FIRST;