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

package org.apache.shardingsphere.sqlfederation.compiler.exception;

import org.apache.shardingsphere.infra.exception.core.external.sql.sqlstate.XOpenSQLState;
import org.apache.shardingsphere.sql.parser.statement.core.statement.SQLStatement;

/**
 * SQL federation SQL node convert exception.
 */
public final class SQLFederationSQLNodeConvertException extends SQLFederationSQLException {
    
    private static final long serialVersionUID = 7115939407266382363L;
    
    public SQLFederationSQLNodeConvertException(final SQLStatement sqlStatement) {
        super(XOpenSQLState.SYNTAX_ERROR, 0, "Unsupported SQL node conversion for SQL statement '%s'.", sqlStatement);
    }
}
