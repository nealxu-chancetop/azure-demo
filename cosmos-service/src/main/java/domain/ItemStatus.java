package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ItemStatus {
    @JsonProperty("NEW")
    NEW,
    @JsonProperty("BOM_CREATED")
    BOM_CREATED,
    @JsonProperty("PUBLISHED")
    PUBLISHED
}