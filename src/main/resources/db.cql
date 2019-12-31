CREATE TABLE IF NOT EXISTS ROLES ( id uuid PRIMARY KEY, name text);

CREATE TABLE IF NOT EXISTS GROUPS ( id uuid PRIMARY KEY, name text, primary_role uuid);

CREATE MATERIALIZED VIEW IF NOT EXISTS GROUP_BY_NAME AS SELECT * from GROUPS WHERE id is not null AND name is not null PRIMARY KEY(name, id);

CREATE TABLE IF NOT EXISTS USERS ( id uuid PRIMARY KEY, first_name text, last_name text, username text, email text, password text, enabled boolean, parent uuid, children list<uuid>);

CREATE MATERIALIZED VIEW IF NOT EXISTS USER_BY_EMAIL AS SELECT * from USERS WHERE id is not null  AND email is not null PRIMARY KEY(email, id);

CREATE TABLE IF NOT EXISTS USER_BY_USERNAME ( username text PRIMARY KEY, user_id uuid );

CREATE TABLE IF NOT EXISTS GROUP_BY_ROLE ( role_id uuid PRIMARY KEY, group_id uuid);

CREATE TABLE IF NOT EXISTS USERS_BY_ROLE ( role_id uuid PRIMARY KEY, user_id set<uuid>);

CREATE TABLE IF NOT EXISTS ROLES_BY_USER ( user_id uuid PRIMARY KEY, role_ids set<uuid>);

CREATE TABLE IF NOT EXISTS GROUPS_BY_USER ( user_id uuid PRIMARY KEY, group_ids set<uuid>);

CREATE TABLE IF NOT EXISTS USERS_BY_GROUP ( group_id uuid PRIMARY KEY, user_ids set<uuid>);