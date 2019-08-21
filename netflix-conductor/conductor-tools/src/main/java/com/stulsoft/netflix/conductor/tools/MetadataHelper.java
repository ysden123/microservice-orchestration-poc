/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.TaskDef;

/**
 * @author Yuriy Stul
 */
public class MetadataHelper {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private MetadataHelper() {
    }

    public static TaskDef taskDefFromJsonString(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, TaskDef.class);
    }
}
