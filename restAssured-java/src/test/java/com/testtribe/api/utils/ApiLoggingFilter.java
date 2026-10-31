package com.testtribe.api.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Single source of API logs: request and response, with secrets masked. */
public class ApiLoggingFilter implements Filter {

    private static final Logger log = LogManager.getLogger(ApiLoggingFilter.class);
    private static final int MAX_BODY = 5000;

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        log.info("--> {} {}", req.getMethod(), req.getURI());
        if (log.isDebugEnabled()) {
            log.debug("Request headers: {}", LogMasker.mask(String.valueOf(req.getHeaders())));
            Object body = req.getBody();
            log.debug("Request body: {}", prepare(body == null ? null : body.toString()));
        }

        Response response = ctx.next(req, res);

        log.info("<-- {} {} {} ({} ms)", response.getStatusCode(), req.getMethod(),
                req.getURI(), response.getTime());
        if (log.isDebugEnabled()) {
            log.debug("Response headers: {}", LogMasker.mask(String.valueOf(response.getHeaders())));
            log.debug("Response body: {}", prepare(response.asString()));
        }
        if (response.getStatusCode() >= 400) {
            log.warn("Error response body: {}", prepare(response.asString()));
        }
        return response;
    }

    private static String prepare(String body) {
        String masked = LogMasker.mask(body);
        if (masked != null && masked.length() > MAX_BODY) {
            return masked.substring(0, MAX_BODY) + "...[truncated]";
        }
        return masked;
    }
}
