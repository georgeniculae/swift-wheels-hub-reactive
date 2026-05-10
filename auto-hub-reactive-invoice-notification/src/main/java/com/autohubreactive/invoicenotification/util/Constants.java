package com.autohubreactive.invoicenotification.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String CONTENT_TYPE = "text/plain";
    public static final String SUBJECT = "Invoice Notice";
    public static final String ENDPOINT = "mail/send";
    public static final String MAIL_TEMPLATE_FOLDER = "mail-template/";
    public static final String DATA_RESIDENCY = "eu";
    public static final String FILE_NAME = "invoice-notice";
    public static final String MUSTACHE_FILE_EXTENSION = ".mustache";
    public static final String HTML_FILE_EXTENSION = ".html";
    public static final String PDF_TEMPLATE = "pdf-template/";
    public static final String PDF_EXTENSION = ".pdf";
    public static final String INVOICE = "invoice";
    public static final String INVOICE_PDF = "invoice-pdf";
    public static final String INVOICE_FILENAME_PREFIX = "invoice-";
    public static final String APPLICATION_PDF_CONTENT_TYPE = "application/pdf";

}
