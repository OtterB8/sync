const config = {
    "name": "profile-connect",
    "config": {
        "connector.class": "io.debezium.connector.mysql.MySqlConnector",
        "tasks.max": "1",
        "database.hostname": "localhost",
        "database.port": "3306",
        "database.user": "boba_user",
        "database.password": "Bobauserpassword@12",
        "database.server.id": "7232020",
        "database.server.name": "dbserver1",
        "database.whitelist": "boba",
        "table.whitelist": "boba.Profile",
        "database.history.kafka.bootstrap.servers": "localhost:19092",
        "database.history.kafka.topic": "schema-changes.profile",
        "snapshot.mode": "schema_only"
    }
}

module.exports = config;