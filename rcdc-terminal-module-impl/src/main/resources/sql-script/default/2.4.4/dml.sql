-- 内置的ocs型号sn的第4、6、7位
INSERT INTO "public"."t_cbb_terminal_authorization_whitelist" VALUES ('9b5eca97-1ad2-43ed-938d-e77abea3d694','RG-OCS-128_PA9',10, now(),0);
INSERT INTO "public"."t_cbb_terminal_authorization_whitelist" VALUES ('745f2751-cc85-4f66-b265-505ac44acdaf','RG-OCS-256_PAA',10, now(),0);

-- RG-OCS-256 对应的PA9  | RG-OCS-128对应的PAA
update t_cbb_terminal_authorization_whitelist set product_type = 'RG-OCS-256_PA9' where product_type = 'RG-OCS-256_PAA';
update t_cbb_terminal_authorization_whitelist set product_type = 'RG-OCS-128_PAA' where product_type = 'RG-OCS-128_PA9';