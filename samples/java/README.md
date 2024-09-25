# Export API Java Example

This Java example aggregates and counts builds from the last 24 hours by username.

## Minimum Develocity version

This sample uses version 2 of the Export API, available since Develocity 2021.2.
In order to use it with older Develocity versions, please modify all occurrences of `/build-export/v2` to `/build-export/v1` in `ExportApiJavaExample.java`.

## Setup

To run this sample:

1. Replace the hostname value of the `DEVELOCITY_SERVER_URL` constant in [`ExportApiJavaExample.java`][ExportApiJavaExample] with your Develocity hostname.
3. Set the appropriate authentication environment variables (see below).
2. Run `./gradlew run` from the command line.

### Authentication

We recommend using the Bearer token authentication which has been available since Develocity 2021.1.

#### Bearer token authentication (access key)

1. Create a Develocity access key for a user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set an environment variable locally: `EXPORT_API_ACCESS_KEY` to match the newly created Develocity access key.

#### Basic authentication (user / password)

Non-SAML users can authenticate via basic auth.

1. Create a Develocity user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set two environment variables locally: `EXPORT_API_USER` `EXPORT_API_PASSWORD` to match the newly created Export API user credentials.

## Sample output
```
Streaming builds...
Streaming events for : tlau2phsbmpl4
Streaming events for : gyj4ue5rkfhes
...
Streaming events for : lgxfpx2rniy26
Streaming events for : zakj2zjtnv27k
Streaming events for : 2bbnxgrcq5uco

Results: [user1: 80, user2: 64, build-agent1: 480, user3: 77, build-agent2: 598]
```

[ExportApiJavaExample]: src/main/java/com/develocity/export/ExportApiJavaExample.java
[Export API Access Control]: https://docs.gradle.com/enterprise/export-api/#access_control
