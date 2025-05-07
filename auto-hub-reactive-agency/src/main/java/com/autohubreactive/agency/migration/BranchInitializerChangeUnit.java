package com.autohubreactive.agency.migration;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.autohubreactive.model.agency.Branch;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

@ChangeUnit(id = "branch-initializer", order = "2", author = "George Niculae")
@Slf4j
public class BranchInitializerChangeUnit {

    private static final String COLLECTION_NAME = "branch";

    @BeforeExecution
    public void beforeExecution(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();
        mongoDatabase.createCollection(COLLECTION_NAME).subscribe(subscriber);
        subscriber.await();
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(COLLECTION_NAME)
                .drop()
                .subscribe(subscriber);

        subscriber.await();
    }

    @Execution
    public void execution(ClientSession clientSession, MongoDatabase mongoDatabase) {
        SubscriberSync<InsertManyResult> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(COLLECTION_NAME, Branch.class)
                .insertMany(clientSession, DatabaseCollectionCreator.getBranches())
                .subscribe(subscriber);

        InsertManyResult result = subscriber.getFirst();

        log.info("ClientInitializerChangeLog.execution wasAcknowledged: {}", result.wasAcknowledged());

        result.getInsertedIds()
                .forEach((key, value) -> log.info("update id[{}] : {}", key, value));
    }

    @RollbackExecution
    public void rollbackExecution(ClientSession clientSession, MongoDatabase mongoDatabase) {
        SubscriberSync<DeleteResult> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(COLLECTION_NAME, Branch.class)
                .deleteMany(clientSession, new Document())
                .subscribe(subscriber);

        DeleteResult result = subscriber.getFirst();

        log.info("ClientInitializerChangeLog.rollbackExecution wasAcknowledged: {}", result.wasAcknowledged());
        log.info("ClientInitializerChangeLog.rollbackExecution deleted count: {}", result.getDeletedCount());
    }

}
