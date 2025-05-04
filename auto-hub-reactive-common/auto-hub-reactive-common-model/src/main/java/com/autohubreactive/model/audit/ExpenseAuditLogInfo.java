package com.autohubreactive.model.audit;

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

@Document(collection = "expense_audit_log_info")
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExpenseAuditLogInfo {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NonNull
    private String methodName;

    private String username;

    private LocalDateTime timestamp;

    @Builder.Default
    private List<String> parametersValues = new ArrayList<>();

    public ExpenseAuditLogInfo(ObjectId id,
                               @NonNull String methodName,
                               String username,
                               LocalDateTime timestamp,
                               List<String> parametersValues) {
        this.id = id;
        this.methodName = methodName;
        this.username = username;
        this.timestamp = timestamp;
        this.parametersValues = Objects.requireNonNullElseGet(parametersValues, ArrayList::new);
    }

}
