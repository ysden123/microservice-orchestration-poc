'use strict';
/*
 * Copyright (c) 2019. Yuriy Stul
 */
const zb = require('zeebe-node');
const zbc = new zb.ZBClient('localhost:26500');

const zbWorker = zbc.createWorker('test-nodejs-worker', 'demo-service', handler);

function handler(job, complete) {
    console.log('Task variables', job.variables);
    const updatedVariables = Object.assign({}, job.variables, {
        updatedProperty: 'newValue',
    });

    // Task worker business logic goes here

    complete.success(updatedVariables)
}

