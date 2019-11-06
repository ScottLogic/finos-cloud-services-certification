package com.scottlogic.compliance.event;

import com.amazonaws.services.lambda.runtime.events.ConfigEvent;

public class ComplianceEventFactory {
    private static String MESSAGE_TYPE = "/messageType";
    private static String SCHEDULED_EVENT = "ScheduledNotification";
    private static String CONFIG_CHANGE_EVENT = "ConfigurationItemChangeNotification";

    public static ComplianceEvent create(ConfigEvent event) {
        Extractor extractor = new Extractor(event.getInvokingEvent());
        String eventType = extractor.verify(MESSAGE_TYPE);

        if (SCHEDULED_EVENT.equals(eventType)) {
            return CompliancePeriodicEvent.extractEvent(event);
        } else if (CONFIG_CHANGE_EVENT.equals(eventType)) {
            return ComplianceChangeEvent.extractEvent(event);
        } else {
            throw new ComplianceEventParseException("Couldn't recognise event");
        }
    }


}
