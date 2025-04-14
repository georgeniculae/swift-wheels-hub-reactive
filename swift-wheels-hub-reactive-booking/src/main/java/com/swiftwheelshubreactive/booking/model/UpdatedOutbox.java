package com.swiftwheelshubreactive.booking.model;

import com.swiftwheelshubreactive.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "updated_outbox")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UpdatedOutbox {

    @BsonProperty("_id")
    @BsonId
    @Version
    private ObjectId id;

    private Booking content;

    private boolean isCarChanged;

}
