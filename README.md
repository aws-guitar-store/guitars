# Spring Boot Micro-Services

## Guitar Store - Guitars micro-service

### Startup

`mvn spring:boot:run`

### Shutdown

`CTRL-C`

### Configuration

#### Environment Variables

* MONGO_HOST: The hostname of the MongoDB instance to connect to (default: localhost).
* MONGO_PORT: The port that the MongoDB instance is listening on (default: 27017).
* MONGO_DATABASE: The name of the database to use (default: sbms).
* MONGO_AUTH_DATABASE: The name of the database to use for authentication (default: sbms).
* MONGO_USER: The username to connect to MongoDB with (default: sbms).
* MONGO_PASSWORD: The password to use to connect to MongoDB (default: sbms).
* KAFKA_HOST: The hostname of the Kafka instance to connect to (default: localhost).
* KAFKA_PORT: The port that the Kafka instance is listening on (default: 9092).
* KAFKA_TOPIC_GUITARS_REQUESTED: The name of the Kafka topic for the "guitars requested" event
  (default: sbms.event.guitars-requested).
