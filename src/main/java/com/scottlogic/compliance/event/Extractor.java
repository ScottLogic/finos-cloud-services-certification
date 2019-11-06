package com.scottlogic.compliance.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

class Extractor {
    private final JsonNode jsonNode;

    public Extractor(String jsonEventStr) {
        if (jsonEventStr == null) {
            jsonEventStr = "{}";
        }

        try {
            jsonNode = new ObjectMapper().readTree(jsonEventStr);
        } catch (IOException e) {
            throw new ComplianceEventParseException("Failed to parse JSON Node from " + jsonEventStr, e);
        }
    }

    String verify(String nodePtr) {
        return ofNullable(jsonNode.at(nodePtr).textValue())
                .orElseThrow(() -> new ComplianceEventParseException(String.format("Failed to extract value for \"%s\" from InvokingEvent", nodePtr)));
    }

    Map<String, String> asMap() {
        Map<String, String> map = new HashMap<>();
        jsonNode.fields().forEachRemaining(kv -> map.put(kv.getKey(), kv.getValue().textValue()));
        return Collections.unmodifiableMap(map);
    }
}
