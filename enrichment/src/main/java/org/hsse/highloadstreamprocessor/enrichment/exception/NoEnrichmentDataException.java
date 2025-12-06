package org.hsse.highloadstreamprocessor.enrichment.exception;

public class NoEnrichmentDataException extends RuntimeException {
    public NoEnrichmentDataException(String message) {
        super(message);
    }
}
