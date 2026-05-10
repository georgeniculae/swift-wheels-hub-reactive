package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.invoicenotification.util.Constants;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InvoicePdfStorageService {

    private final ReactiveGridFsTemplate reactiveGridFsTemplate;

    public Mono<ObjectId> savePdf(InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        Document metadata = new Document()
                .append("invoiceId", invoiceResponse.id())
                .append("customerUsername", invoiceResponse.customerUsername())
                .append("customerEmail", invoiceResponse.customerEmail())
                .append("createdAt", Instant.now().toString());

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(pdfBytes);

        return reactiveGridFsTemplate.store(
                Flux.just(dataBuffer),
                Constants.INVOICE_FILENAME_PREFIX + invoiceResponse.id() + Constants.PDF_EXTENSION,
                Constants.APPLICATION_PDF_CONTENT_TYPE,
                metadata
        );
    }

    public Mono<byte[]> retrievePdf(String invoiceId) {
        return reactiveGridFsTemplate.findOne(getQuery(invoiceId))
                .switchIfEmpty(Mono.error(new AutoHubResponseStatusException(HttpStatus.NOT_FOUND, "PDF not found for invoice: " + invoiceId)))
                .flatMap(reactiveGridFsTemplate::getResource)
                .flatMap(resource -> DataBufferUtils.join(resource.getDownloadStream()))
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return bytes;
                });
    }

    private Query getQuery(String invoiceId) {
        return new Query(Criteria.where("metadata.invoiceId").is(invoiceId));
    }

}
