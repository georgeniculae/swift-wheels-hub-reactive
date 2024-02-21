package com.swiftwheelshubreactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@Document(collection = "rental_office")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RentalOffice {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NonNull
    private String name;

    @NonNull
    private String contactAddress;

    private String logoType;

}
