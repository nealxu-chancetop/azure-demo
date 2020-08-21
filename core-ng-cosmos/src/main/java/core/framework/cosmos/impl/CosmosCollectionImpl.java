package core.framework.cosmos.impl;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import core.framework.cosmos.Collection;
import core.framework.cosmos.CosmosCollection;
import core.framework.internal.validate.Validator;
import core.framework.log.ActionLogContext;
import core.framework.log.Markers;
import core.framework.util.StopWatch;
import core.framework.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Neal
 */
public class CosmosCollectionImpl<T> implements CosmosCollection<T> {
    private final Logger logger = LoggerFactory.getLogger(CosmosCollectionImpl.class);
    private final CosmosImpl cosmos;
    private final Class<T> entityClass;
    private final String collectionName;
    private final Validator validator;
    private CosmosContainer cosmosContainer;

    public CosmosCollectionImpl(CosmosImpl cosmos, Class<T> entityClass) {
        this.cosmos = cosmos;
        this.entityClass = entityClass;
        this.validator = Validator.of(entityClass);

        this.collectionName = entityClass.getAnnotation(Collection.class).name();
    }

    @Override
    public Optional<T> get(String id) {
        StopWatch watch = new StopWatch();
        if (Strings.isBlank(id)) throw new Error("id must not be null");

        int returnedDocs = 0;
        try {
            CosmosItemResponse<T> result = cosmosContainer().readItem(id, new PartitionKey(id), entityClass);
            if (result != null) returnedDocs = 1;
            return result == null ? Optional.empty() : Optional.ofNullable(result.getItem());
        } finally {
            long elapsed = watch.elapsed();
            ActionLogContext.track("cosmos", elapsed, returnedDocs, 0);
            logger.debug("get, collection={}, id={}, returnedDocs={}, elapsed={}",
                collectionName,
                id,
                returnedDocs,
                elapsed);
            checkSlowOperation(elapsed);
        }
    }

    @Override
    public void upsert(T entity) {
        var watch = new StopWatch();
        validator.validate(entity, false);
        try {
            cosmosContainer().upsertItem(entity);
        } finally {
            long elapsed = watch.elapsed();
            ActionLogContext.track("cosmos", elapsed, 0, 1);
            logger.debug("upsert, collection={}, elapsed={}", collectionName, elapsed);
            checkSlowOperation(elapsed);
        }
    }

    @Override
    public void insert(T entity) {
        var watch = new StopWatch();
        validator.validate(entity, false);
        try {
            cosmosContainer().createItem(entity);
        } finally {
            long elapsed = watch.elapsed();
            ActionLogContext.track("cosmos", elapsed, 0, 1);
            logger.debug("insert, collection={}, elapsed={}", collectionName, elapsed);
            checkSlowOperation(elapsed);
        }
    }

    @Override
    public Optional<T> findOne(SqlQuerySpec query) {
        return findOne(query, entityClass);
    }

    @Override
    public <V> Optional<V> findOne(SqlQuerySpec query, Class<V> clazz) {
        var watch = new StopWatch();
        int returnedDocs = 0;
        try {
            List<V> results = new ArrayList<>();
            CosmosPagedIterable<V> items = cosmosContainer().queryItems(query, new CosmosQueryRequestOptions(), clazz);
            fetch(items, results);
            if (results.isEmpty()) return Optional.empty();
            if (results.size() > 1) throw new Error("more than one row returned");
            returnedDocs = 1;
            return Optional.of(results.get(0));
        } finally {
            long elapsed = watch.elapsed();
            ActionLogContext.track("cosmos", elapsed, returnedDocs, 0);
            logger.debug("findOne, class={}, sql={}, params={}, returnedDocs={}, elapsed={}",
                clazz.getSimpleName(),
                query.getQueryText(),
                query.getParameters(),
                returnedDocs,
                elapsed);
            checkSlowOperation(elapsed);
        }
    }

    @Override
    public List<T> find(SqlQuerySpec query) {
        return find(query, entityClass);
    }

    @Override
    public <V> List<V> find(SqlQuerySpec query, Class<V> clazz) {
        var watch = new StopWatch();
        List<V> results = new ArrayList<>();
        try {
            CosmosPagedIterable<V> items = cosmosContainer().queryItems(query, new CosmosQueryRequestOptions(), clazz);
            fetch(items, results);
            checkTooManyRowsReturned(results.size());
            return results;
        } finally {
            long elapsed = watch.elapsed();
            int size = results.size();
            ActionLogContext.track("cosmos", elapsed, size, 0);
            logger.debug("find, clazz={}, sql={}, params={}, returnedDocs={}, elapsed={}",
                collectionName,
                query.getQueryText(),
                query.getParameters(),
                size,
                elapsed);
            checkSlowOperation(elapsed);
        }
    }

    @Override
    public void delete(String id) {
        var watch = new StopWatch();
        try {
            cosmosContainer().deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
        } finally {
            long elapsed = watch.elapsed();
            ActionLogContext.track("mongo", elapsed, 0, 1);
            logger.debug("delete, collection={}, id={}, elapsed={}", collectionName, id, elapsed);
            checkSlowOperation(elapsed);
        }
    }

    private <V> void fetch(CosmosPagedIterable<V> iterable, List<V> results) {
        Iterator<V> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            results.add(iterator.next());
        }
    }

    private void checkSlowOperation(long elapsed) {
        if (elapsed > cosmos.slowOperationThresholdInNanos)
            logger.warn(Markers.errorCode("SLOW_COSMOSDB"), "slow cosmosDB query, elapsed={}", elapsed);
    }

    private void checkTooManyRowsReturned(int size) {
        if (size > cosmos.tooManyRowsReturnedThreshold)
            logger.warn(Markers.errorCode("TOO_MANY_ROWS_RETURNED"), "too many rows returned, returnedRows={}", size);
    }

    private CosmosContainer cosmosContainer() {
        if (this.cosmosContainer == null) {
            this.cosmosContainer = cosmos.database.getContainer(collectionName);
        }
        return cosmosContainer;
    }
}
