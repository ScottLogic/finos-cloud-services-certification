package com.scottlogic.compliance.event;

import com.amazonaws.services.lambda.runtime.events.ConfigEvent;
import com.scottlogic.compliance.ComplianceResult;

import java.util.List;
import java.util.Map;

public class CompliancePeriodicEvent implements ComplianceEvent {
    public Map<String, String> ruleParameters;

    private CompliancePeriodicEvent(Map<String, String> ruleParameters) {
        this.ruleParameters = ruleParameters;
    }

    static CompliancePeriodicEvent extractEvent(ConfigEvent event) {
        Extractor extractor = new Extractor(event.getRuleParameters());
        Map<String, String> ruleParameters = extractor.asMap();
        return new CompliancePeriodicEvent(ruleParameters);
    }

    public List<ComplianceResult> accept (CheckableVisitor visitor) {
        return visitor.getComplianceCheck(this);
    }
}
