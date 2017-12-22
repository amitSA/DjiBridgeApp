# DefaultApi

All URIs are relative to *https://prog.arc.nasa.gov/api*

Method | HTTP request | Description
------------- | ------------- | -------------
[**componentCreateV1**](DefaultApi.md#componentCreateV1) | **POST** /v1/component | Creates a new component
[**componentDetailsV1**](DefaultApi.md#componentDetailsV1) | **GET** /v1/component | Gets information about the specified component. If no component_id is specified, gets an array of component ids and names that belong to the current user. 
[**dataAddV1**](DefaultApi.md#dataAddV1) | **POST** /v1/data | Adds a dataPoint for the specified component
[**progServicesV1**](DefaultApi.md#progServicesV1) | **GET** /v1/prog_services | Gets a list of prognostics services
[**prognosticsGetV1**](DefaultApi.md#prognosticsGetV1) | **GET** /v1/prognostics | Gets a new prediction for the specified object
[**sessionEndV1**](DefaultApi.md#sessionEndV1) | **DELETE** /v1/session | Ends a running prognostics session
[**sessionStartV1**](DefaultApi.md#sessionStartV1) | **POST** /v1/session | Begins a new prognostics session
[**sessionStatusV1**](DefaultApi.md#sessionStatusV1) | **GET** /v1/session | Gets the status of a running prognostics session
[**systemAssignV1**](DefaultApi.md#systemAssignV1) | **POST** /v1/system/assign | Assigns the specified component to the specified system.
[**systemCreateV1**](DefaultApi.md#systemCreateV1) | **POST** /v1/system | Creates a new system
[**systemStatusV1**](DefaultApi.md#systemStatusV1) | **GET** /v1/system | Gets information about the specified system
[**trajectoryGetV1**](DefaultApi.md#trajectoryGetV1) | **GET** /v1/trajectory | Gets trajectory points for a vehicle
[**trajectoryRemoveV1**](DefaultApi.md#trajectoryRemoveV1) | **DELETE** /v1/trajectory | Removes a trajectory point for a vehicle
[**trajectorySetV1**](DefaultApi.md#trajectorySetV1) | **POST** /v1/trajectory | Sets a trajectory point for a vehicle
[**userInfoV1**](DefaultApi.md#userInfoV1) | **GET** /v1/user | Gets information about the user
[**vehicleCreateV1**](DefaultApi.md#vehicleCreateV1) | **POST** /v1/vehicle | Creates a new vehicle
[**vehicleStatusV1**](DefaultApi.md#vehicleStatusV1) | **GET** /v1/vehicle | Gets information about the specified vehicle. If no vehicle_id is specified, gets an array of vehicle ids and names that belong to the current user. 
[**versionDetailsV1**](DefaultApi.md#versionDetailsV1) | **GET** /v1 | List version 1 API details
[**versions**](DefaultApi.md#versions) | **GET** / | List API versions


<a name="componentCreateV1"></a>
# **componentCreateV1**
> InlineResponse2005 componentCreateV1(name, type, config, username)

Creates a new component

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

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
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| The name of the component being created |
 **type** | **String**| The type of prognostics applied to the component |
 **config** | **String**| Configuration information for the component&#39;s prognostic model. |
 **username** | **String**| The name of the user. |

### Return type

[**InlineResponse2005**](InlineResponse2005.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="componentDetailsV1"></a>
# **componentDetailsV1**
> InlineResponse2005 componentDetailsV1(componentId, username)

Gets information about the specified component. If no component_id is specified, gets an array of component ids and names that belong to the current user. 

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
Long componentId = 789L; // Long | The id of the component being queried
String username = "username_example"; // String | The name of the user.
try {
    InlineResponse2005 result = apiInstance.componentDetailsV1(componentId, username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#componentDetailsV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **componentId** | **Long**| The id of the component being queried |
 **username** | **String**| The name of the user. |

### Return type

[**InlineResponse2005**](InlineResponse2005.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="dataAddV1"></a>
# **dataAddV1**
> dataAddV1(username, sessionId, dataFrame)

Adds a dataPoint for the specified component

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long sessionId = 789L; // Long | The id of an active session to add data for
DataFrame dataFrame = new DataFrame(); // DataFrame | The data being uploaded
try {
    apiInstance.dataAddV1(username, sessionId, dataFrame);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#dataAddV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **sessionId** | **Long**| The id of an active session to add data for |
 **dataFrame** | [**DataFrame**](DataFrame.md)| The data being uploaded |

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="progServicesV1"></a>
# **progServicesV1**
> progServicesV1(username)

Gets a list of prognostics services

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
try {
    apiInstance.progServicesV1(username);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#progServicesV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="prognosticsGetV1"></a>
# **prognosticsGetV1**
> InlineResponse2007 prognosticsGetV1(username, id, type)

Gets a new prediction for the specified object

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long id = 789L; // Long | The name of the object being queried
String type = "type_example"; // String | The type of the object being queried. Should be one of 'vehicle', 'system', or 'component'. 
try {
    InlineResponse2007 result = apiInstance.prognosticsGetV1(username, id, type);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#prognosticsGetV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **id** | **Long**| The name of the object being queried | [optional]
 **type** | **String**| The type of the object being queried. Should be one of &#39;vehicle&#39;, &#39;system&#39;, or &#39;component&#39;.  | [optional]

### Return type

[**InlineResponse2007**](InlineResponse2007.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="sessionEndV1"></a>
# **sessionEndV1**
> sessionEndV1(username, vehicleId)

Ends a running prognostics session

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long vehicleId = 789L; // Long | The vehicle associated with the session
try {
    apiInstance.sessionEndV1(username, vehicleId);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#sessionEndV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **vehicleId** | **Long**| The vehicle associated with the session |

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="sessionStartV1"></a>
# **sessionStartV1**
> InlineResponse2006 sessionStartV1(username, vehicleId)

Begins a new prognostics session

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long vehicleId = 789L; // Long | The vehicle associated with the session
try {
    InlineResponse2006 result = apiInstance.sessionStartV1(username, vehicleId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#sessionStartV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **vehicleId** | **Long**| The vehicle associated with the session |

### Return type

[**InlineResponse2006**](InlineResponse2006.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="sessionStatusV1"></a>
# **sessionStatusV1**
> InlineResponse2006 sessionStatusV1(username, vehicleId)

Gets the status of a running prognostics session

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long vehicleId = 789L; // Long | The vehicle associated with the session
try {
    InlineResponse2006 result = apiInstance.sessionStatusV1(username, vehicleId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#sessionStatusV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **vehicleId** | **Long**| The vehicle associated with the session |

### Return type

[**InlineResponse2006**](InlineResponse2006.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="systemAssignV1"></a>
# **systemAssignV1**
> systemAssignV1(username, componentId, systemId)

Assigns the specified component to the specified system.

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long componentId = 789L; // Long | The id of the component being assigned
Long systemId = 789L; // Long | The id of the system being assigned to
try {
    apiInstance.systemAssignV1(username, componentId, systemId);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#systemAssignV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **componentId** | **Long**| The id of the component being assigned |
 **systemId** | **Long**| The id of the system being assigned to |

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="systemCreateV1"></a>
# **systemCreateV1**
> InlineResponse2004 systemCreateV1(vehicleId, name, type, username)

Creates a new system

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
Long vehicleId = 789L; // Long | The vehicle to associate the system with
String name = "name_example"; // String | The name of the system being created
String type = "type_example"; // String | The type of prognostics applied to the system
String username = "username_example"; // String | The name of the user.
try {
    InlineResponse2004 result = apiInstance.systemCreateV1(vehicleId, name, type, username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#systemCreateV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **vehicleId** | **Long**| The vehicle to associate the system with |
 **name** | **String**| The name of the system being created |
 **type** | **String**| The type of prognostics applied to the system |
 **username** | **String**| The name of the user. |

### Return type

[**InlineResponse2004**](InlineResponse2004.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="systemStatusV1"></a>
# **systemStatusV1**
> InlineResponse2004 systemStatusV1(systemId, username)

Gets information about the specified system

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
Long systemId = 789L; // Long | The id of the system being queried
String username = "username_example"; // String | The name of the user.
try {
    InlineResponse2004 result = apiInstance.systemStatusV1(systemId, username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#systemStatusV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **systemId** | **Long**| The id of the system being queried |
 **username** | **String**| The name of the user. |

### Return type

[**InlineResponse2004**](InlineResponse2004.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="trajectoryGetV1"></a>
# **trajectoryGetV1**
> trajectoryGetV1(username, vehicleId)

Gets trajectory points for a vehicle

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long vehicleId = 789L; // Long | 
try {
    apiInstance.trajectoryGetV1(username, vehicleId);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#trajectoryGetV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **vehicleId** | **Long**|  | [optional]

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="trajectoryRemoveV1"></a>
# **trajectoryRemoveV1**
> trajectoryRemoveV1(username, trajectoryPoint, vehicleId)

Removes a trajectory point for a vehicle

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
TrajectoryPoint1 trajectoryPoint = new TrajectoryPoint1(); // TrajectoryPoint1 | 
Long vehicleId = 789L; // Long | 
try {
    apiInstance.trajectoryRemoveV1(username, trajectoryPoint, vehicleId);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#trajectoryRemoveV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **trajectoryPoint** | [**TrajectoryPoint1**](TrajectoryPoint1.md)|  | [optional]
 **vehicleId** | **Long**|  | [optional]

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="trajectorySetV1"></a>
# **trajectorySetV1**
> trajectorySetV1(username, trajectoryPoint, vehicleId)

Sets a trajectory point for a vehicle

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
TrajectoryPoint trajectoryPoint = new TrajectoryPoint(); // TrajectoryPoint | 
Long vehicleId = 789L; // Long | 
try {
    apiInstance.trajectorySetV1(username, trajectoryPoint, vehicleId);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#trajectorySetV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **trajectoryPoint** | [**TrajectoryPoint**](TrajectoryPoint.md)|  | [optional]
 **vehicleId** | **Long**|  | [optional]

### Return type

null (empty response body)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="userInfoV1"></a>
# **userInfoV1**
> InlineResponse2002 userInfoV1(username)

Gets information about the user

Gets information about the user, including the vehicle and components owned by the user. 

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
try {
    InlineResponse2002 result = apiInstance.userInfoV1(username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#userInfoV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |

### Return type

[**InlineResponse2002**](InlineResponse2002.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="vehicleCreateV1"></a>
# **vehicleCreateV1**
> InlineResponse2003 vehicleCreateV1(name, username)

Creates a new vehicle

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String name = "name_example"; // String | The name of the vehicle being created
String username = "username_example"; // String | The name of the user.
try {
    InlineResponse2003 result = apiInstance.vehicleCreateV1(name, username);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#vehicleCreateV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **name** | **String**| The name of the vehicle being created |
 **username** | **String**| The name of the user. |

### Return type

[**InlineResponse2003**](InlineResponse2003.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="vehicleStatusV1"></a>
# **vehicleStatusV1**
> InlineResponse2003 vehicleStatusV1(username, vehicleId)

Gets information about the specified vehicle. If no vehicle_id is specified, gets an array of vehicle ids and names that belong to the current user. 

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
String username = "username_example"; // String | The name of the user.
Long vehicleId = 789L; // Long | The vehicle being queried
try {
    InlineResponse2003 result = apiInstance.vehicleStatusV1(username, vehicleId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#vehicleStatusV1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **String**| The name of the user. |
 **vehicleId** | **Long**| The vehicle being queried | [optional]

### Return type

[**InlineResponse2003**](InlineResponse2003.md)

### Authorization

[key](../README.md#key)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="versionDetailsV1"></a>
# **versionDetailsV1**
> InlineResponse2001 versionDetailsV1()

List version 1 API details

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
try {
    InlineResponse2001 result = apiInstance.versionDetailsV1();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#versionDetailsV1");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="versions"></a>
# **versions**
> InlineResponse200 versions()

List API versions

### Example
```java
// Import classes:
//import io.swagger.client.api.DefaultApi;

DefaultApi apiInstance = new DefaultApi();
try {
    InlineResponse200 result = apiInstance.versions();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#versions");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**InlineResponse200**](InlineResponse200.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

