# Build duration logger

In this JavaScript example, build durations are calculated and written out to the JavaScript console.

## Concepts

- Real-time build streaming
- Retrieving build events for a build
- Using data from multiple build events in a calculation

## Setup

To run this sample:

1. Install `node`
2. Run `npm install` within this directory
3. Replace the value of the `gradleEnterpriseServer` constant in [`index.js`][index] with your Gradle Enterprise base url.
4. Run `npm start`

Now start publishing builds to your Gradle Enterprise instance and watch their build durations get written to the console.

[index]: index.js
