package com.scottlogic.compliance.event;

import com.scottlogic.compliance.ComplianceResult;

import java.util.List;

public interface CheckableVisitor {

    List<ComplianceResult> getComplianceCheck(ComplianceChangeEvent cce);
    List<ComplianceResult> getComplianceCheck(CompliancePeriodicEvent cpe);
}
