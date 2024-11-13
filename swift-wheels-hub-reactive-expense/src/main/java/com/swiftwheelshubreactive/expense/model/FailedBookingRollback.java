package com.swiftwheelshubreactive.expense.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "failed_booking_rollback")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FailedBookingRollback {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;
    private String bookingId;

    public FailedBookingRollback(String bookingId) {
        this.bookingId = bookingId;
    }

}
