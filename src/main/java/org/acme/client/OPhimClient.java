package org.acme.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface OPhimClient {
    JsonNode get(String path, Map<String, String> queryParams);

}
