const axios = require('axios').default;
const profileMapping = require('./mappings/profile');
const mysqlConnectorConfig = require('./connectors/mysql');
const esConnectorConfig = require('./connectors/elasticsearch');

// Elasticsearch
const esRoot = 'http://localhost:9200/';

const esHealthApi = esRoot + '_cat/health';
const esMappingApi = esRoot + 'profile';

// Kafka connect
const kafkaConnectRoot = 'http://localhost:8083/';
const kafkaConnectRegisterApi = kafkaConnectRoot + 'connectors';

(async function() {
    function sleep(ms) {
        return new Promise((resolve) => {
          setTimeout(resolve, ms);
        });
    }
    
    async function check(what, api, interval) {
        console.log('Checking ' + what);
        try {
            await axios.get(api);
        } catch (error) {
            console.log(what + ' has not started');
            await sleep(interval);
            await check(what, api, interval);
        }
    }
    
    async function putMapping() {
        console.log('Put profile mapping');
        try {
            await axios.put(esMappingApi, profileMapping);
            console.log('Put profile mapping success');
        } catch (error) {
            console.log('Put profile mapping failed: ', error);
        }
    }
    
    async function registerConnector(name, config) {
        console.log('Registor ' + name + ' connector');
        try {
            await axios.post(kafkaConnectRegisterApi, config);
            console.log('Registor ' + name + ' success');
        } catch (error) {
            console.log('Registor ' + name + ' failed', error.message);
        }
    }
    
    await check('Elasticsearch', esHealthApi, 5000);
    await putMapping();
    
    await check('Kafka Connect', kafkaConnectRoot, 5000);
    await registerConnector('Mysql', mysqlConnectorConfig);
    await registerConnector('Elasticsearch', esConnectorConfig);
})();