ARG DEBEZIUM_VERSION

FROM debezium/connect:$DEBEZIUM_VERSION

COPY ./confluentinc-kafka-connect-elasticsearch-5.5.1/lib/ /kafka/connect/
COPY ./confluentinc-kafka-connect-elasticsearch-5.5.1/libs/ /kafka/libs/