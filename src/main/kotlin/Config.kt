package config

// Generic config
val NO_VALUE = "NO-VALUE"

// MongoDB Config
val MONGO_URI = System.getenv("MONGO_URI")?:"mongo-server"
val MONGO_PORT = System.getenv("MONGO_PORT")?:"27017"
val MONGO_USER = System.getenv("MONGO_USER")?:"";
val MONGO_PASS = System.getenv("MONGO_PASS")?:"";

// Vault config
val VAULT_URL = System.getenv("VAULT_URL")?:config.NO_VALUE
val VAULT_TOKEN_HEADER = "X-Vault-Token"
val VAULT_TOKEN_VALUE = System.getenv("VAULT_TOKEN")?:config.NO_VALUE

// Redis DB Config
val REDIS_CLIENT = System.getenv("REDIS_CLIENT")?:"redis.local"
val REDIS_CLIENT_KEY = "REDIS_CLIENT"
val REDIS_CLIENT_DEFAULT_EXPIRATION_TIME = System.getenv("redisKeyExpirationTime")?:"604800"
