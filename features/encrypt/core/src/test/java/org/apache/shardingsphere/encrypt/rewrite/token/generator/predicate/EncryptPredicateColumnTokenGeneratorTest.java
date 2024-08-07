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

package org.apache.shardingsphere.encrypt.rewrite.token.generator.predicate;

import org.apache.shardingsphere.encrypt.rewrite.token.generator.fixture.EncryptGeneratorFixtureBuilder;
import org.apache.shardingsphere.infra.exception.generic.UnsupportedSQLOperationException;
import org.apache.shardingsphere.infra.rewrite.sql.token.common.pojo.SQLToken;
import org.apache.shardingsphere.infra.rewrite.sql.token.common.pojo.generic.SubstitutableColumnNameToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncryptPredicateColumnTokenGeneratorTest {
    
    private final EncryptPredicateColumnTokenGenerator generator = new EncryptPredicateColumnTokenGenerator();
    
    @BeforeEach
    void setup() {
        generator.setEncryptRule(EncryptGeneratorFixtureBuilder.createEncryptRule());
    }
    
    @Test
    void assertIsGenerateSQLToken() {
        generator.setSchemas(Collections.emptyMap());
        assertTrue(generator.isGenerateSQLToken(EncryptGeneratorFixtureBuilder.createUpdateStatementContext()));
    }
    
    @Test
    void assertGenerateSQLTokenFromGenerateNewSQLToken() {
        generator.setSchemas(Collections.emptyMap());
        Collection<SQLToken> substitutableColumnNameTokens = generator.generateSQLTokens(EncryptGeneratorFixtureBuilder.createUpdateStatementContext());
        assertThat(substitutableColumnNameTokens.size(), is(1));
        assertThat(((SubstitutableColumnNameToken) substitutableColumnNameTokens.iterator().next()).toString(null), is("pwd_assist"));
    }
    
    @Test
    void assertGenerateSQLTokensWhenJoinConditionUseDifferentEncryptor() {
        generator.setSchemas(Collections.emptyMap());
        assertThrows(UnsupportedSQLOperationException.class, () -> generator.generateSQLTokens(EncryptGeneratorFixtureBuilder.createSelectStatementContext()));
    }
}
