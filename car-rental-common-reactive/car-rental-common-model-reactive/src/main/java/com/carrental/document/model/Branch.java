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

@Document(collection = "branch")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Branch {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;
    private String name;
    private String address;
    private RentalOffice rentalOffice;

}
