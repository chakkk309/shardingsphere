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

package org.apache.shardingsphere.test.e2e.agent.jaeger.asserts.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.infra.util.json.JsonConfiguration;

import java.util.Collection;

/**
 * Jaeger span response.
 */
@Getter
@Setter
public final class JaegerSpanResponse implements JsonConfiguration {
    
    @JsonProperty("traceID")
    private String traceId;
    
    @JsonProperty("spanID")
    private String spanId;
    
    private String operationName;
    
    private Collection<Tag> tags;
    
    /**
     * Jaeger tag response.
     */
    @Getter
    @Setter
    public static final class Tag implements JsonConfiguration {
        
        private String key;
        
        private String type;
        
        private String value;
    }
}
