package com.scottlogic.compliance;

import com.scottlogic.compliance.event.ComplianceEvent;

import java.util.List;


public interface Checkable {
    List<ComplianceResult> getComplianceCheck(ComplianceEvent ie);
}
