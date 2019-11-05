package com.scottlogic.compliance;

import java.util.List;


public interface Checkable {
    List<ComplianceResult> getComplianceCheck(ComplianceEvent ie);
}
