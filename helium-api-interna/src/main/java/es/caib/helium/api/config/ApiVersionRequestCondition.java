package es.caib.helium.api.config;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private final String apiVersion;

    public ApiVersionRequestCondition(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        // Més condició particular comparteix la versió
        return new ApiVersionRequestCondition(other.apiVersion);
    }

    @Override
    @Nullable
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        String version = request.getHeader("X-API-Version");
        if (version == null || !version.equals(this.apiVersion)) {
            return null;
        }
        return this;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        // Compara les versions (reverse order) gran a petit
        return other.apiVersion.compareTo(this.apiVersion);
    }
}