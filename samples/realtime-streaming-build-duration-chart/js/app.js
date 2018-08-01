const buildSuccessData = [];
let buildSuccessDataReceived = false;
let buildSuccessPlot;

const buildDurationDataSuccess = [];
const buildDurationDataFailure = [];
let buildDurationPlot;

function initializeCharts(buildSuccessChart, buildDurationChart) {
    _initializeBuildSuccessChart(buildSuccessChart);
    _initializeBuildDurationChart(buildDurationChart);
    _update();
}

function updateCharts(startTime, endTime, success) {
    _updateBuildSuccessChart(success);
    _updateBuildDurationChart(startTime, endTime, success);
}

function buildStreamUrl(gradleEnterpriseServer) {
    return `${gradleEnterpriseServer}/build-export/v1/builds/since/now?stream`;
}

function buildEventStreamUrl(gradleEnterpriseServer, buildId, eventTypes) {
    return `${gradleEnterpriseServer}/build-export/v1/build/${buildId}/events?eventTypes=${eventTypes}`
}

function _initializeBuildSuccessChart(chart) {
    buildSuccessData.push({label: 'Success', data: 1});
    buildSuccessData.push({label: 'Failure', data: 0});

    buildSuccessPlot = $.plot(chart, buildSuccessData, {
        series: {
            pie: {
                show: true,
                radius: 800,
                label: {show: true, radius: 2 / 3, formatter: _labelFormatter, threshold: 0.18}
            }
        },
        legend: {show: false},
        colors: ['#00C489', '#FB2F08']
    });
}

function _initializeBuildDurationChart(chart) {
    buildDurationPlot = $.plot(chart, [], {
        series: {shadowSize: 0, points: {show: true, radius: 3, lineWidth: 0}, lines: {show: false}},
        axisLabels: {show: true},
        yaxis: {min: 0, axisLabel: 'Build duration (seconds)', axisLabelPadding: 25},
        xaxis: {mode: 'time', timezone: 'browser', timeformat: '%H:%M'}
    });
}

function _updateBuildSuccessChart(success) {
    if (!buildSuccessDataReceived) {
        buildSuccessDataReceived = true;
        buildSuccessData[0].data = 0;
        buildSuccessData[1].data = 0;
    }

    const i = (success ? 0 : 1);

    buildSuccessData[i].data = buildSuccessData[i].data + 1;
}

function _updateBuildDurationChart(startTime, endTime, success) {
    const dataPoint = [endTime, (endTime - startTime)/1000];

    if (success) {
        buildDurationDataSuccess.push(dataPoint);
    } else {
        buildDurationDataFailure.push(dataPoint);
    }
}

function _labelFormatter(label, series) {
    return `<div style="font-size:8pt; text-align:center; padding:2px; color:white;">${label}<br/>${Math.round(series.percent)}%</div>`;
}

function _getStartTime() {
    return $.now() - (1000 * 60 * 30);
}

function _getEndTime() {
    return $.now() + (1000 * 30);
}

function _update() {
    buildDurationPlot.setData([
        {data: buildDurationDataSuccess, points: {fillColor: '#00C489'}},
        {data: buildDurationDataFailure, points: {fillColor: '#FB2F08'}}
    ]);
    buildDurationPlot.getOptions().xaxes[0].min = _getStartTime();
    buildDurationPlot.getOptions().xaxes[0].max = _getEndTime();
    buildDurationPlot.setupGrid();
    buildDurationPlot.draw();

    buildSuccessPlot.setData(buildSuccessData);
    buildSuccessPlot.draw();

    setTimeout(_update, 1000);
}
