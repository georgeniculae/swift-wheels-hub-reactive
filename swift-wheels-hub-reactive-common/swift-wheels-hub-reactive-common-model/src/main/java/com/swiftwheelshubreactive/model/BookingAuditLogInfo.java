package com.swiftwheelshubreactive.model;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "booking_audit_log_info")
@NoArgsConstructor
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

    @Builder.Default
    private List<String> parametersValues = new ArrayList<>();

    public BookingAuditLogInfo(ObjectId id,
                               @NonNull String methodName,
                               LocalDateTime timestamp,
                               String username,
                               List<String> parametersValues) {
        this.id = id;
        this.methodName = methodName;
        this.timestamp = timestamp;
        this.username = username;
        this.parametersValues = Objects.requireNonNullElseGet(parametersValues, ArrayList::new);
    }

}
