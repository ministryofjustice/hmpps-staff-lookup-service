# hmpps-staff-lookup-service
[![repo standards badge](https://img.shields.io/badge/dynamic/json?color=blue&style=flat&logo=github&label=MoJ%20Compliant&query=%24.result&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv1%2Fcompliant_public_repositories%2Fhmpps-staff-lookup-service)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-github-repositories.html#hmpps-staff-lookup-service "Link to report")
[![CircleCI](https://circleci.com/gh/ministryofjustice/hmpps-staff-lookup-service/tree/main.svg?style=svg)](https://circleci.com/gh/ministryofjustice/hmpps-staff-lookup-service)
[![Docker Repository on Quay](https://quay.io/repository/hmpps/hmpps-staff-lookup-service/status "Docker Repository on Quay")](https://quay.io/repository/hmpps/hmpps-staff-lookup-service)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://hmpps-staff-lookup-service-dev.hmpps.service.justice.gov.uk/swagger-ui.html)

This is the HMPPS Staff lookup service.

## What it does

This API has been created to allow consumers to search for information relating to HMPPS staff.

Currently there is only one endpoint, which searches for users with e-mails that begin with the given characters. 

### Support

The main endpoint of this API invokes a full re-build of the database with the latest data from Microsoft AD.
It will only re-build if there has not been a successful build already done today.
This check can be overridden by the `?checkBuildRequired=$` request parameter being set the `false`,
but it is important to check there isn't a re-index currently in progress before doing this (see below).

Note that the re-index is done asynchronously, i.e. the endpoint will return immediately regardless of whether a re-index was attempted.

The asynchronous nature of this service makes it harder to determine it's status,
so here are some common scenarios and how we might deal with them:

#### How do we know if re-indexing currently in progress?

One way to do this is to inspect the shadow table in the database.
To do this you need to connect to the database - we usually use port-forwarding to achieve this.
If the shadow table is empty or being actively updated then we can assume a re-index is in progres.

An alternative method is to inspect the App Insights logs.
If a request has been made to do a full re-index but there is not a more recent custom event to say it has completed,
then we can assume a re-index is in progress.
Additionally, we can check the dependencies to see when the last requests to microsoft graph were made.

#### How can we ascertain if the latest re-indexing failed?

Every re-indexing should raise a customEvent in App Insights which will indicate whether it failed and how long it took.
Furthermore, any exceptions that occur wil be sent to App Insights.

#### How can I cancel a re-indexing?

Currently the only way to do this is to identify the pod on which the re-indexing is happening and kill it.
This can be achieved using K8S by simply deleting the pod.

## Testing
This allows you to test that the scripts work locally against postgresql in docker
```shell
docker compose up -d
./gradlew clean check
```

## Code style & formatting
```shell
./gradlew ktlintApplyToIdea addKtlintFormatGitPreCommitHook
```
will apply ktlint styles to intellij and also add a pre-commit hook to format all changed kotlin files.

