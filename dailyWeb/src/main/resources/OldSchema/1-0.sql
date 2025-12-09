-- docs definition

CREATE TABLE IF NOT EXISTS "docs" ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT(10) NOT NULL, "type_id" INTEGER DEFAULT (0) NOT NULL, create_time TEXT NOT NULL, update_time TEXT NOT NULL , docs_id INTEGER NOT NULL, user_id INTEGER NOT NULL);

-- docs_item definition

CREATE TABLE IF NOT EXISTS docs_item (
                                         id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
    , "index" INTEGER NOT NULL, docs_id INTEGER NOT NULL, item_content TEXT NOT NULL, item_Field TEXT NOT NULL);

CREATE INDEX IF NOT EXISTS docs_item_index_PK__IDX ON docs_item ("index",docs_id);

-- docs_type definition

CREATE TABLE IF NOT EXISTS "docs_type" (
                                           id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                                           name TEXT NOT NULL,
                                           des TEXT ,
                                           type_id INTEGER);

CREATE INDEX IF NOT EXISTS docs_type_name_IDX ON docs_type (name);
CREATE UNIQUE INDEX IF NOT EXISTS docs_type_type_id_IDX ON docs_type (type_id);


-- "user" definition

CREATE TABLE IF NOT EXISTS "user" (
                                      id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                                      user_id INTEGER NOT NULL,
                                      oauth_userId TEXT NOT NULL,
                                      oauth_provider TEXT NOT NULL
    , created_at TEXT NOT NULL, name TEXT NOT NULL, avatar_url TEXT NOT NULL);

CREATE INDEX IF NOT EXISTS user_oauth_userId_IDX ON "user" (oauth_userId,oauth_provider);


-- user_auth definition

CREATE TABLE IF NOT EXISTS user_auth (
                                         id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
    , user_id INTEGER, docs_type_id INTEGER);

CREATE INDEX IF NOT EXISTS user_auth_user_id_IDX ON user_auth (user_id,docs_type_id);




-- dataBaseVersion definition

CREATE TABLE  IF NOT EXISTS "dataBaseVersion" (
                                                  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
    , version TEXT NOT NULL, date TEXT NOT NULL, log TEXT);