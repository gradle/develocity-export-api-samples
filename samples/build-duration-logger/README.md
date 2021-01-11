# Build duration logger

In this JavaScript example, build durations are calculated and written out to the JavaScript console.

## Concepts

- Real-time build streaming
- Retrieving build events for a build
- Using data from multiple build events in a calculation

## Setup

To run this sample:

### Installation
1. Install `node`
2. Run `npm install` within this directory

### Configure an export-API user for your Gradle Enterprise instance
1. Create a Gradle Enterprise user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set two environment variables locally: `EXPORT_API_USER` `EXPORT_API_PASSWORD` to match the newly created Export API user credentials.

### Running
4. Run `npm start {server-address}` replacing `{server-address}` with your Gradle Enterprise server URL

Now start publishing builds to your Gradle Enterprise instance and watch their build durations get written to the console.

[index]: index.js
[Export API Access Control]: https://docs.gradle.com/enterprise/export-api/#access_control