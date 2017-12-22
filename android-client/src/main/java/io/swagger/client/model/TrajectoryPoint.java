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
public class TrajectoryPoint {
  
  @SerializedName("vehicle_id")
  private Long vehicleId = null;
  @SerializedName("latitude")
  private Double latitude = null;
  @SerializedName("longitude")
  private Double longitude = null;
  @SerializedName("altitude")
  private Double altitude = null;
  @SerializedName("eta")
  private Long eta = null;

  /**
   **/
  @ApiModelProperty(value = "")
  public Long getVehicleId() {
    return vehicleId;
  }
  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Double getLatitude() {
    return latitude;
  }
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Double getLongitude() {
    return longitude;
  }
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Double getAltitude() {
    return altitude;
  }
  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Long getEta() {
    return eta;
  }
  public void setEta(Long eta) {
    this.eta = eta;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TrajectoryPoint trajectoryPoint = (TrajectoryPoint) o;
    return (this.vehicleId == null ? trajectoryPoint.vehicleId == null : this.vehicleId.equals(trajectoryPoint.vehicleId)) &&
        (this.latitude == null ? trajectoryPoint.latitude == null : this.latitude.equals(trajectoryPoint.latitude)) &&
        (this.longitude == null ? trajectoryPoint.longitude == null : this.longitude.equals(trajectoryPoint.longitude)) &&
        (this.altitude == null ? trajectoryPoint.altitude == null : this.altitude.equals(trajectoryPoint.altitude)) &&
        (this.eta == null ? trajectoryPoint.eta == null : this.eta.equals(trajectoryPoint.eta));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.vehicleId == null ? 0: this.vehicleId.hashCode());
    result = 31 * result + (this.latitude == null ? 0: this.latitude.hashCode());
    result = 31 * result + (this.longitude == null ? 0: this.longitude.hashCode());
    result = 31 * result + (this.altitude == null ? 0: this.altitude.hashCode());
    result = 31 * result + (this.eta == null ? 0: this.eta.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrajectoryPoint {\n");
    
    sb.append("  vehicleId: ").append(vehicleId).append("\n");
    sb.append("  latitude: ").append(latitude).append("\n");
    sb.append("  longitude: ").append(longitude).append("\n");
    sb.append("  altitude: ").append(altitude).append("\n");
    sb.append("  eta: ").append(eta).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
