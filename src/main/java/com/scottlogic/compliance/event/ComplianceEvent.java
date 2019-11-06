package com.scottlogic.compliance.event;

import com.scottlogic.compliance.ComplianceResult;

import java.util.List;

public interface ComplianceEvent {
    List<ComplianceResult> accept (CheckableVisitor visitor);
}
