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

package org.apache.shardingsphere.broadcast.distsql.handler.update;

import lombok.Setter;
import org.apache.shardingsphere.broadcast.api.config.BroadcastRuleConfiguration;
import org.apache.shardingsphere.broadcast.distsql.statement.DropBroadcastTableRuleStatement;
import org.apache.shardingsphere.broadcast.rule.BroadcastRule;
import org.apache.shardingsphere.distsql.handler.engine.update.rdl.rule.spi.database.DatabaseRuleDropExecutor;
import org.apache.shardingsphere.distsql.handler.exception.rule.MissingRequiredRuleException;
import org.apache.shardingsphere.distsql.handler.required.DistSQLExecutorCurrentRuleRequired;
import org.apache.shardingsphere.distsql.handler.util.CollectionUtils;
import org.apache.shardingsphere.infra.exception.core.ShardingSpherePreconditions;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Drop broadcast table rule executor.
 */
@DistSQLExecutorCurrentRuleRequired(BroadcastRule.class)
@Setter
public final class DropBroadcastTableRuleExecutor implements DatabaseRuleDropExecutor<DropBroadcastTableRuleStatement, BroadcastRule, BroadcastRuleConfiguration> {
    
    private ShardingSphereDatabase database;
    
    private BroadcastRule rule;
    
    @Override
    public void checkBeforeUpdate(final DropBroadcastTableRuleStatement sqlStatement) {
        if (!sqlStatement.isIfExists()) {
            checkBroadcastTableRuleExist(sqlStatement);
        }
    }
    
    private void checkBroadcastTableRuleExist(final DropBroadcastTableRuleStatement sqlStatement) {
        Collection<String> currentRules = rule.getConfiguration().getTables();
        Collection<String> notExistRules = sqlStatement.getTables().stream().filter(each -> !CollectionUtils.containsIgnoreCase(currentRules, each)).collect(Collectors.toList());
        ShardingSpherePreconditions.checkState(notExistRules.isEmpty(), () -> new MissingRequiredRuleException("Broadcast", database.getName(), notExistRules));
    }
    
    @Override
    public boolean hasAnyOneToBeDropped(final DropBroadcastTableRuleStatement sqlStatement) {
        return !Collections.disjoint(rule.getConfiguration().getTables(), sqlStatement.getTables());
    }
    
    @Override
    public BroadcastRuleConfiguration buildToBeAlteredRuleConfiguration(final DropBroadcastTableRuleStatement sqlStatement) {
        BroadcastRuleConfiguration result = new BroadcastRuleConfiguration(new HashSet<>(rule.getConfiguration().getTables()));
        result.getTables().removeIf(each -> CollectionUtils.containsIgnoreCase(sqlStatement.getTables(), each));
        return result;
    }
    
    @Override
    public boolean updateCurrentRuleConfiguration(final DropBroadcastTableRuleStatement sqlStatement, final BroadcastRuleConfiguration currentRuleConfig) {
        currentRuleConfig.getTables().removeIf(each -> CollectionUtils.containsIgnoreCase(sqlStatement.getTables(), each));
        return currentRuleConfig.isEmpty();
    }
    
    @Override
    public Class<BroadcastRule> getRuleClass() {
        return BroadcastRule.class;
    }
    
    @Override
    public Class<DropBroadcastTableRuleStatement> getType() {
        return DropBroadcastTableRuleStatement.class;
    }
}
