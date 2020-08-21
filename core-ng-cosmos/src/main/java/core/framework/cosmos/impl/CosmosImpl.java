package core.framework.cosmos.impl;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import core.framework.cosmos.Cosmos;
import core.framework.cosmos.CosmosCollection;
import core.framework.internal.log.LogManager;
import core.framework.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * @author Neal
 */
public class CosmosImpl implements Cosmos {
    private final Logger logger = LoggerFactory.getLogger(CosmosImpl.class);
    int tooManyRowsReturnedThreshold = 2000;
    long slowOperationThresholdInNanos = Duration.ofSeconds(5).toNanos();
    String databaseId;
    CosmosClientBuilder cosmosClientBuilder = new CosmosClientBuilder().consistencyLevel(ConsistencyLevel.SESSION).contentResponseOnWriteEnabled(true)
        .userAgentSuffix(LogManager.APP_NAME)
        .gatewayMode();
    private CosmosClient cosmosClient;
    public CosmosDatabase database;

    public void initialize() {
        this.database = createDatabase();
    }

    public void close() {
        if (cosmosClient != null)
            cosmosClient.close();
    }

    private CosmosDatabase createDatabase() {
        if (database != null) throw new Error("CosmosDB already init");
        this.cosmosClient = this.cosmosClientBuilder.buildClient();
        return cosmosClient.getDatabase(this.databaseId);
    }

    public void endpoint(String endpoint) {
        cosmosClientBuilder.endpoint(endpoint);
    }

    public void key(String key) {
        cosmosClientBuilder.key(key);
    }

    public void databaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public void databaseId(List<String> preferredRegions) {
        cosmosClientBuilder.preferredRegions(preferredRegions);
    }

    public void preferredRegions(List<String> preferredRegions) {
        cosmosClientBuilder.preferredRegions(preferredRegions);
    }

    public void slowOperationThreshold(Duration threshold) {
        slowOperationThresholdInNanos = threshold.toNanos();
    }

    public void tooManyRowsReturnedThreshold(int threshold) {
        this.tooManyRowsReturnedThreshold = threshold;
    }

    public <T> CosmosCollection<T> collection(Class<T> entityClass) {
        var watch = new StopWatch();
        try {
//            new MongoClassValidator(entityClass).validateEntityClass();
//            codecs.registerEntity(entityClass);
            return new CosmosCollectionImpl<>(this, entityClass);
        } finally {
            logger.info("register mongo entity, entityClass={}, elapsed={}", entityClass.getCanonicalName(), watch.elapsed());
        }
    }

    public <T> void view(Class<T> viewClass) {
        var watch = new StopWatch();
        try {
//            new MongoClassValidator(viewClass).validateViewClass();
//            codecs.registerView(viewClass);
            throw new Error("Can't support view for now");
        } finally {
            logger.info("register mongo view, viewClass={}, elapsed={}", viewClass.getCanonicalName(), watch.elapsed());
        }
    }
}
