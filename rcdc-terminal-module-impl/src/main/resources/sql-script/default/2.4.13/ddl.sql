ALTER TABLE t_cbb_terminal_authorize ADD CONSTRAINT terminal_auth_id_unique UNIQUE ( terminal_id );
ALTER TABLE t_cbb_terminal ADD CONSTRAINT terminal_id_unique UNIQUE ( terminal_id );