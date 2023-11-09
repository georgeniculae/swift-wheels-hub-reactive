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

@Document(collection = "car")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Car {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;
    private String make;
    private String model;
    private BodyType bodyType;
    private int yearOfProduction;
    private String color;
    private int mileage;
    private CarStatus carStatus;
    private Double amount;
    private Branch originalBranch;
    private Branch actualBranch;
    private String urlOfImage;

}
