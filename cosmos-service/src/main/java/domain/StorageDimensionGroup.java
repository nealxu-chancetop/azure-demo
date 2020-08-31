package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Neal
 */
public enum StorageDimensionGroup {
    @JsonProperty("WMS")
    WMS,
    @JsonProperty("WMS_SITEPP")
    WMS_SITEPP,
    @JsonProperty("wms_comppp") //for test
    WMS_COMPPP
}
