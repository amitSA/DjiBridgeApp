/**
 * Prognostics as a Service API
 * The PaaS REST API exposes the GSAP prognostics architecture as an internet accessible service. 
 *
 * OpenAPI spec version: 1.0.0
 * Contact: christopher.a.teubert@nasa.gov
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Date;
import io.swagger.client.model.*;
import io.swagger.client.model.System;

public class JsonUtil {
  public static GsonBuilder gsonBuilder;

  static {
    gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Date(json.getAsJsonPrimitive().getAsLong());
      }
    });
  }

  public static Gson getGson() {
    return gsonBuilder.create();
  }

  public static String serialize(Object obj){
    return getGson().toJson(obj);
  }

  public static <T> T deserializeToList(String jsonString, Class cls){
    return getGson().fromJson(jsonString, getListTypeForDeserialization(cls));
  }

  public static <T> T deserializeToObject(String jsonString, Class cls){
    return getGson().fromJson(jsonString, getTypeForDeserialization(cls));
  }

  public static Type getListTypeForDeserialization(Class cls) {
    String className = cls.getSimpleName();
    
    if ("ApiResponse".equalsIgnoreCase(className)) {
      return new TypeToken<List<ApiResponse>>(){}.getType();
    }
    
    if ("Component".equalsIgnoreCase(className)) {
      return new TypeToken<List<Component>>(){}.getType();
    }
    
    if ("DataFrame".equalsIgnoreCase(className)) {
      return new TypeToken<List<DataFrame>>(){}.getType();
    }
    
    if ("DataPoint".equalsIgnoreCase(className)) {
      return new TypeToken<List<DataPoint>>(){}.getType();
    }
    
    if ("Event".equalsIgnoreCase(className)) {
      return new TypeToken<List<Event>>(){}.getType();
    }
    
    if ("IdNamePair".equalsIgnoreCase(className)) {
      return new TypeToken<List<IdNamePair>>(){}.getType();
    }
    
    if ("InlineResponse200".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse200>>(){}.getType();
    }
    
    if ("InlineResponse2001".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2001>>(){}.getType();
    }
    
    if ("InlineResponse2002".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2002>>(){}.getType();
    }
    
    if ("InlineResponse2002Vehicles".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2002Vehicles>>(){}.getType();
    }
    
    if ("InlineResponse2003".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2003>>(){}.getType();
    }
    
    if ("InlineResponse2004".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2004>>(){}.getType();
    }
    
    if ("InlineResponse2005".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2005>>(){}.getType();
    }
    
    if ("InlineResponse2006".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2006>>(){}.getType();
    }
    
    if ("InlineResponse2007".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2007>>(){}.getType();
    }
    
    if ("InlineResponse2007Events".equalsIgnoreCase(className)) {
      return new TypeToken<List<InlineResponse2007Events>>(){}.getType();
    }
    
    if ("ProgResult".equalsIgnoreCase(className)) {
      return new TypeToken<List<ProgResult>>(){}.getType();
    }
    
    if ("ProgService".equalsIgnoreCase(className)) {
      return new TypeToken<List<ProgService>>(){}.getType();
    }
    
    if ("ProgServices".equalsIgnoreCase(className)) {
      return new TypeToken<List<ProgServices>>(){}.getType();
    }
    
    if ("ProgServicesInner".equalsIgnoreCase(className)) {
      return new TypeToken<List<ProgServicesInner>>(){}.getType();
    }
    
    if ("Session".equalsIgnoreCase(className)) {
      return new TypeToken<List<Session>>(){}.getType();
    }
    
    if ("System".equalsIgnoreCase(className)) {
      return new TypeToken<List<System>>(){}.getType();
    }
    
    if ("TrajectoryPoint".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrajectoryPoint>>(){}.getType();
    }
    
    if ("TrajectoryPoint1".equalsIgnoreCase(className)) {
      return new TypeToken<List<TrajectoryPoint1>>(){}.getType();
    }
    
    if ("User".equalsIgnoreCase(className)) {
      return new TypeToken<List<User>>(){}.getType();
    }
    
    if ("V1dataData".equalsIgnoreCase(className)) {
      return new TypeToken<List<V1dataData>>(){}.getType();
    }
    
    if ("Vehicle".equalsIgnoreCase(className)) {
      return new TypeToken<List<Vehicle>>(){}.getType();
    }
    
    return new TypeToken<List<Object>>(){}.getType();
  }

  public static Type getTypeForDeserialization(Class cls) {
    String className = cls.getSimpleName();
    
    if ("ApiResponse".equalsIgnoreCase(className)) {
      return new TypeToken<ApiResponse>(){}.getType();
    }
    
    if ("Component".equalsIgnoreCase(className)) {
      return new TypeToken<Component>(){}.getType();
    }
    
    if ("DataFrame".equalsIgnoreCase(className)) {
      return new TypeToken<DataFrame>(){}.getType();
    }
    
    if ("DataPoint".equalsIgnoreCase(className)) {
      return new TypeToken<DataPoint>(){}.getType();
    }
    
    if ("Event".equalsIgnoreCase(className)) {
      return new TypeToken<Event>(){}.getType();
    }
    
    if ("IdNamePair".equalsIgnoreCase(className)) {
      return new TypeToken<IdNamePair>(){}.getType();
    }
    
    if ("InlineResponse200".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse200>(){}.getType();
    }
    
    if ("InlineResponse2001".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2001>(){}.getType();
    }
    
    if ("InlineResponse2002".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2002>(){}.getType();
    }
    
    if ("InlineResponse2002Vehicles".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2002Vehicles>(){}.getType();
    }
    
    if ("InlineResponse2003".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2003>(){}.getType();
    }
    
    if ("InlineResponse2004".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2004>(){}.getType();
    }
    
    if ("InlineResponse2005".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2005>(){}.getType();
    }
    
    if ("InlineResponse2006".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2006>(){}.getType();
    }
    
    if ("InlineResponse2007".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2007>(){}.getType();
    }
    
    if ("InlineResponse2007Events".equalsIgnoreCase(className)) {
      return new TypeToken<InlineResponse2007Events>(){}.getType();
    }
    
    if ("ProgResult".equalsIgnoreCase(className)) {
      return new TypeToken<ProgResult>(){}.getType();
    }
    
    if ("ProgService".equalsIgnoreCase(className)) {
      return new TypeToken<ProgService>(){}.getType();
    }
    
    if ("ProgServices".equalsIgnoreCase(className)) {
      return new TypeToken<ProgServices>(){}.getType();
    }
    
    if ("ProgServicesInner".equalsIgnoreCase(className)) {
      return new TypeToken<ProgServicesInner>(){}.getType();
    }
    
    if ("Session".equalsIgnoreCase(className)) {
      return new TypeToken<Session>(){}.getType();
    }
    
    if ("System".equalsIgnoreCase(className)) {
      return new TypeToken<System>(){}.getType();
    }
    
    if ("TrajectoryPoint".equalsIgnoreCase(className)) {
      return new TypeToken<TrajectoryPoint>(){}.getType();
    }
    
    if ("TrajectoryPoint1".equalsIgnoreCase(className)) {
      return new TypeToken<TrajectoryPoint1>(){}.getType();
    }
    
    if ("User".equalsIgnoreCase(className)) {
      return new TypeToken<User>(){}.getType();
    }
    
    if ("V1dataData".equalsIgnoreCase(className)) {
      return new TypeToken<V1dataData>(){}.getType();
    }
    
    if ("Vehicle".equalsIgnoreCase(className)) {
      return new TypeToken<Vehicle>(){}.getType();
    }
    
    return new TypeToken<Object>(){}.getType();
  }

};
