# Export API Java Example

In this Java example, builds from the last 24 hours are counted and aggregated by username.

## Minimum Gradle Enterprise version

This sample uses version 2 of the Export API, available since Gradle Enterprise 2021.2.
In order to use it with older Gradle Enterprise versions, please modify all occurrences of `/build-export/v2` to `/build-export/v1` in `ExportApiJavaExample.java`.

## Setup

To run this sample:

1. Create a Gradle Enterprise user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Replace the hostname value of the `GRADLE_ENTERPRISE_SERVER` constant in [`ExportApiJavaExample.java`][ExportApiJavaExample] with your Gradle Enterprise hostname.
3. Replace `EXPORT_API_USERNAME` and `EXPORT_API_PASSWORD` values to match the newly created Export API user credentials. 
4. Open a terminal window.
5. Run `./gradlew run` from the command line.

Sample output:
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

[ExportApiJavaExample]: src/main/java/com/gradle/enterprise/export/ExportApiJavaExample.java
[Export API Access Control]: https://docs.gradle.com/enterprise/export-api/#access_control