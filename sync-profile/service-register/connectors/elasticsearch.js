const config = {
    "name": "es-sink-profile",
    "config": {
          "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
          "connection.url": "http://localhost:9200,http://localhost:9300,http://localhost:9400",
          "tasks.max": "1",
          "topics": "dbserver1.boba.Profile",
          "type.name": "_doc",
          "key.converter": "io.confluent.connect.avro.AvroConverter",
          "value.converter": "io.confluent.connect.avro.AvroConverter",
          "key.converter.schema.registry.url": "http://localhost:8081",
          "value.converter.schema.registry.url": "http://localhost:8081",
          "transforms": "key,value,drop,geo,indexname",
          
          "transforms.key.type": "org.apache.kafka.connect.transforms.ExtractField$Key",
          "transforms.key.field": "userid",
          
          "transforms.value.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
          "transforms.value.field": "after",

          "transforms.drop.type": "org.apache.kafka.connect.transforms.ReplaceField$Value",
          "transforms.drop.blacklist": "passwd,emotionId,soundtrackId,profileType,configId,status",

          "transforms.geo.type": "com.baopdh.transforms.GeoPointConverter",
          "transforms.geo.field.lat": "lastLat",
          "transforms.geo.field.lon": "lastLon",
          "transforms.geo.field.drop": "true",
          "transforms.geo.field.new": "lastLocation",

          "transforms.indexname.type": "org.apache.kafka.connect.transforms.RegexRouter",
          "transforms.indexname.regex": ".*",
          "transforms.indexname.replacement": "profile",

          "key.ignore": "false"
    }
}

module.exports = config;