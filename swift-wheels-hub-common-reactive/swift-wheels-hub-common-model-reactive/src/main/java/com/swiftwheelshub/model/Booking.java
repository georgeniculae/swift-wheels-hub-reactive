package com.swiftwheelshub.model;

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

import java.time.LocalDate;

@Document(collection = "booking")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Booking {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @NonNull
    private LocalDate dateOfBooking;

    private BookingStatus status;

    private String customerUsername;

    private String customerEmail;

    private ObjectId carId;

    @NonNull
    private LocalDate dateFrom;

    @NonNull
    private LocalDate dateTo;

    private Double amount;

    private Double rentalCarPrice;

    private ObjectId rentalBranchId;

    private ObjectId returnBranchId;

}
