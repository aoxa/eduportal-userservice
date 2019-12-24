CREATE KEYSPACE  IF NOT EXISTS userKeyspace with REPLICATION  = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE userKeyspace;

CREATE TABLE IF NOT EXISTS ROLES ( id uuid PRIMARY KEY, name text);

CREATE TABLE IF NOT EXISTS GROUPS ( id uuid PRIMARY KEY, name text, primaryRole uuid);

CREATE TABLE IF NOT EXISTS USERS ( id uuid PRIMARY KEY, firstName text, lastName text, email text, password text, enabled boolean, children list<uuid>);

CREATE TABLE IF NOT EXISTS GROUPS ( id uuid PRIMARY KEY, name text, primaryRole uuid);

CREATE TABLE IF NOT EXISTS GROUPBYROLE ( roleid uuid PRIMARY KEY, groupid uuid);

CREATE TABLE IF NOT EXISTS USERBYROLE ( roleid uuid PRIMARY KEY, userid uuid);

CREATE MATERIALIZED VIEW IF NOT EXISTS USERBYEMAIL AS SElECT * from USERS WHERE id is not null  AND email is not null PRIMARY KEY(email, id);

CREATE TABLE IF NOT EXISTS ROLESBYUSER ( userid uuid PRIMARY KEY, roleids set<uuid>);

CREATE TABLE IF NOT EXISTS GROUPSBYUSER ( userid uuid PRIMARY KEY, groupids set<uuid>);

CREATE TABLE IF NOT EXISTS USERSBYGROUP ( groupid uuid PRIMARY KEY, userids list<uuid>);