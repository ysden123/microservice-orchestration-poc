/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools;

import com.netflix.conductor.common.metadata.tasks.TaskDef;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yuriy Stul
 */
class MetadataHelperTest {

    @Test
    void taskDefFromJsonString() throws Exception {
        String text =readResource("taskDefinition.json");
        TaskDef taskDef = MetadataHelper.taskDefFromJsonString(text);
        assertNotNull(taskDef);
    }

    @Test
    void taskDefFromJsonString2() throws Exception {
        String text =readResource("taskDefinition2.json");
        TaskDef taskDef = MetadataHelper.taskDefFromJsonString(text);
        assertNotNull(taskDef);
    }

    private String readResource(String resourceName) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        if (is == null)
            throw new IOException("Resource " + resourceName + " was not found");
        return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
    }
}