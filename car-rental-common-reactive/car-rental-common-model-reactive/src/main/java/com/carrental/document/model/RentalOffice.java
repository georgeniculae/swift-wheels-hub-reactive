package com.carrental.document.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Contact address domain cannot be empty")
    private String contactAddress;

    private String logoType;

}
