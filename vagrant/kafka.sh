#!/usr/bin/env bash

KAFKA_V="kafka_2.11-0.8.2.2"
SPARK_V="spark-1.5.1-bin-hadoop2.6"

# Run Kafka
cd ~/opt/"$KAFKA_V"

#   Start Kafka zookeeper
./bin/zookeeper-server-start.sh config/zookeeper.properties &
sleep 5
#   Start Kafka servers
./bin/kafka-server-start.sh config/server.properties &
sleep 5

#   Create Kafka topics
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic noisenoise

#   Start Kafka Producer
#./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test &

#   Start Kafka Consumer
#./bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning &
