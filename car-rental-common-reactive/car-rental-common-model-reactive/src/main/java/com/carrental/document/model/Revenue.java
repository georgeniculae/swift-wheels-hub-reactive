package com.carrental.document.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "revenue")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Revenue {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;
    private LocalDate dateOfRevenue;
    private Double amountFromBooking;

}
