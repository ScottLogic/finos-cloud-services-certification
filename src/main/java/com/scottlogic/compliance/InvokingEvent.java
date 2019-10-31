package com.scottlogic.compliance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;


import static java.util.Optional.ofNullable;

public class InvokingEvent {

    private static final String ARN_PTR= "/configurationItem/configuration/arn";
    private static final String CAPTURE_TIME_PTR = "/configurationItem/configurationItemCaptureTime";
    private static final String RESOURCE_ID_PTR = "/configurationItem/resourceId";
    private static final String RESOURCE_TYPE_PTR = "/configurationItem/resourceType";


    public final String arn;
    public final String complianceResourceId;
    public final String complianceResourceType;
    public final Instant orderingTimestamp;

    private InvokingEvent(String arn, String complianceResourceId, String complianceResourceType, Instant orderingTimestamp) {
        this.arn = arn;
        this.complianceResourceId = complianceResourceId;
        this.complianceResourceType = complianceResourceType;
        this.orderingTimestamp = orderingTimestamp;
    }

    public static InvokingEvent from(String jsonEventStr) {
        Extractor extractor = new Extractor(jsonEventStr);

        String arn = extractor.verify(ARN_PTR);
        String resourceId = extractor.verify(RESOURCE_ID_PTR);
        String resourceType = extractor.verify(RESOURCE_TYPE_PTR);
        Instant orderingTimestamp  = getDate(extractor.verify(CAPTURE_TIME_PTR));

        return new InvokingEvent(arn, resourceId, resourceType, orderingTimestamp);
    }

    private static class Extractor {
        private final JsonNode invokingEvent;

        public Extractor(String jsonEventStr) {
            try {
                invokingEvent = new ObjectMapper().readTree(jsonEventStr);
            } catch (IOException e) {
                throw new ParseException("Failed to parse Invoking Event from " + jsonEventStr, e);
            }
        }

        String verify(String nodePtr){
            return ofNullable(invokingEvent.at(nodePtr).textValue())
                    .orElseThrow(() -> new ParseException(String.format("Failed to extract value for \"%s\" from InvokingEvent", nodePtr)));
        }
    }


    private static Instant getDate(String dateString) {
        return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateString));
    }

}
