/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.api.operations;

import java.io.Closeable;

import org.neo4j.kernel.api.exceptions.index.IndexNotFoundKernelException;
import org.neo4j.kernel.api.index.IndexReader;
import org.neo4j.kernel.api.scan.LabelScanReader;
import org.neo4j.kernel.impl.api.LockHolder;
import org.neo4j.kernel.impl.api.state.TxState;

/**
 * Contains all state necessary for satisfying operations performed on a statement.
 */
public interface StatementState extends TxState.Holder, Closeable
{
    LockHolder locks();

    IndexReader getIndexReader( long indexId ) throws IndexNotFoundKernelException;

    LabelScanReader getLabelScanReader();

    @Override
    void close();
}