/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.transforms;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;


/**
 *
 * @author baopdh
 */
public class GeoPointConverter<R extends ConnectRecord<R>> implements Transformation<R> {

    @Override
    public void configure(Map<String, ?> map) {
        
    }
    
    @Override
    public R apply(R r) {
        return null;
    }

    @Override
    public ConfigDef config() {
        return null;
    }
    
    @Override
    public void close() {
        
    }
}