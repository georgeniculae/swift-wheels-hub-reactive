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

@Document(collection = "booking_audit_log_info")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookingAuditLogInfo {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NonNull
    private String methodName;

    private LocalDateTime timestamp;

    private String username;

    private List<String> parametersValues;

}
