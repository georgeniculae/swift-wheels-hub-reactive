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

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "customer_audit_log_info")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CustomerAuditLogInfo {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NonNull
    private String methodName;

    private String username;

    private LocalDateTime timestamp;

    private List<String> parametersValues;

}
