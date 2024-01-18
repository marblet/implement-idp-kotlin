USE kotlin_idp

CREATE TABLE clients (
    id VARCHAR(128),
    secret VARCHAR(128),
    redirect_uris TEXT NOT NULL,
    name VARCHAR(128) NOT NULL,
    created_at DATETIME default current_timestamp,
    updated_at DATETIME default current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id VARCHAR(128),
    username VARCHAR(128) unique NOT NULL,
    password VARCHAR(128) NOT NULL,
    created_at DATETIME default current_timestamp,
    updated_at DATETIME default current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE authorization_codes (
    code VARCHAR(32),
    user_id VARCHAR(128) NOT NULL,
    client_id VARCHAR(128) NOT NULL,
    scope TEXT,
    expiration DATETIME NOT NULL,
    created_at DATETIME default current_timestamp,
    PRIMARY KEY (code),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);
