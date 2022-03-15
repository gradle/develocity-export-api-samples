# Export API Javascript Example

In this JavaScript example, build durations are calculated and written out to the JavaScript console.

## Minimum Gradle Enterprise version

This sample uses version 2 of the Export API, available since Gradle Enterprise 2021.2.
In order to use it with older Gradle Enterprise versions, please modify the following line in `index.js`:
```
this.baseUrl = `${this.gradleEnterpriseServerUrl}/build-export/v2`
```
must be changed to
```
this.baseUrl = `${this.gradleEnterpriseServerUrl}/build-export/v1`
```

## Setup

To run this sample:

1. Install [node.js].
2. Run `npm install` within this directory.

### Authorisation

We recommend using the Bearer token authentication which has been available since Gradle Enterprise 2021.1.

#### Bearer token authentication (access key)

1. Create a Gradle Enterprise access key for a user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set an environment variable locally: `EXPORT_API_ACCESS_KEY` to match the newly created Gradle Enterprise access key.

#### Basic authentication (user / password)

Non-SAML users can authenticate via basic auth.

1. Create a Gradle Enterprise user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set two environment variables locally: `EXPORT_API_USER` `EXPORT_API_PASSWORD` to match the newly created Export API user credentials.

### Running

Run `npm start https://ge.my-company.com` replacing `ge.my-company.com` with your Gradle Enterprise server URL.

Now start publishing builds to your Gradle Enterprise instance and watch their build durations get written to the console.

### Integrate the sample in an HTML page

If using the script in an HTML page instead of a Node application, remove the first line of [index.js]:

```const EventSourcePolyfill = require('eventsource');```.

And ensure the `EventSourcePolyfill` module is loaded in the page before running the example script by adding:

```
<script type="text/javascript" src="https://unpkg.com/@heroku/eventsource@1.0.7/example/eventsource-polyfill.js"></script>
```

to the `<head>` of the HTML page. 

[index.js]: index.js
[node.js]: https://nodejs.org/
[Export API Access Control]: https://docs.gradle.com/enterprise/export-api/#access_control
