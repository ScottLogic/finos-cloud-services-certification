package com.scottlogic.compliance;

import software.amazon.awssdk.services.config.model.ComplianceType;

import java.time.Instant;

public class ComplianceResult {
    public String complianceResourceId;
    public String complianceResourceType;
    public Instant orderingTimestamp;
    public ComplianceType complianceType;

    public ComplianceResult(String complianceResourceId, String complianceResourceType, Instant orderingTimestamp, ComplianceType complianceType) {
        this.complianceResourceId = complianceResourceId;
        this.complianceResourceType = complianceResourceType;
        this.orderingTimestamp = orderingTimestamp;
        this.complianceType = complianceType;
    }
}
