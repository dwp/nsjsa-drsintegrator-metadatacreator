package uk.gov.dwp.components.drs.creator.domain;

import com.fasterxml.jackson.databind.JsonNode;

public class CreatorPayload {
    private JsonNode metadata;
    private JsonNode payload;

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
