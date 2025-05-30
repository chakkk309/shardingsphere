/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.e2e.env.container.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.test.e2e.env.container.atomic.adapter.config.AdaptorContainerConfiguration;
import org.apache.shardingsphere.test.e2e.env.container.atomic.constants.ProxyContainerConstants;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Suite proxy cluster container configuration factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyClusterContainerConfigurationFactory {
    
    /**
     * Create instance of adaptor container configuration.
     *
     * @param scenario scenario
     * @param databaseType database type
     * @param adapterContainerImage adapter container image
     * @return created instance
     */
    public static AdaptorContainerConfiguration newInstance(final String scenario, final DatabaseType databaseType, final String adapterContainerImage) {
        return new AdaptorContainerConfiguration(scenario, new LinkedList<>(), getMountedResources(scenario, databaseType), adapterContainerImage, "");
    }
    
    private static Map<String, String> getMountedResources(final String scenario, final DatabaseType databaseType) {
        Map<String, String> result = new HashMap<>(3, 1F);
        result.put("/env/common/cluster/proxy/conf/logback.xml", ProxyContainerConstants.CONFIG_PATH_IN_CONTAINER + "logback.xml");
        result.put("/env/scenario/" + scenario + "/proxy/conf/" + databaseType.getType().toLowerCase(), ProxyContainerConstants.CONFIG_PATH_IN_CONTAINER);
        result.put(getGlobalYamlPath(scenario, databaseType), ProxyContainerConstants.CONFIG_PATH_IN_CONTAINER + "global.yaml");
        return result;
    }
    
    @NotNull
    private static String getGlobalYamlPath(final String scenario, final DatabaseType databaseType) {
        if (isDialectScenarioGlobalYamlExists(scenario, databaseType)) {
            return "/env/scenario/" + scenario + "/proxy/mode/cluster/" + databaseType.getType().toLowerCase() + "/global.yaml";
        }
        if (isScenarioGlobalYamlExists(scenario)) {
            return "/env/scenario/" + scenario + "/proxy/mode/cluster/global.yaml";
        }
        return "/env/common/cluster/proxy/conf/global.yaml";
    }
    
    private static boolean isDialectScenarioGlobalYamlExists(final String scenario, final DatabaseType databaseType) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("env/scenario/" + scenario + "/proxy/mode/cluster/" + databaseType.getType().toLowerCase() + "/global.yaml");
        return null != url;
    }
    
    private static boolean isScenarioGlobalYamlExists(final String scenario) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("env/scenario/" + scenario + "/proxy/mode/cluster/global.yaml");
        return null != url;
    }
}
