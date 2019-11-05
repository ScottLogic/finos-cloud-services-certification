package com.scottlogic.compliance;

import com.amazonaws.services.lambda.runtime.events.ConfigEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class EventParser {
    private static String MESSAGE_TYPE = "/messageType";
    private static String SCHEDULED_EVENT = "ScheduledNotification";
    private static String CONFIG_CHANGE_EVENT = "ConfigurationItemChangeNotification";

    public static ComplianceEvent extract(ConfigEvent event) {
        Extractor extractor = new Extractor(event.getInvokingEvent());
        String eventType = extractor.verify(MESSAGE_TYPE);

        if (SCHEDULED_EVENT.equals(eventType)) {
            return PeriodicEvent.extractEvent(event);
        } else if (CONFIG_CHANGE_EVENT.equals(eventType)) {
            return ChangeEvent.extractEvent(event);
        } else {
            throw new ParseException("Couldn't recognise event");
        }
    }

    public static class ChangeEvent implements ComplianceEvent {
        private static final String ARN_PTR= "/configurationItem/configuration/arn";
        private static final String CAPTURE_TIME_PTR = "/configurationItem/configurationItemCaptureTime";
        private static final String RESOURCE_ID_PTR = "/configurationItem/resourceId";
        private static final String RESOURCE_TYPE_PTR = "/configurationItem/resourceType";

        public String arn;
        public String complianceResourceId;
        public String complianceResourceType;
        public Instant orderingTimestamp;
        public Map<String, String> ruleParameters;

        private ChangeEvent(final String arn,
                            final String complianceResourceId,
                            final String complianceResourceType,
                            final Instant orderingTimestamp,
                            final Map<String, String> ruleParameters) {
            this.arn = arn;
            this.complianceResourceId = complianceResourceId;
            this.complianceResourceType = complianceResourceType;
            this.orderingTimestamp = orderingTimestamp;
            this.ruleParameters = ruleParameters;
        }

        public static ChangeEvent extractEvent(ConfigEvent event) {
            Extractor eventExtractor = new Extractor(event.getInvokingEvent());
            Extractor paramsExtractor = new Extractor(event.getRuleParameters());

            String arn = eventExtractor.verify(ARN_PTR);
            String resourceId = eventExtractor.verify(RESOURCE_ID_PTR);
            String resourceType = eventExtractor.verify(RESOURCE_TYPE_PTR);
            Instant orderingTimestamp  = getDate(eventExtractor.verify(CAPTURE_TIME_PTR));
            Map<String, String> ruleParameters = paramsExtractor.asMap();
            return new ChangeEvent(arn, resourceId, resourceType, orderingTimestamp, ruleParameters);
        }
    }

    public static class PeriodicEvent implements ComplianceEvent {
        public Map<String, String> ruleParameters;

        private PeriodicEvent(Map<String, String> ruleParameters) {
            this.ruleParameters = ruleParameters;
        }

        public static PeriodicEvent extractEvent(ConfigEvent event) {
            Extractor extractor = new Extractor(event.getRuleParameters());
            Map<String, String> ruleParameters = extractor.asMap();
            return new PeriodicEvent(ruleParameters);
        }
    }

    private static class Extractor {
        private final JsonNode jsonNode;

        public Extractor(String jsonEventStr) {
            if (jsonEventStr == null) {
                jsonEventStr = "{}";
            }

            try {
                jsonNode = new ObjectMapper().readTree(jsonEventStr);
            } catch (IOException e) {
                throw new ParseException("Failed to parse JSON Node from " + jsonEventStr, e);
            }
        }

        String verify(String nodePtr) {
            return ofNullable(jsonNode.at(nodePtr).textValue())
                    .orElseThrow(() -> new ParseException(String.format("Failed to extract value for \"%s\" from InvokingEvent", nodePtr)));
        }

        Map<String, String> asMap() {
            Map<String, String> map = new HashMap<>();
            jsonNode.fields().forEachRemaining(kv -> map.put(kv.getKey(), kv.getValue().textValue()));
            return map;
        }
    }

    private static Instant getDate(String dateString) {
        return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateString));
    }
}
