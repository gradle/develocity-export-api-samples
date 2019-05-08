# Real-time streaming build duration chart

In this JavaScript example, build durations are calculated and plotted on a chart in real-time.

The chart is created with the fantastic [Flot](http://www.flotcharts.org/) library.

## Concepts

- Real-time build streaming
- Retrieving build events for a build
- Using data from multiple build events in a calculation

## Setup

To run this sample:

1. Replace the value of the `gradleEnterpriseServer` constant in [`index.html`](index.html#L19) with your Gradle Enterprise base url.
1. Open [`index.html`](index.html) in Firefox or Chrome (there is currently an issue with Safari)

Now start publishing builds to your Gradle Enterprise instance and watch the chart update in real-time.

### Credentials

If your Gradle Enterprise installation requires authentication, change `withCredentials` in [`index.html`](index.html#L20) to `true`.

The browser will ask you for your credentials in a basic-auth window when the page is viewed.
