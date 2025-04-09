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

package org.apache.shardingsphere.test.it.rewrite.engine.scenario;

import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereColumn;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereSchema;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereTable;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.infra.rule.attribute.datanode.MutableDataNodeRuleAttribute;
import org.apache.shardingsphere.single.rule.SingleRule;
import org.apache.shardingsphere.sql.parser.statement.core.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.ddl.CreateTableStatement;
import org.apache.shardingsphere.test.fixture.jdbc.MockedDataSource;
import org.apache.shardingsphere.test.it.rewrite.engine.SQLRewriterIT;
import org.apache.shardingsphere.test.it.rewrite.engine.SQLRewriterITSettings;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SQLRewriterITSettings("scenario/encrypt/case")
class EncryptSQLRewriterIT extends SQLRewriterIT {
    
    @Override
    protected Collection<ShardingSphereSchema> mockSchemas(final String schemaName) {
        Collection<ShardingSphereTable> tables = new LinkedList<>();
        tables.add(new ShardingSphereTable("t_account", Arrays.asList(
                new ShardingSphereColumn("account_id", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("certificate_number", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("password", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("amount", Types.DECIMAL, false, false, false, true, false, false),
                new ShardingSphereColumn("status", Types.TINYINT, false, false, false, false, false, false)), Collections.emptyList(), Collections.emptyList()));
        tables.add(new ShardingSphereTable("t_account_bak", Arrays.asList(
                new ShardingSphereColumn("account_id", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("certificate_number", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("password", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("amount", Types.DECIMAL, false, false, false, true, false, false),
                new ShardingSphereColumn("status", Types.TINYINT, false, false, false, false, false, false)), Collections.emptyList(), Collections.emptyList()));
        tables.add(new ShardingSphereTable("t_account_detail", Arrays.asList(
                new ShardingSphereColumn("account_id", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("certificate_number", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("password", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("amount", Types.DECIMAL, false, false, false, true, false, false),
                new ShardingSphereColumn("status", Types.TINYINT, false, false, false, false, false, false)), Collections.emptyList(), Collections.emptyList()));
        tables.add(new ShardingSphereTable("t_order", Arrays.asList(
                new ShardingSphereColumn("ORDER_ID", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("USER_ID", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("CONTENT", Types.VARCHAR, false, false, false, true, false, false)), Collections.emptyList(), Collections.emptyList()));
        tables.add(new ShardingSphereTable("t_user", Arrays.asList(
                new ShardingSphereColumn("user_id", Types.INTEGER, false, false, false, true, false, false),
                new ShardingSphereColumn("user_name", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("password", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("email", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("telephone", Types.VARCHAR, false, false, false, true, false, false),
                new ShardingSphereColumn("creation_date", Types.DATE, false, false, false, true, false, false)), Collections.emptyList(), Collections.emptyList()));
        return Collections.singleton(new ShardingSphereSchema(schemaName, tables, Collections.emptyList()));
    }
    
    @Override
    protected void mockRules(final Collection<ShardingSphereRule> rules, final String schemaName, final SQLStatement sqlStatement) {
        Optional<SingleRule> singleRule = rules.stream().filter(SingleRule.class::isInstance).map(SingleRule.class::cast).findFirst();
        if (singleRule.isPresent() && !(sqlStatement instanceof CreateTableStatement)) {
            singleRule.get().getAttributes().getAttribute(MutableDataNodeRuleAttribute.class).put("encrypt_ds", schemaName, "t_account");
            singleRule.get().getAttributes().getAttribute(MutableDataNodeRuleAttribute.class).put("encrypt_ds", schemaName, "t_account_bak");
            singleRule.get().getAttributes().getAttribute(MutableDataNodeRuleAttribute.class).put("encrypt_ds", schemaName, "t_account_detail");
            singleRule.get().getAttributes().getAttribute(MutableDataNodeRuleAttribute.class).put("encrypt_ds", schemaName, "t_order");
            singleRule.get().getAttributes().getAttribute(MutableDataNodeRuleAttribute.class).put("encrypt_ds", schemaName, "t_user");
        }
    }
    
    @SuppressWarnings("MagicConstant")
    @Override
    protected void mockDataSource(final Map<String, DataSource> dataSources) throws SQLException {
        for (Entry<String, DataSource> entry : dataSources.entrySet()) {
            Connection connection = mock(Connection.class, RETURNS_DEEP_STUBS);
            when(connection.getMetaData().getURL()).thenReturn("jdbc:mock://127.0.0.1/foo_ds");
            when(connection.getMetaData().getUserName()).thenReturn("root");
            when(connection.createStatement(anyInt(), anyInt(), anyInt()).getConnection()).thenReturn(connection);
            ResultSet typeInfo = mockTypeInfo();
            when(connection.getMetaData().getTypeInfo()).thenReturn(typeInfo);
            entry.setValue(new MockedDataSource(connection));
        }
    }
    
    private ResultSet mockTypeInfo() throws SQLException {
        ResultSet result = mock(ResultSet.class);
        when(result.next()).thenReturn(true, true, false);
        when(result.getString("TYPE_NAME")).thenReturn("INTEGER", "VARCHAR");
        when(result.getInt("DATA_TYPE")).thenReturn(Types.INTEGER, Types.VARCHAR);
        return result;
    }
}
