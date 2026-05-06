package com.autohubreactive.ai.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeyProvider {

    @Getter
    @Value("${apikey.secret}")
    private String apiKey;

    @Value("${vector-store.system-roles}")
    private String systemRoles;

    public List<String> getSystemRoles() {
        return List.of(systemRoles);
    }

}
