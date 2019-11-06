package com.scottlogic.compliance.dynamodb;

import com.amazonaws.services.lambda.runtime.events.ConfigEvent;
import com.scottlogic.compliance.AbstractChecker;
import com.scottlogic.compliance.event.ComplianceChangeEvent;
import com.scottlogic.compliance.ComplianceResult;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.config.model.ComplianceType;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.EvaluationResult;
import software.amazon.awssdk.services.iam.model.SimulatePrincipalPolicyRequest;
import software.amazon.awssdk.services.iam.model.SimulatePrincipalPolicyResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class SecureTransportCheck extends AbstractChecker {

    public static void main(String[] args) {
        String testEventStr = "{\"configurationItemDiff\":null,\"configurationItem\":{\"relatedEvents\":[],\"relationships\":[],\"configuration\":{\"path\":\"/\",\"userName\":\"serverless-test\",\"userId\":\"AIDAQO5QBPYFF4ULQRVCE\",\"arn\":\"arn:aws:iam::032044580362:user/serverless-test\",\"createDate\":\"2019-06-25T08:37:33.000Z\",\"userPolicyList\":[],\"groupList\":[],\"attachedManagedPolicies\":[{\"policyName\":\"AdministratorAccess\",\"policyArn\":\"arn:aws:iam::aws:policy/AdministratorAccess\"}],\"permissionsBoundary\":null,\"tags\":[]},\"supplementaryConfiguration\":{},\"tags\":{},\"configurationItemVersion\":\"1.3\",\"configurationItemCaptureTime\":\"2019-10-30T11:39:10.807Z\",\"configurationStateId\":1572435550807,\"awsAccountId\":\"032044580362\",\"configurationItemStatus\":\"ResourceDiscovered\",\"resourceType\":\"AWS::IAM::User\",\"resourceId\":\"AIDAQO5QBPYFF4ULQRVCE\",\"resourceName\":\"serverless-test\",\"ARN\":\"arn:aws:iam::032044580362:user/serverless-test\",\"awsRegion\":\"global\",\"availabilityZone\":\"Not Applicable\",\"configurationStateMd5Hash\":\"\",\"resourceCreationTime\":\"2019-06-25T08:37:33.000Z\"},\"notificationCreationTime\":\"2019-10-30T14:27:59.587Z\",\"messageType\":\"ConfigurationItemChangeNotification\",\"recordVersion\":\"1.3\"}";
        String resultToken = "eyJlbmNyeXB0ZWREYXRhIjpbLTU0LC0xNiwyOCw1LDkzLC0xMTEsMzIsLTkxLDU4LDEyNywxMDEsLTExOCwtOTIsMzQsNzUsMTA0LC0xMSwxMTAsMTEzLDEzLC0xMDUsLTEwMSwzOCwxMDIsLTI5LC0xMjgsNzksLTgzLC02OCwtMjAsNDMsLTgwLDg4LC0xMTAsNDgsOTAsNzYsNSwtMTAzLC02MiwtMjIsLTY4LDExNiwtMTA0LC03NiwxMTksLTEwNCwtMTQsNzUsLTYwLDYzLC02MSw2OCwxMDEsOSwtMzcsLTI0LDY4LC04NiwtMiwxMTIsOTgsLTQzLC00OCw0MywtMTE4LDI0LDExNywtMTIwLDEwOCwyMSw5MiwtMTE3LC03NywtODAsMTA2LDgyLC01NywxMjIsNzYsMjgsLTEwOCwxMTMsLTE5LDgxLDc1LC0yOSw4Nyw1NywzNSwxMDMsLTE5LDEyNyw4Nyw5NCwtMTAzLDU5LDU5LDIsLTgxLC00OSwtNzgsLTc2LC03MiwtNjAsNjQsLTE4LDUsMTIsLTY1LDEwOSwtNTksLTYsLTk4LDc4LDMzLC05NSw3NiwtNjMsLTg4LDEyNSw0Miw5Nyw4MSwyLDIzLC02NywyMCwtNDksNTUsNDMsLTUzLDEsLTQ4LC0xMywtMTA4LC00NSwtMTI4LC0xMTksLTk1LC03NiwtOTAsLTEzLC0zOSwxMywtMzgsLTg0LDkzLDgxLC0xNCwtMTI2LC0xMTQsLTk0LC01NiwtODYsLTE5LDcxLDEwOSwtMjMsMTI2LDk4LC0xMDEsMTE1LDM0LC01NiwtOTcsLTU0LDEwMSwtMTYsMzUsLTU4LDc0LDEwNSwxMTYsOTYsNTgsLTYsLTQ4LDg0LC01NywtNjMsMTE0LDk5LDE0LDEyNywxMjEsLTkyLDEyMywtNjAsLTMwLDUyLDEwNSwtMTI1LC0xMjQsMTE0LDI0LC05OCwxMTgsMTExLDE0LC0xMTcsNzIsLTU3LC03NCwtMTcsNjksNDYsLTUwLDExOCwtMjksLTUwLDgyLC0xLDU1LC0xMTEsMTI1LC03NywyMyw2MCwtNDcsNSwxMjMsLTEwOCwzMCwtNjYsOTUsLTU4LC02OSw0OCw1NywtMTE2LC0zLDExLC02NSw3OSwtNTMsLTExNSwxMDUsMTIyLDUxLC0xNiwtMzksLTE3LC0xMjIsNTUsMTA5LC02Niw5MCwtOTMsNTAsLTg3LC01Niw1MiwtNDksNjAsOTQsLTEwNCwtNzcsMTE5LDExOSwtMTIsLTksLTM5LC01MiwtMTA2LC0xMTQsNjgsLTMyLDcsNTYsNTEsLTU4LDAsLTExMiwtOTEsMjIsNzQsNDUsLTYsLTEyMCwtNTcsMTQsNTIsLTg1LDY5LC0yMSwxMjIsMTE4LC02OSwtNjQsLTE2LC0xMDcsLTkwLDEwNiwtMzYsMzUsLTg5LDY5LDExNSwtNzAsNzksMzcsMTA4LC0yNCwtNzAsLTc0LC0xNSwtMTMsLTQ2LDg5LC0zLC01NSw2OCwxMiwxMDEsNzMsODMsMTAyLC0zMCwyLC05MSwxMTQsLTQwLDg5LDQ1LDM3LC03Nyw4NiwxMTEsLTk3LDMxLDYyLC04NSw2NywtOTIsLTE5LDE2LDE4LC0yOSwxMjUsMTExLC03MCwxMTYsMTAyLC0xMTIsLTMxLDgxLC03NiwtNjEsLTI5LDg0LC0zOCw3OCw3MiwtODMsLTY1LC0xMDIsLTUzLDQxLDM2LDIxLC0xMDYsLTExMSwtMzcsLTIsLTQ1LC0yOCwyNSwtMTIyLDExOCwtNDcsOTIsLTkwLDg2LDkxLC0xMDksLTE4LC01OSwtMzMsMTIwLDExNiwxMjQsLTEwMSw4MSw3Myw4NywtMTAsNjIsLTEwNiwtNDUsLTg4LDEyNCwzOCwzNywtNzUsMTE4LC0yLC0xMTgsLTEyNywtMzAsMTYsODcsMjgsNjIsLTE1LC0xMjEsLTYyLC03LC02NCwtOTMsLTExOCwyNiw3OSw5MCwtODcsLTExOSwtMTAyLDIzLC0xLDU5LC00OSwxMjMsMTIyLDEwMiwtMTI4LC04NCwtMTAwLC04OSwxMDQsNzIsLTE2LC01MCw5NiwtMSwtMTIzLDk1LDcyLC0zOSwtNzYsMiwxMjQsOTEsMzEsNDQsLTE4LC02NSw0MCw2OCw5MywtMzgsLTk4LDE5LC01MSw4NCwtNDEsNzUsLTg0LC03MiwxMjQsOTgsLTEyNCwtMTEyLC0xMjUsLTcsNjQsLTc1LDIsLTU2LDk2LDExNSwtMjAsLTExMSwxNywtNjcsLTMyLC0yNiw5NCw2NSwtMTUsMTA3LDkxLDk1LC0xMTgsLTEwMCwtMzgsODEsLTUwLC0yOSwzLC01OCwtNzIsLTQsLTUyLC0xMDAsMTYsNTUsMjksLTExNCwtNjMsMywtNTEsMTEyLC0xMTcsNDQsLTMyLC0zMyw1OCw1MywtMTE4LDI3LC03LC0xNywtMjMsMjQsLTk0LDI4LC0yNywtOTYsNDgsLTExMiw3NiwtMzIsOTcsLTYxLC0xLC01MSwtNTEsOTQsLTE1LDI3LDUxLC01N10sIm1hdGVyaWFsU2V0U2VyaWFsTnVtYmVyIjoxLCJpdlBhcmFtZXRlclNwZWMiOnsiaXYiOlszMiwtMTE5LDM4LDEwNiwxMjEsLTEyNCwtNjUsNzcsMjksOTIsLTgsLTQwLDIzLDI3LDEzLDc3XX19";
        ConfigEvent testConfigEvent = new ConfigEvent().withInvokingEvent(testEventStr).withResultToken(resultToken);
        SecureTransportCheck tester = new SecureTransportCheck();
        tester.handleRequest(testConfigEvent, null);
    }

    public List<ComplianceResult> getComplianceCheck(ComplianceChangeEvent event) {
        IamClient iam = IamClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();
        SimulatePrincipalPolicyResponse response = iam.simulatePrincipalPolicy(SimulatePrincipalPolicyRequest.builder()
                .policySourceArn(event.arn)
                .actionNames("dynamodb:*")
                .build());

        log(response.toString());
        List<EvaluationResult> results = response.evaluationResults();

        if (results.isEmpty()) {
            throw new RuntimeException("Failed in execution of compliance check!");
        }

        boolean requiresSecureTransport = results.get(0).missingContextValues().contains("aws:SecureTransport");

        ComplianceType complianceType = requiresSecureTransport ? ComplianceType.COMPLIANT : ComplianceType.NON_COMPLIANT;
        ComplianceResult complianceResult = new ComplianceResult(
                event.complianceResourceId,
                event.complianceResourceType,
                event.orderingTimestamp,
                complianceType
        );
        return Collections.singletonList(complianceResult);
    }
}
