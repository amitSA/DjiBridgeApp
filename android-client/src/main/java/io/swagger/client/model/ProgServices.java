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

import io.swagger.client.model.ProgServicesInner;
import java.util.*;
import java.util.ArrayList;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

@ApiModel(description = "")
public class ProgServices extends ArrayList<ProgServicesInner> {
  


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProgServices progServices = (ProgServices) o;
    return true;
  }

  @Override
  public int hashCode() {
    int result = 17;
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProgServices {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
