/** 支持RG-CT7800-2000和SmartRain100S V1.00终端型号**/
INSERT INTO t_cbb_terminal_model_driver VALUES ('99b49768-bdeb-47eb-b882-8bd8e6c80a9c', 0, 'SmartRain100S', '80020091', 'ARMv7 Processor rev 0 (v7l) RK3188 CPU, @ 1.6GHz', 'VDI');
INSERT INTO t_cbb_terminal_model_driver VALUES ('672dfc4d-18d5-44ec-a525-c817cb66dd17', 0, 'RG-CT7800-2000', '80062001', 'ZHAOXIN KaiXian KX-U6780A@2.7GHz', 'IDV');

/**CT7800终端系列支持新麒麟**/
INSERT INTO t_cbb_image_template_vmmode VALUES ('11e88e98-a591-415e-bac3-7747e9236b3c', 'ZHAOXIN KaiXian KX-U6780A@2.7GHz', 'KYLIN_64', 'PASSTHROUGH', 0, 1, 'PASSTHROUGH', 'PASSTHROUGH', 'EMULATION', 'REDIRECT', 'DEFAULT', now(), now(), 'REDIRECT');
