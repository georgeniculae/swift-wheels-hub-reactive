package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.invoicenotification.util.TestUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicePdfStorageServiceTest {

    @InjectMocks
    private InvoicePdfStorageService invoicePdfStorageService;

    @Mock
    private ReactiveGridFsTemplate reactiveGridFsTemplate;

    @Test
    void savePdfTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ObjectId objectId = new ObjectId();
        when(reactiveGridFsTemplate.store(any(), anyString(), anyString(), any()))
                .thenReturn(Mono.just(objectId));

        invoicePdfStorageService.savePdf(invoiceResponse, new byte[]{1, 2, 3})
                .as(StepVerifier::create)
                .expectNext(objectId)
                .verifyComplete();
    }

    @Test
    void retrievePdfTest_success() {
        byte[] pdfContent = new byte[]{1, 2, 3};

        GridFSFile gridFSFile = mock(GridFSFile.class);
        ReactiveGridFsResource resource = mock(ReactiveGridFsResource.class);

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(pdfContent);

        when(reactiveGridFsTemplate.findOne(any())).thenReturn(Mono.just(gridFSFile));
        when(reactiveGridFsTemplate.getResource(any(GridFSFile.class))).thenReturn(Mono.just(resource));
        when(resource.getDownloadStream()).thenReturn(Flux.just(dataBuffer));

        invoicePdfStorageService.retrievePdf("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .assertNext(bytes -> {
                    assert bytes.length == 3;
                })
                .verifyComplete();
    }

    @Test
    void retrievePdfTest_notFound() {
        when(reactiveGridFsTemplate.findOne(any())).thenReturn(Mono.empty());

        invoicePdfStorageService.retrievePdf("nonexistent-id")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
