# FINOS Cloud Service Certification - Prototype Implementation 

The FINOS [Cloud Service Certification Project](https://finosfoundation.atlassian.net/wiki/spaces/FDX/pages/904626436/Cloud+Service+Certification+Project) has the following missions:

> The mission of the Cloud Service Certification Working Group is to accelerate the development, deployment, and adoption of a common set of controls and tests for cloud services.

These controls are currently documented in Word / markdown format within the project's respective GitHub repo, [finos/cloud-service-certification](https://github.com/finos/cloud-service-certification).

## Prototype Implementation

This prototype seeks to automate these compliance check so that they can be validated rapidly and with repeatability. They make use of AWS Config, a service which provides an event-driven framework for detecting changes in configuration and executing checks (as Lambda functions). It also provides a dashboard with metrics and reporting. 

It is anticipated that each test case identified by the Cloud Service Certification Project will be implemented as a Lambda Function.

## Licence

Copyright 2019 Scott Logic

Distributed under the Apache License, Version 2.0.

SPDX-License-Identifier: Apache-2.0

