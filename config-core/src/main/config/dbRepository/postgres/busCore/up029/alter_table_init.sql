ALTER TABLE ST_User ADD COLUMN creationDate TIMESTAMP;
ALTER TABLE ST_User ADD COLUMN saveDate TIMESTAMP;
ALTER TABLE ST_User ADD COLUMN version INT DEFAULT 0 NOT NULL;
ALTER TABLE ST_User ADD COLUMN tosAcceptanceDate TIMESTAMP;
ALTER TABLE ST_User ADD COLUMN lastLoginDate TIMESTAMP;
ALTER TABLE ST_User ADD COLUMN nbSuccessfulLoginAttempts INT DEFAULT 0 NOT NULL;
ALTER TABLE ST_User ADD COLUMN lastLoginCredentialUpdateDate TIMESTAMP;
ALTER TABLE ST_User ADD COLUMN expirationDate TIMESTAMP;
ALTER TABLE ST_User ADD COLUMN state VARCHAR(30);
ALTER TABLE ST_User ADD COLUMN stateSaveDate TIMESTAMP;

UPDATE ST_User
SET
  state = (CASE WHEN accessLevel = 'R' THEN 'DELETED'
           ELSE 'VALID' END),
  stateSaveDate = CURRENT_TIMESTAMP;

ALTER TABLE ST_User ALTER COLUMN state SET NOT NULL;
ALTER TABLE ST_User ALTER COLUMN stateSaveDate SET NOT NULL;