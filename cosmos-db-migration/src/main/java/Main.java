import core.framework.cosmos.CosmosMigration;

/**
 * @author Neal
 */
public class Main {
    public static void main(String[] args) {
        var migration = new CosmosMigration("cosmos.properties");
        migration.migrate(cosmos -> {
            cosmos.createContainerIfNotExists("bom_headers", "/id");
            cosmos.createContainerIfNotExists("items", "/id");
        });
    }
}
