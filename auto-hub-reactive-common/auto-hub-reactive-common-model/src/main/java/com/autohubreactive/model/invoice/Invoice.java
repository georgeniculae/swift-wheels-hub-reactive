package com.autohubreactive.model.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "invoice")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Invoice {

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    private String customerUsername;

    private String customerEmail;

    private ObjectId carId;

    private ObjectId receptionistEmployeeId;

    private ObjectId returnBranchId;

    private ObjectId bookingId;

    private LocalDate dateTo;

    private LocalDate dateFrom;

    private LocalDate carReturnDate;

    private Boolean isVehicleDamaged;

    private BigDecimal rentalCarPrice;

    private BigDecimal damageCost;

    private BigDecimal additionalPayment;

    private BigDecimal totalAmount;

    private String comments;

}
