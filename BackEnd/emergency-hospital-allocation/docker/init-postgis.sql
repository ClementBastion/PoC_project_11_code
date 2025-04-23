-- init-postgis.sql
CREATE EXTENSION IF NOT EXISTS postgis;

-- ALTER TABLE hospital
--    ADD COLUMN geom geography(Point,4326);

--UPDATE hospital
--SET geom = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
--WHERE org_id <> '0';

--CREATE INDEX idx_hospital_geom
--    ON hospital
--       USING GIST (geom);