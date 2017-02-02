# Build count by user

In this Java example, builds from the last 24 hours are counted and aggregated by username.

## Concepts

- Build streaming
- Retrieving build events for a build

## Setup

To run this sample:

1. Replace the hostname value of the `GRADLE_ENTERPRISE_SERVER` constant in [`BuildCountByUser.java`][BuildCountByUser] with your Gradle Enterprise hostname.
2. Open a terminal window.
3. Run `./gradlew run` from the command line.

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

[BuildCountByUser]: src/main/java/com/gradle/cloudservices/enterprise/export/BuildCountByUser.java
