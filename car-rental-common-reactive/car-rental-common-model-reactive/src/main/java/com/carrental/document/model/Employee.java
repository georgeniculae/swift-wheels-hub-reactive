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

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotEmpty(message = "Job position cannot be empty")
    private String jobPosition;

    private Branch workingBranch;

}
