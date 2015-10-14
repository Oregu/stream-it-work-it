#!/usr/bin/env bash

mkdir -p ~/downloads
mkdir -p ~/opt

# Install JDK
sudo apt-get update
sudo apt-get -y install openjdk-8-jdk

KAFKA_V="kafka_2.11-0.8.2.2"
SPARK_V="spark-1.5.1-bin-hadoop2.6"

# Install Kafka
if [ ! -d ~/opt/kafka* ]; then
  wget -P ~/downloads http://apache.arvixe.com/kafka/0.8.2.2/"$KAFKA_V".tgz
  tar -xzvf ~/downloads/"$KAFKA_V".tgz -C ~/opt
fi

# Install Spark
if [ ! -d ~/opt/spark* ]; then
  wget -P ~/downloads http://apache.arvixe.com/spark/spark-1.5.1/"$SPARK_V".tgz
  tar -xzvf ~/downloads/"$SPARK_V".tgz -C ~/opt
fi
