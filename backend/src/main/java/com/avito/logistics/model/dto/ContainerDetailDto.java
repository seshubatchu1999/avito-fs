package com.avito.logistics.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerDetailDto {

    @JsonProperty("container_number")
    private String containerNumber;

    @JsonProperty("seal_numbers")
    private List<String> sealNumbers = new ArrayList<>();

    @JsonProperty("container_size_type")
    private String containerSizeType;

    @JsonProperty("package_count")
    private String packageCount;

    @JsonProperty("package_type")
    private String packageType;

    @JsonProperty("gross_weight")
    private String grossWeight;

    @JsonProperty("net_weight")
    private String netWeight;

    @JsonProperty("cbm_measurement")
    private String cbmMeasurement;

    public ContainerDetailDto() {}

    public String getContainerNumber() {
        return containerNumber;
    }

    public void setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
    }

    public List<String> getSealNumbers() {
        return sealNumbers;
    }

    public void setSealNumbers(List<String> sealNumbers) {
        this.sealNumbers = sealNumbers != null ? sealNumbers : new ArrayList<>();
    }

    public String getContainerSizeType() {
        return containerSizeType;
    }

    public void setContainerSizeType(String containerSizeType) {
        this.containerSizeType = containerSizeType;
    }

    public String getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(String packageCount) {
        this.packageCount = packageCount;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getCbmMeasurement() {
        return cbmMeasurement;
    }

    public void setCbmMeasurement(String cbmMeasurement) {
        this.cbmMeasurement = cbmMeasurement;
    }
}
