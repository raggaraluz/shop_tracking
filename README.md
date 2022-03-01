# Shop Tracking

## Introduction

This is a sample project with Spring Boot web and JPA.

Internally it uses a H2 database in file to store the data persistently. It provides a command line argument to
initialize this database with random data.

## Build

The project is maven based, so it can be build with

```
./mvnw clean package
```

This will generate the executable jar in target/shop_tracking-0.0.1-SNAPSHOT.jar

## Execution

In order to initialize the database with random data, for testing purposes, the application can be launched using
the command line argument `init-db`

```
java -jar target/shop_tracking-0.0.1-SNAPSHOT.jar init-db
```

This will create a sample database with 200000 devices (some of them configured, and some not) and 200000 SIM cards, some
of them assigned to devices, others not assigned.
Once the database is initialized, the application will be running normally and accepting requests.


In subsequent executions, in order to avoid reinitializing the database, the application should be launched without
arguments:

```
java -jar target/shop_tracking-0.0.1-SNAPSHOT.jar
```

In case there is already an existing database which should be restarted, the `clear-db` argument may be used

```
java -jar target/shop_tracking-0.0.1-SNAPSHOT.jar clear-db init-db
```

## Endpoints

The following endpoints are available (with examples)

* GET - http://<ip>:8080/api/device/waiting-activation - Returns all the devices in the warehouse that are waiting for activation
  ```
  curl -s -H 'Content-Type: application/json' http://localhost:8080/api/device/waiting-activation
  ```

* DELETE - http://<ip>:8080/api/device/{id} - Management endpoint to delete the device {id}
  ```
  curl -s -v -X DELETE -H 'Content-Type: application/json' http://localhost:8080/api/device/1
  ```

* PUT - http://<ip>:8080/api/device/{id} - Management endpoint to delete the device {id}. The new device configuration is passed in
  the request body.
  ```
  DEVICEID=800003; SIMID=10199999; curl -s -X PUT -H 'Content-Type: application/json' -d '{"id": '$DEVICEID', "temperature": 20, "status": "READY", "sim": {"id": '$SIMID' } }' http://localhost:8080/api/device/$DEVICEID
  ```

* GET - http://<ip>:8080/api/device/available-for-sale - Gets an ordered result of devices available for sale. They are ordered by SIM ID
  ```
  curl -s -H 'Content-Type: application/json' http://localhost:8080/api/device/available-for-sale
  ```


## Test and Coverage

The project have some examples of Unit Tests and Integration Tests.

The current coverage is 98%. It can be checked using JaCoCo plugin reports:

```
./mvnw test jacoco:report
```

And then opening the file `target/site/jacoco/index.html`
