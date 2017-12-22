# swagger-android-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-android-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-android-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-android-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import io.swagger.client.api.DefaultApi;

public class DefaultApiExample {

    public static void main(String[] args) {
        DefaultApi apiInstance = new DefaultApi();
        String name = "name_example"; // String | The name of the component being created
        String type = "type_example"; // String | The type of prognostics applied to the component
        String config = "config_example"; // String | Configuration information for the component's prognostic model.
        String username = "username_example"; // String | The name of the user.
        try {
            InlineResponse2005 result = apiInstance.componentCreateV1(name, type, config, username);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#componentCreateV1");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://prog.arc.nasa.gov/api*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**componentCreateV1**](docs/DefaultApi.md#componentCreateV1) | **POST** /v1/component | Creates a new component
*DefaultApi* | [**componentDetailsV1**](docs/DefaultApi.md#componentDetailsV1) | **GET** /v1/component | Gets information about the specified component. If no component_id is specified, gets an array of component ids and names that belong to the current user. 
*DefaultApi* | [**dataAddV1**](docs/DefaultApi.md#dataAddV1) | **POST** /v1/data | Adds a dataPoint for the specified component
*DefaultApi* | [**progServicesV1**](docs/DefaultApi.md#progServicesV1) | **GET** /v1/prog_services | Gets a list of prognostics services
*DefaultApi* | [**prognosticsGetV1**](docs/DefaultApi.md#prognosticsGetV1) | **GET** /v1/prognostics | Gets a new prediction for the specified object
*DefaultApi* | [**sessionEndV1**](docs/DefaultApi.md#sessionEndV1) | **DELETE** /v1/session | Ends a running prognostics session
*DefaultApi* | [**sessionStartV1**](docs/DefaultApi.md#sessionStartV1) | **POST** /v1/session | Begins a new prognostics session
*DefaultApi* | [**sessionStatusV1**](docs/DefaultApi.md#sessionStatusV1) | **GET** /v1/session | Gets the status of a running prognostics session
*DefaultApi* | [**systemAssignV1**](docs/DefaultApi.md#systemAssignV1) | **POST** /v1/system/assign | Assigns the specified component to the specified system.
*DefaultApi* | [**systemCreateV1**](docs/DefaultApi.md#systemCreateV1) | **POST** /v1/system | Creates a new system
*DefaultApi* | [**systemStatusV1**](docs/DefaultApi.md#systemStatusV1) | **GET** /v1/system | Gets information about the specified system
*DefaultApi* | [**trajectoryGetV1**](docs/DefaultApi.md#trajectoryGetV1) | **GET** /v1/trajectory | Gets trajectory points for a vehicle
*DefaultApi* | [**trajectoryRemoveV1**](docs/DefaultApi.md#trajectoryRemoveV1) | **DELETE** /v1/trajectory | Removes a trajectory point for a vehicle
*DefaultApi* | [**trajectorySetV1**](docs/DefaultApi.md#trajectorySetV1) | **POST** /v1/trajectory | Sets a trajectory point for a vehicle
*DefaultApi* | [**userInfoV1**](docs/DefaultApi.md#userInfoV1) | **GET** /v1/user | Gets information about the user
*DefaultApi* | [**vehicleCreateV1**](docs/DefaultApi.md#vehicleCreateV1) | **POST** /v1/vehicle | Creates a new vehicle
*DefaultApi* | [**vehicleStatusV1**](docs/DefaultApi.md#vehicleStatusV1) | **GET** /v1/vehicle | Gets information about the specified vehicle. If no vehicle_id is specified, gets an array of vehicle ids and names that belong to the current user. 
*DefaultApi* | [**versionDetailsV1**](docs/DefaultApi.md#versionDetailsV1) | **GET** /v1 | List version 1 API details
*DefaultApi* | [**versions**](docs/DefaultApi.md#versions) | **GET** / | List API versions


## Documentation for Models

 - [ApiResponse](docs/ApiResponse.md)
 - [Component](docs/Component.md)
 - [DataFrame](docs/DataFrame.md)
 - [DataPoint](docs/DataPoint.md)
 - [Event](docs/Event.md)
 - [IdNamePair](docs/IdNamePair.md)
 - [InlineResponse200](docs/InlineResponse200.md)
 - [InlineResponse2001](docs/InlineResponse2001.md)
 - [InlineResponse2002](docs/InlineResponse2002.md)
 - [InlineResponse2002Vehicles](docs/InlineResponse2002Vehicles.md)
 - [InlineResponse2003](docs/InlineResponse2003.md)
 - [InlineResponse2004](docs/InlineResponse2004.md)
 - [InlineResponse2005](docs/InlineResponse2005.md)
 - [InlineResponse2006](docs/InlineResponse2006.md)
 - [InlineResponse2007](docs/InlineResponse2007.md)
 - [InlineResponse2007Events](docs/InlineResponse2007Events.md)
 - [ProgResult](docs/ProgResult.md)
 - [ProgService](docs/ProgService.md)
 - [ProgServices](docs/ProgServices.md)
 - [ProgServicesInner](docs/ProgServicesInner.md)
 - [Session](docs/Session.md)
 - [System](docs/System.md)
 - [TrajectoryPoint](docs/TrajectoryPoint.md)
 - [TrajectoryPoint1](docs/TrajectoryPoint1.md)
 - [User](docs/User.md)
 - [V1dataData](docs/V1dataData.md)
 - [Vehicle](docs/Vehicle.md)


## Documentation for Authorization

Authentication schemes defined for the API:
### key

- **Type**: API key
- **API key parameter name**: key
- **Location**: URL query string


## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

christopher.a.teubert@nasa.gov

