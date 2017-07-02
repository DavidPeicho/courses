#!/usr/bin/env bash

check_arg()
{
    if [ -z "$2" ]; then
        echo "Error: argument '$1' requires a value"
    fi
}

#########################
#   ARGUMENTS PARSING   #
#########################
KAFKA_FOLDER_ARG=""
while [[ $# -ne 0 ]] ; do
    key="$1"
    case $key in
        -k|--kafka)
            KAFKA_FOLDER_ARG="$2"
            check_arg $key $KAFKA_FOLDER_ARG
            shift
        ;;
        *)
        ;;
    esac
    shift
done

#########################
#       RUNS KAFKA      #
#########################
TOPIC_NAME="movie-topic"

KAFKA_BIN="$KAFKA_FOLDER_ARG"/bin
KAFKA_CONFIG="$KAFKA_FOLDER_ARG"/config

KAFKA_STOP_SCRIPT="$KAFKA_BIN"/kafka-server-stop.sh
ZOO_STOP_SCRIPT="$KAFKA_BIN"/zookeeper-server-stop.sh

zoo_server_pid=""
kafka_server_pid=""

# Starts Zookeeper service, enabling synchronization accross clusters
"$KAFKA_BIN"/zookeeper-server-start.sh "$KAFKA_CONFIG"/zookeeper.properties &
zoo_server_pid="$!"
# Starts kafka service
"$KAFKA_BIN"/kafka-server-start.sh "$KAFKA_CONFIG"/server.properties &
kafka_server_pid="$!"
# Creates the movie topic
"$KAFKA_BIN"/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic "$TOPIC_NAME" &

finish() {
    sh $KAFKA_STOP_SCRIPT
    sleep 5
    sh $ZOO_STOP_SCRIPT
}

trap finish SIGINT SIGTERM EXIT

wait "$kafka_server_pid"
