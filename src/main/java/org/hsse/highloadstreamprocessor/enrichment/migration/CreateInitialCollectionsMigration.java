package org.hsse.highloadstreamprocessor.enrichment.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@ChangeUnit(id = "create-initial-collections", order = "1", author = "dev-team")
public class CreateInitialCollectionsMigration {

    @Execution
    public void createCollections(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists("enrichment")) {
            mongoTemplate.createCollection("enrichment");
        }

        IndexOperations indexOperations = mongoTemplate.indexOps("enrichment");
        indexOperations.createIndex(new Index().on("query_value", Direction.ASC));
        indexOperations.createIndex(new Index().on("created_at", Direction.DESC));
        indexOperations.createIndex(new Index().on("status", Direction.ASC));
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("enrichment");
    }
}
