package com.scottlogic.compliance;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ConfigEvent;
import com.scottlogic.compliance.event.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.config.ConfigClient;
import software.amazon.awssdk.services.config.model.Evaluation;
import software.amazon.awssdk.services.config.model.PutEvaluationsRequest;
import software.amazon.awssdk.services.config.model.PutEvaluationsResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import static java.util.Objects.isNull;


public abstract class AbstractChecker implements RequestHandler<ConfigEvent, Void>, Checkable, CheckableVisitor {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    private Context context;

    @Override
    public List<ComplianceResult> getComplianceCheck(ComplianceEvent event) {
        return event.accept(this);
    }

    @Override
    public List<ComplianceResult> getComplianceCheck(ComplianceChangeEvent event) {
        throw new UnsupportedOperationException("Config rule does not support Change Events");
    }

    @Override
    public List<ComplianceResult> getComplianceCheck(CompliancePeriodicEvent event) {
        throw new UnsupportedOperationException("Config rule does not support Periodic Events");
    }

    public Void handleRequest(ConfigEvent event, Context context) {
        this.context = context;

        log("-------------------------Invocation started: --------------" + sdf.format(new Date()));
        log("invokingEvent " + event.getInvokingEvent());
        log("ruleParameters " + event.getRuleParameters());

        ComplianceEvent complianceEvent = ComplianceEventFactory.create(event);

        List<ComplianceResult> complianceResults = getComplianceCheck(complianceEvent);


        PutEvaluationsRequest putEvaluationsRequest = getPutEvaluationsRequest(event, complianceResults);

        log(putEvaluationsRequest.toString());

        ConfigClient configClient = ConfigClient.builder()
                .region(Region.EU_WEST_2)
                .build();
        PutEvaluationsResponse evaluationsResponse = configClient.putEvaluations(putEvaluationsRequest);

        if (evaluationsResponse.failedEvaluations().size() > 0) {
            throw new RuntimeException("Failed to evaluate compliance check");
        }

        log("-------------------------Invocation completed: -------------" + sdf.format(new Date()));
        return null;
    }

    protected void log(String message) {
        if(isNull(context)){
            System.out.println(message);
        }else {
            context.getLogger().log(message);
        }
    }

    private PutEvaluationsRequest getPutEvaluationsRequest(ConfigEvent event, List<ComplianceResult> complianceResults) {
        List<Evaluation> evaluations = complianceResults.stream().map(complianceResult -> Evaluation.builder()
                .complianceResourceId(complianceResult.complianceResourceId)
                .complianceResourceType(complianceResult.complianceResourceType)
                .orderingTimestamp(complianceResult.orderingTimestamp)
                .complianceType(complianceResult.complianceType)
                .build()).collect(Collectors.toList());

        return PutEvaluationsRequest.builder()
                .evaluations(evaluations)
                .resultToken(event.getResultToken())
                .build();
    }


}
