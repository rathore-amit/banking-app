-- 📍 Concept: "Microservices Architecture" notebook — Database-per-service
-- Account Service aur Transaction Service, dono ke apne alag databases hain,
-- ek doosre ki tables ko kabhi directly touch nahi karte.

CREATE DATABASE account_db;
CREATE DATABASE transaction_db;
