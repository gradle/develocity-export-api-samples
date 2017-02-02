# Real-time streaming build duration chart

In this JavaScript example, build durations are calculated and plotted on a chart in real-time.

The chart is created with the fantastic [Flot][flot] library.

## Concepts

- Real-time build streaming
- Retrieving build events for a build
- Using data from multiple build events in a calculation

## Setup

To run this sample:

1. Replace the value of the `gradleEnterpriseServer` constant in [`index.html`][index] with your Gradle Enterprise base url.
2. Open [`index.html`][index] in your favorite browser.

Now start publishing builds to your Gradle Enterprise instance and watch the chart update in real-time.

[index]: index.html
[flot]: http://www.flotcharts.org/
