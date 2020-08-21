package domain;

import core.framework.api.validate.NotBlank;
import core.framework.api.validate.NotNull;
import core.framework.cosmos.Collection;

import java.util.List;

/**
 * @author Neal
 */
@Collection(name = "items")
public class Item {
    @NotNull
    @NotBlank
    public String id;

    public String searchName;

    @NotNull
    @NotBlank
    public String name;

    public StorageDimensionGroup storageDimensionGroup;

    @NotNull
    public ItemStatus status;

    public List<String> restaurantIds;

    @NotNull
    public List<UnitConversion> unitConversions;

    @NotNull
    public Long createdTime; //only support UTC

    @NotNull
    @NotBlank
    public String updatedBy;

    @NotNull
    public Long updatedTime;

    public static class UnitConversion {
        @NotNull
        public Double fromQuantity;

        @NotNull
        @NotBlank
        public String fromUnit;

        @NotNull
        public Double toQuantity;

        @NotNull
        @NotBlank
        public String toUnit;
    }
}
