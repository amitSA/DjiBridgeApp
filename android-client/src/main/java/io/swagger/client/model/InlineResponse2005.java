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
public class InlineResponse2005 {
  
  @SerializedName("id")
  private Long id = null;
  @SerializedName("owner_id")
  private Long ownerId = null;
  @SerializedName("system_id")
  private Long systemId = null;
  @SerializedName("name")
  private String name = null;
  @SerializedName("type")
  private String type = null;
  @SerializedName("config")
  private Object config = null;

  /**
   **/
  @ApiModelProperty(value = "")
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Long getOwnerId() {
    return ownerId;
  }
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Long getSystemId() {
    return systemId;
  }
  public void setSystemId(Long systemId) {
    this.systemId = systemId;
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
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
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
    InlineResponse2005 inlineResponse2005 = (InlineResponse2005) o;
    return (this.id == null ? inlineResponse2005.id == null : this.id.equals(inlineResponse2005.id)) &&
        (this.ownerId == null ? inlineResponse2005.ownerId == null : this.ownerId.equals(inlineResponse2005.ownerId)) &&
        (this.systemId == null ? inlineResponse2005.systemId == null : this.systemId.equals(inlineResponse2005.systemId)) &&
        (this.name == null ? inlineResponse2005.name == null : this.name.equals(inlineResponse2005.name)) &&
        (this.type == null ? inlineResponse2005.type == null : this.type.equals(inlineResponse2005.type)) &&
        (this.config == null ? inlineResponse2005.config == null : this.config.equals(inlineResponse2005.config));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.id == null ? 0: this.id.hashCode());
    result = 31 * result + (this.ownerId == null ? 0: this.ownerId.hashCode());
    result = 31 * result + (this.systemId == null ? 0: this.systemId.hashCode());
    result = 31 * result + (this.name == null ? 0: this.name.hashCode());
    result = 31 * result + (this.type == null ? 0: this.type.hashCode());
    result = 31 * result + (this.config == null ? 0: this.config.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2005 {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  ownerId: ").append(ownerId).append("\n");
    sb.append("  systemId: ").append(systemId).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  type: ").append(type).append("\n");
    sb.append("  config: ").append(config).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
