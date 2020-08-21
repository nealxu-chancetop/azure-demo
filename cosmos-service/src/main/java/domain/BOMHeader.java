package domain;

import core.framework.api.validate.NotBlank;
import core.framework.api.validate.NotNull;
import core.framework.cosmos.Collection;

import java.util.List;

/**
 * @author Neal
 */
@Collection(name = "bom_headers")
public class BOMHeader {
    @NotNull
    @NotBlank
    public String id;

    @NotNull
    @NotBlank
    public String name;

    @NotNull
    @NotBlank
    public String itemNumber;

    @NotNull
    public List<BomLine> bomLines;

    @NotNull
    @NotBlank
    public String createdBy;

    @NotNull
    public Long createdTime;

    public static class BomLine {
        @NotNull
        @NotBlank
        public String itemNumber;

        @NotNull
        public Double quantity;

        @NotNull
        @NotBlank
        public String unit;

        @NotNull
        public Integer order;

        @NotNull
        public Boolean manageInventory;
    }
}
