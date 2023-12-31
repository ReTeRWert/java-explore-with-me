DROP TABLE IF EXISTS endpoints;

CREATE TABLE IF NOT EXISTS endpoints (
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(64)                             NOT NULL,
    uri       VARCHAR(255)                            NOT NULL,
    ip        VARCHAR(32)                             NOT NULL,
    timestamp TIMESTAMP                               NOT NULL,

    CONSTRAINT pk_endpoints PRIMARY KEY (id)
);