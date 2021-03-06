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
public class InlineResponse2001 {
  
  @SerializedName("code")
  private Integer code = null;
  @SerializedName("summary")
  private String summary = null;
  @SerializedName("response")
  private String response = null;

  /**
   **/
  @ApiModelProperty(value = "")
  public Integer getCode() {
    return code;
  }
  public void setCode(Integer code) {
    this.code = code;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public String getResponse() {
    return response;
  }
  public void setResponse(String response) {
    this.response = response;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse2001 inlineResponse2001 = (InlineResponse2001) o;
    return (this.code == null ? inlineResponse2001.code == null : this.code.equals(inlineResponse2001.code)) &&
        (this.summary == null ? inlineResponse2001.summary == null : this.summary.equals(inlineResponse2001.summary)) &&
        (this.response == null ? inlineResponse2001.response == null : this.response.equals(inlineResponse2001.response));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.code == null ? 0: this.code.hashCode());
    result = 31 * result + (this.summary == null ? 0: this.summary.hashCode());
    result = 31 * result + (this.response == null ? 0: this.response.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2001 {\n");
    
    sb.append("  code: ").append(code).append("\n");
    sb.append("  summary: ").append(summary).append("\n");
    sb.append("  response: ").append(response).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
