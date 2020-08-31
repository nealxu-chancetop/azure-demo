package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.framework.api.validate.NotBlank;
import core.framework.api.validate.NotNull;
import core.framework.cosmos.Entity;
import core.framework.cosmos.Id;

import java.util.List;

/**
 * @author Neal
 */
@Entity(name = "items")
public class Item {
    @NotNull
    @NotBlank
    @Id
    public String id;

    @JsonProperty("search_name")
    public String searchName;

    @NotNull
    @NotBlank
    @JsonProperty("name")
    public String name;

    @JsonProperty("storage_dimension_group")
    public StorageDimensionGroup storageDimensionGroup;

    @JsonProperty("storage_dimension_groups")
    public List<StorageDimensionGroup> storageDimensionGroups;

    @NotNull
    @JsonProperty("status")
    public ItemStatus status;

    @JsonProperty("restaurant_ids")
    public List<String> restaurantIds;

    @NotNull
    @JsonProperty("unit_conversions")
    public List<UnitConversion> unitConversions;

    @NotNull
    @JsonProperty("created_time")
    public Long createdTime; //only support UTC

    @NotNull
    @NotBlank
    @JsonProperty("updated_by")
    public String updatedBy;

    @NotNull
    @JsonProperty("updated_time")
    public Long updatedTime;

    public static class UnitConversion {
        @NotNull
        @JsonProperty("from_quantity")
        public Double fromQuantity;

        @NotNull
        @NotBlank
        @JsonProperty("from_unit")
        public String fromUnit;

        @NotNull
        @JsonProperty("to_quantity")
        public Double toQuantity;

        @NotNull
        @NotBlank
        @JsonProperty("to_unit")
        public String toUnit;
    }
}
