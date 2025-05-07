package com.autohubreactive.model.agency;

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

@Document(collection = "employee")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Employee {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String jobPosition;

    private Branch workingBranch;

}
