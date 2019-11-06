package com.scottlogic.compliance.event;

import com.amazonaws.services.lambda.runtime.events.ConfigEvent;
import com.scottlogic.compliance.ComplianceResult;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ComplianceChangeEvent implements ComplianceEvent {
    private static final String ARN_PTR= "/configurationItem/configuration/arn";
    private static final String CAPTURE_TIME_PTR = "/configurationItem/configurationItemCaptureTime";
    private static final String RESOURCE_ID_PTR = "/configurationItem/resourceId";
    private static final String RESOURCE_TYPE_PTR = "/configurationItem/resourceType";

    public String arn;
    public String complianceResourceId;
    public String complianceResourceType;
    public Instant orderingTimestamp;
    public Map<String, String> ruleParameters;

    private ComplianceChangeEvent(final String arn,
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

    public static ComplianceChangeEvent extractEvent(ConfigEvent event) {
        Extractor eventExtractor = new Extractor(event.getInvokingEvent());
        Extractor paramsExtractor = new Extractor(event.getRuleParameters());

        String arn = eventExtractor.verify(ARN_PTR);
        String resourceId = eventExtractor.verify(RESOURCE_ID_PTR);
        String resourceType = eventExtractor.verify(RESOURCE_TYPE_PTR);
        Instant orderingTimestamp  = getDate(eventExtractor.verify(CAPTURE_TIME_PTR));
        Map<String, String> ruleParameters = paramsExtractor.asMap();
        return new ComplianceChangeEvent(arn, resourceId, resourceType, orderingTimestamp, ruleParameters);
    }

    private static Instant getDate(String dateString) {
        return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateString));
    }

    public List<ComplianceResult> accept (CheckableVisitor visitor) {
        return visitor.getComplianceCheck(this);
    }
}
