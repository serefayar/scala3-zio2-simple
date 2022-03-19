CREATE TABLE "customers"
(
    "id"         SERIAL,
    "first_name" VARCHAR NOT NULL,
    "last_name"  VARCHAR NOT NULL
);

INSERT INTO "customers" (first_name, last_name) 
VALUES  ('jane', 'doe'),
        ('john', 'doe');