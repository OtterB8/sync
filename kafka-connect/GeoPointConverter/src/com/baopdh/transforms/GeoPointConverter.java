/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.transforms;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.Requirements;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

/**
 *
 * @author baopdh
 */
public class GeoPointConverter<R extends ConnectRecord<R>> implements Transformation<R> {
    public static final String OVERVIEW_DOC = "Convert 2 latitude and longitude fields to 1 location field";

    private interface ConfigName {
        String FIELD_LAT = "field.lat";
        String FIELD_LON = "field.lon";
        String FIELD_DROP = "field.drop";
        String FIELD_NEW = "field.new";
    }
    
    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(ConfigName.FIELD_LAT, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "Field name to extract latitude")
            .define(ConfigName.FIELD_LON, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "Field name to extract longitude")
            .define(ConfigName.FIELD_DROP, ConfigDef.Type.BOOLEAN, false, ConfigDef.Importance.LOW, "Drop 2 above fields or not")
            .define(ConfigName.FIELD_NEW, ConfigDef.Type.STRING, "location", ConfigDef.Importance.HIGH, "New field name to add");
    
    private static final String PURPOSE = "convert geo points";
    
    private String latField;
    private String lonField;
    private boolean isDropFields;
    private String newFieldName;
    
    private Cache<Schema, Schema> schemaUpdateCache;
   
    @Override
    public void configure(Map<String, ?> map) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, map);
        latField = config.getString(ConfigName.FIELD_LAT);
        lonField = config.getString(ConfigName.FIELD_LON);
        isDropFields = config.getBoolean(ConfigName.FIELD_DROP);
        newFieldName = config.getString(ConfigName.FIELD_NEW);
        
        schemaUpdateCache = new SynchronizedCache<>(new LRUCache<>(16));
    }
    
    @Override
    public R apply(R record) {
        if (operatingValue(record) == null) {
            return null;
        } else if (operatingSchema(record) == null) {
            return applySchemaLess(record);
        } else {
            return applyWithSchema(record);
        }
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
    
    @Override
    public void close() {
        schemaUpdateCache = null;
    }
    
    private R newRecord(R record, Schema updatedSchema, Object updatedValue) {
        return record.newRecord(
                record.topic(), record.kafkaPartition(),
                record.keySchema(), record.key(),
                updatedSchema, updatedValue, record.timestamp());
    }
    
    private Object operatingSchema(R record) {
        return record.valueSchema();
    }
    
    private Object operatingValue(R record) {
        return record.value();
    }
    
    private R applySchemaLess(R record) {
        final Map<String, Object> value = Requirements.requireMapOrNull(operatingValue(record), PURPOSE);
        
        final Map<String, Object> updatedValue = new HashMap<>(value);
        try {
//            double lat = Double.valueOf((String)updatedValue.get(latField));
//            double lon = Double.valueOf((String)updatedValue.get(lonField));
//            
//            updatedValue.put(newFieldName, String.format("%f,%f", lat, lon));
            updatedValue.put(newFieldName, "Simple Test");
            
            if (isDropFields) {
                updatedValue.remove(latField);
                updatedValue.remove(lonField);
            }
            
            return newRecord(record, null, updatedValue);
        } catch (NumberFormatException nfe) {
            return record;
        }
    }
    
    private R applyWithSchema(R record) {
        final Struct value = Requirements.requireStruct(operatingValue(record), PURPOSE);

        Schema updatedSchema = schemaUpdateCache.get(value.schema());
        if (updatedSchema == null) {
            updatedSchema = makeUpdatedSchema(value.schema());
            schemaUpdateCache.put(value.schema(), updatedSchema);
        }
        
        final Struct updatedValue = new Struct(updatedSchema);
        updatedSchema.fields().stream().forEach(field -> {
            try {
                updatedValue.put(field, value.get(field));
            } catch (Exception e) {} //ignore if field not exist
        });
        
        try {
//            double lat = Double.valueOf((String)value.get(latField));
//            double lon = Double.valueOf((String)value.get(lonField));
//            
//            updatedValue.put(newFieldName, String.format("%f,%f", lat, lon));
            String s1 = (String) value.get(latField);
            String s2 = (String) value.get(lonField);
            updatedValue.put(newFieldName, s1 + "," + s2);
            
            return newRecord(record, updatedSchema, updatedValue);
        } catch (NumberFormatException nfe) {
            return record;
        }
    }
    
    private Schema makeUpdatedSchema(Schema schema) {
        final SchemaBuilder builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());
        
        if (isDropFields) {
            schema.fields().stream().forEach(field -> {
                if (field.name().equals(latField) || field.name().equals(lonField))
                    return;
                builder.field(field.name(), field.schema());
            });
        } else {
            schema.fields().stream().forEach(field -> {
                builder.field(field.name(), field.schema());
            });
        }
        
        builder.field(newFieldName, Schema.STRING_SCHEMA);
        
        return builder.build();
    }
}