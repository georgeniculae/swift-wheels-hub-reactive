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

import java.util.List;

@Document(collection = "audit_log_info")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuditLogInfo {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NotEmpty(message = "Method name cannot be empty")
    private String methodName;

    private String username;

    private List<String> parametersValues;

}
