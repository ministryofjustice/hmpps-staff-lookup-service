# hmpps-staff-lookup-service
[![repo standards badge](https://img.shields.io/badge/dynamic/json?color=blue&style=flat&logo=github&label=MoJ%20Compliant&query=%24.result&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv1%2Fcompliant_public_repositories%2Fhmpps-staff-lookup-service)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-github-repositories.html#hmpps-staff-lookup-service "Link to report")
[![CircleCI](https://circleci.com/gh/ministryofjustice/hmpps-staff-lookup-service/tree/main.svg?style=svg)](https://circleci.com/gh/ministryofjustice/hmpps-staff-lookup-service)
[![Docker Repository on Quay](https://quay.io/repository/hmpps/hmpps-staff-lookup-service/status "Docker Repository on Quay")](https://quay.io/repository/hmpps/hmpps-staff-lookup-service)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://hmpps-staff-lookup-service-dev.hmpps.service.justice.gov.uk/webjars/swagger-ui/index.html?configUrl=/v3/api-docs)

This is the HMPPS Staff lookup service.

## What it does

This API has been created to allow consumers to search for information relating to HMPPS staff.

Currently there is only one endpoint, which searches for users with e-mails that begin with the given characters. 