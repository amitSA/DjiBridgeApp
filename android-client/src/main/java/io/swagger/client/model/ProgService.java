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

package io.swagger.client.model;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

@ApiModel(description = "")
public class ProgService {
  
  @SerializedName("description")
  private String description = null;
  @SerializedName("name")
  private String name = null;
  @SerializedName("sensors")
  private String sensors = null;
  @SerializedName("config")
  private Object config = null;

  /**
   **/
  @ApiModelProperty(value = "")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public String getSensors() {
    return sensors;
  }
  public void setSensors(String sensors) {
    this.sensors = sensors;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Object getConfig() {
    return config;
  }
  public void setConfig(Object config) {
    this.config = config;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProgService progService = (ProgService) o;
    return (this.description == null ? progService.description == null : this.description.equals(progService.description)) &&
        (this.name == null ? progService.name == null : this.name.equals(progService.name)) &&
        (this.sensors == null ? progService.sensors == null : this.sensors.equals(progService.sensors)) &&
        (this.config == null ? progService.config == null : this.config.equals(progService.config));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.description == null ? 0: this.description.hashCode());
    result = 31 * result + (this.name == null ? 0: this.name.hashCode());
    result = 31 * result + (this.sensors == null ? 0: this.sensors.hashCode());
    result = 31 * result + (this.config == null ? 0: this.config.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProgService {\n");
    
    sb.append("  description: ").append(description).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  sensors: ").append(sensors).append("\n");
    sb.append("  config: ").append(config).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
