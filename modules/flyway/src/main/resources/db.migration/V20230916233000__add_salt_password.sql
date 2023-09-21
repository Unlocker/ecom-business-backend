ALTER TABLE users ADD COLUMN salt varchar not null;
ALTER TABLE user_access_token ADD CONSTRAINT unq_user_id_on_user_token UNIQUE (user_id);