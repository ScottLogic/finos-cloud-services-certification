package com.scottlogic.compliance;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ConfigEvent;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.config.ConfigClient;
import software.amazon.awssdk.services.config.model.ComplianceType;
import software.amazon.awssdk.services.config.model.Evaluation;
import software.amazon.awssdk.services.config.model.PutEvaluationsRequest;
import software.amazon.awssdk.services.config.model.PutEvaluationsResponse;

import java.text.SimpleDateFormat;
import java.util.Date;


import static java.util.Objects.isNull;


public abstract class AbstractChecker implements RequestHandler<ConfigEvent, Void>, Checkable {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    private Context context;



    public Void handleRequest(ConfigEvent event, Context context) {
        this.context = context;

        log("-------------------------Invocation started: --------------" + sdf.format(new Date()));
        log("invokingEvent " + event.toString());

        InvokingEvent invokingEvent = InvokingEvent.from(event.getInvokingEvent());

        ComplianceType complianceResult = getComplianceCheck(invokingEvent);


        PutEvaluationsRequest putEvaluationsRequest = getPutEvaluationsRequest(event, invokingEvent, complianceResult);

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

    private PutEvaluationsRequest getPutEvaluationsRequest(ConfigEvent event, InvokingEvent invokingEvent, ComplianceType complianceResult) {
        Evaluation evaluation = Evaluation.builder()
                .complianceResourceId(invokingEvent.complianceResourceId)
                .complianceResourceType(invokingEvent.complianceResourceType)
                .orderingTimestamp(invokingEvent.orderingTimestamp)
                .complianceType(complianceResult)
                .build();

        return PutEvaluationsRequest.builder()
                .evaluations(evaluation)
                .resultToken(event.getResultToken())
                .build();
    }


}
