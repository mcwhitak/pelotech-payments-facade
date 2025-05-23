CREATE TABLE AUTHORIZATIONS
(
  id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  lvt VARCHAR NOT NULL,
  amount DECIMAL NOT NULL,
  create_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SETTLEMENTS
(
  id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  auth_id INTEGER NOT NULL,
  create_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_auth_id
    FOREIGN KEY(auth_id)
      REFERENCES AUTHORIZATIONS(id)
);
