import app.cosmos.api.item.ItemWebService;
import core.framework.cosmos.module.CosmosConfig;
import core.framework.module.App;
import core.framework.module.SystemModule;
import domain.BOMHeader;
import domain.Item;
import item.service.ItemWebServiceImpl;
import service.ItemService;

import java.util.List;

/**
 * @author Neal
 */
public class CosmosServiceApp extends App {
    @Override
    protected void initialize() {
        load(new SystemModule("sys.properties"));
        loadProperties("cosmos.properties");

        CosmosConfig cosmosConfig = config(CosmosConfig.class);
        cosmosConfig.databaseId(requiredProperty("cosmos.databaseId"));
        cosmosConfig.endpoint(requiredProperty("cosmos.endpoint"));
        cosmosConfig.key(requiredProperty("cosmos.key"));
        cosmosConfig.preferredRegions(List.of(requiredProperty("cosmos.preferredRegions").split(",")));

        cosmosConfig.entity(Item.class);
        cosmosConfig.entity(BOMHeader.class);


        bind(ItemService.class);

        api().service(ItemWebService.class, bind(ItemWebServiceImpl.class));
    }
}
