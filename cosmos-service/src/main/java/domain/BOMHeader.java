package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.framework.api.validate.NotBlank;
import core.framework.api.validate.NotNull;
import core.framework.cosmos.Entity;
import core.framework.cosmos.Id;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Neal
 */
@Entity(name = "bom_headers")
public class BOMHeader {
    @NotNull
    @NotBlank
    @Id
    public String id;

    @NotNull
    @NotBlank
    @JsonProperty("name")
    public String name;

    @NotNull
    @NotBlank
    @JsonProperty("item_number")
    public String itemNumber;

    @NotNull
    @JsonProperty("bom_lines")
    public List<BomLine> bomLines;

    @NotNull
    @NotBlank
    @JsonProperty("created_by")
    public String createdBy;

    @NotNull
    @JsonProperty("created_time")
    public ZonedDateTime createdTime;

    public static class BomLine {
        @NotNull
        @NotBlank
        @JsonProperty("item_number")
        public String itemNumber;

        @NotNull
        @JsonProperty("quantity")
        public Double quantity;

        @NotNull
        @NotBlank
        @JsonProperty("unit")
        public String unit;

        @NotNull
        @JsonProperty("order")
        public Integer order;

        @NotNull
        @JsonProperty("manage_inventory")
        public Boolean manageInventory;
    }
}
