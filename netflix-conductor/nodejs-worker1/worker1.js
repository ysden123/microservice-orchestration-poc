/*
 * Copyright (c) 2019. Yuriy Stul
 */
'use strict';

const ConductorClient = require('conductor-client').default;

const conductorClient = new ConductorClient({
    baseURL: 'http://localhost:8080/api'
});

const watcher = conductorClient.registerWatcher("task_1", (eventData, updater) => {
        console.log("Received request");
        if (eventData != null) {
            console.log("");
            console.log("eventData:");
            console.log(eventData);
            console.log("inputData:");
            console.log(eventData.inputData);
            console.log("inputData.eventData:");
            console.log(eventData.inputData.eventData);
            updater.complete({
                outputData: {
                    resultData: {
                        state: "the state value (NodeJS)",
                        skipped: "no",
                        result: "the result"
                    }
                }
            })
        } else {
            updater.fail({outputData: null, reasonForIncompletion: "No data specified"})
        }
    },
    {pollingIntervals: 1000, autoAck: true, maxRunner: 2},
    true);

console.log("task_1 was started:");
console.log(watcher);
