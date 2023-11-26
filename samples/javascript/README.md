# Export API Javascript Example

In this JavaScript example, build durations are calculated and written out to the JavaScript console.

## Minimum Develocity version

This sample uses version 2 of the Export API, available since Develocity 2021.2.
In order to use it with older Develocity versions, please modify the following line in `index.js`:
```
this.baseUrl = `${this.develocityServerUrl}/build-export/v2`
```
must be changed to
```
this.baseUrl = `${this.develocityServerUrl}/build-export/v1`
```

## Setup

To run this sample:

1. Install [node.js].
2. Run `npm install` within this directory.

### Authorisation

We recommend using the Bearer token authentication which has been available since Develocity 2021.1.

#### Bearer token authentication (access key)

1. Create a Develocity access key for a user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set an environment variable locally: `EXPORT_API_ACCESS_KEY` to match the newly created Develocity access key.

#### Basic authentication (user / password)

Non-SAML users can authenticate via basic auth.

1. Create a Develocity user with the `Export API` role as described in the [Export API Access Control] documentation.
2. Set two environment variables locally: `EXPORT_API_USER` `EXPORT_API_PASSWORD` to match the newly created Export API user credentials.

### Running

Run `npm start https://develocity.example.com` replacing `develocity.example.com` with your Develocity server URL.

Alternatively to setting environment variables, credentials can be passed to the Node process by running:

``` EXPORT_API_ACCESS_KEY=<KEYVALUE> npm start https://develocity.example.com ``` replacing `<KEYVALUE>` with the access key or instead using `EXPORT_API_USER` and `EXPORT_API_PASSWORD` in the case of Basic authentication.

Now start publishing builds to your Develocity instance and watch their build durations get written to the console.

### Integrate the sample in an HTML page

If using the script in an HTML page instead of a Node application, remove the first line of [index.js]:

```const EventSourcePolyfill = require('eventsource');```.

And ensure the `EventSourcePolyfill` module is loaded in the page before running the example script by adding:

```
<script type="text/javascript" src="https://unpkg.com/eventsource@2.0.2/example/eventsource-polyfill.js"></script>
```

to the `<head>` of the HTML page. 

[index.js]: index.js
[node.js]: https://nodejs.org/
[Export API Access Control]: https://docs.gradle.com/enterprise/export-api/#access_control
