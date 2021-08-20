package es.caib.helium.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private ErrorDecoder delegate = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        HttpHeaders responseHeaders = new HttpHeaders();
        if (response.headers() != null && !response.headers().isEmpty()){
            response.headers().entrySet().stream()
                    .forEach(entry -> responseHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue())));
        }

        HttpStatus statusCode = HttpStatus.valueOf(response.status());
        String statusText = response.reason();

        log.trace("Error: [" + statusCode.value() + "] " + statusText);

        byte[] responseBody;
        try {
            responseBody = response.body() != null ? IOUtils.toByteArray(response.body().asInputStream()) : "".getBytes();
        } catch (
                IOException e) {
            throw new RuntimeException("Failed to process response body.", e);
        }

        if (statusCode.is4xxClientError()) {
            return new HttpClientErrorException(statusCode, statusText, responseHeaders, responseBody, null);
        }

        if (statusCode.is5xxServerError()) {
            return new HttpServerErrorException(statusCode, statusText, responseHeaders, responseBody, null);
        }
        return delegate.decode(methodKey, response);
    }
}
