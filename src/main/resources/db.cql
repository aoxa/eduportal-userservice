CREATE TABLE IF NOT EXISTS ROLES ( id uuid PRIMARY KEY, name text);

CREATE TABLE IF NOT EXISTS GROUPS ( id uuid PRIMARY KEY, name text, primary_role uuid);

CREATE TABLE IF NOT EXISTS USERS ( id uuid PRIMARY KEY, first_name text, last_name text, email text, password text, enabled boolean, children list<uuid>);

CREATE MATERIALIZED VIEW IF NOT EXISTS USERBYEMAIL AS SElECT * from USERS WHERE id is not null  AND email is not null PRIMARY KEY(email, id);

CREATE TABLE IF NOT EXISTS GROUPBYROLE ( role_id uuid PRIMARY KEY, group_id uuid);

CREATE TABLE IF NOT EXISTS USERSBYROLE ( role_id uuid PRIMARY KEY, user_id set<uuid>);

CREATE TABLE IF NOT EXISTS ROLESBYUSER ( user_id uuid PRIMARY KEY, role_ids set<uuid>);

CREATE TABLE IF NOT EXISTS GROUPSBYUSER ( user_id uuid PRIMARY KEY, group_ids set<uuid>);

CREATE TABLE IF NOT EXISTS USERSBYGROUP ( group_id uuid PRIMARY KEY, user_ids set<uuid>);