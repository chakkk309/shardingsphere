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

package org.apache.shardingsphere.encrypt.rewrite.token.generator;

import lombok.Setter;
import org.apache.shardingsphere.encrypt.exception.syntax.UnsupportedEncryptSQLException;
import org.apache.shardingsphere.encrypt.rewrite.aware.EncryptRuleAware;
import org.apache.shardingsphere.encrypt.rule.EncryptRule;
import org.apache.shardingsphere.encrypt.rule.table.EncryptTable;
import org.apache.shardingsphere.infra.annotation.HighFrequencyInvocation;
import org.apache.shardingsphere.infra.binder.context.segment.select.orderby.OrderByItem;
import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.binder.context.statement.dml.SelectStatementContext;
import org.apache.shardingsphere.infra.exception.core.ShardingSpherePreconditions;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereSchema;
import org.apache.shardingsphere.infra.rewrite.sql.token.common.generator.CollectionSQLTokenGenerator;
import org.apache.shardingsphere.infra.rewrite.sql.token.common.generator.aware.SchemaMetaDataAware;
import org.apache.shardingsphere.infra.rewrite.sql.token.common.pojo.SQLToken;
import org.apache.shardingsphere.infra.rewrite.sql.token.common.pojo.generic.SubstitutableColumnNameToken;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.order.item.ColumnOrderByItemSegment;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

/**
 * Order by item token generator for encrypt.
 */
@HighFrequencyInvocation
@Setter
public final class EncryptOrderByItemTokenGenerator implements CollectionSQLTokenGenerator<SelectStatementContext>, EncryptRuleAware, SchemaMetaDataAware {
    
    private EncryptRule encryptRule;
    
    private Map<String, ShardingSphereSchema> schemas;
    
    private ShardingSphereSchema defaultSchema;
    
    @Override
    public boolean isGenerateSQLToken(final SQLStatementContext sqlStatementContext) {
        return sqlStatementContext instanceof SelectStatementContext && containsOrderByItem((SelectStatementContext) sqlStatementContext);
    }
    
    private boolean containsOrderByItem(final SelectStatementContext sqlStatementContext) {
        if (!sqlStatementContext.getOrderByContext().getItems().isEmpty() && !sqlStatementContext.getOrderByContext().isGenerated()) {
            return true;
        }
        for (SelectStatementContext each : sqlStatementContext.getSubqueryContexts().values()) {
            if (containsOrderByItem(each)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Collection<SQLToken> generateSQLTokens(final SelectStatementContext sqlStatementContext) {
        Collection<SQLToken> result = new LinkedHashSet<>();
        ShardingSphereSchema schema = sqlStatementContext.getTablesContext().getSchemaName().map(schemas::get).orElseGet(() -> defaultSchema);
        for (OrderByItem each : getOrderByItems(sqlStatementContext)) {
            if (each.getSegment() instanceof ColumnOrderByItemSegment) {
                ColumnSegment columnSegment = ((ColumnOrderByItemSegment) each.getSegment()).getColumn();
                Map<String, String> columnTableNames = sqlStatementContext.getTablesContext().findTableNames(Collections.singleton(columnSegment), schema);
                result.addAll(generateSQLTokens(Collections.singleton(columnSegment), columnTableNames));
            }
        }
        return result;
    }
    
    private Collection<SubstitutableColumnNameToken> generateSQLTokens(final Collection<ColumnSegment> columnSegments, final Map<String, String> columnTableNames) {
        Collection<SubstitutableColumnNameToken> result = new LinkedList<>();
        for (ColumnSegment each : columnSegments) {
            String tableName = columnTableNames.getOrDefault(each.getExpression(), "");
            Optional<EncryptTable> encryptTable = encryptRule.findEncryptTable(tableName);
            String columnName = each.getIdentifier().getValue();
            ShardingSpherePreconditions.checkState(!encryptTable.isPresent() || !encryptTable.get().isEncryptColumn(columnName), () -> new UnsupportedEncryptSQLException("ORDER BY"));
        }
        return result;
    }
    
    private Collection<OrderByItem> getOrderByItems(final SelectStatementContext sqlStatementContext) {
        Collection<OrderByItem> result = new LinkedList<>();
        if (!sqlStatementContext.getOrderByContext().isGenerated()) {
            result.addAll(sqlStatementContext.getOrderByContext().getItems());
        }
        for (SelectStatementContext each : sqlStatementContext.getSubqueryContexts().values()) {
            result.addAll(getOrderByItems(each));
        }
        return result;
    }
}
