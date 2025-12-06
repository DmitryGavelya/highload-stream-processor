package org.hsse.highloadstreamprocessor.enrichment.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.Date;

@ChangeUnit(id = "test-data", order = "2", author = "dev-team")
public class ExampleDataMigration {

    @Execution
    public void execute(MongoTemplate mongoTemplate) {
        createEnrichmentData(mongoTemplate);
    }

    private void createEnrichmentData(MongoTemplate mongoTemplate) {
        if (mongoTemplate.getCollection("enrichment").countDocuments() == 0) {
            Date now = new Date();

            Document testData1 = new Document()
                    .append("original_id", "user-001")
                    .append("query_value", "some-value")
                    .append("created_at", now)
                    .append("updated_at", now)
                    .append("status", "Processing")
                    .append("another_value", "the stuff")
                    .append("enrichment_level", "basic")
                    .append("source_system", "legacy-system");

            Document testData2 = new Document()
                    .append("original_id", "user-002")
                    .append("query_value", "another-value")
                    .append("created_at", now)
                    .append("updated_at", now)
                    .append("status", "Stopped")
                    .append("another_value", "different stuff")
                    .append("enrichment_level", "advanced")
                    .append("source_system", "modern-system")
                    .append("priority", "high");

            Document testData3 = new Document()
                    .append("original_id", "user-003")
                    .append("query_value", "yet-another-value")
                    .append("created_at", now)
                    .append("updated_at", now)
                    .append("status", "Stopped")
                    .append("another_value", "more stuff")
                    .append("enrichment_level", "basic")
                    .append("source_system", "external-api")
                    .append("retry_count", 3)
                    .append("last_error", "Timeout exception");

            mongoTemplate.insert(Arrays.asList(testData1, testData2, testData3), "enrichment");
        }
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.remove(new Query(), "enrichment");
    }
}
