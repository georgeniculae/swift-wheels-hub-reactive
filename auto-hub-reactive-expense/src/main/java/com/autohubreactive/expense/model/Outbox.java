package com.autohubreactive.expense.model;

import com.autohubreactive.model.invoice.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "outbox")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Outbox {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    private Invoice content;

}
