package com.scottlogic.compliance;

import software.amazon.awssdk.services.config.model.ComplianceType;


public interface Checkable {
    ComplianceType getComplianceCheck(InvokingEvent ie);
}
