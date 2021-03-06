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
package org.neo4j.cypher.export;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.schema.IndexDefinition;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class CypherResultSubGraph implements SubGraph
{

    private static final long REF_NODE_ID = 0L;
    private final SortedMap<Long, Node> nodes = new TreeMap<Long, Node>();
    private final SortedMap<Long, Relationship> relationships = new TreeMap<Long, Relationship>();

    public void add( Node node )
    {
        final long id = node.getId();
        if ( !nodes.containsKey( id ) )
        {
            addNode( id, node );
        }
    }

    void addNode( long id, Node data )
    {
        nodes.put( id, data );
    }

    public void add( Relationship rel )
    {
        final long id = rel.getId();
        if ( !relationships.containsKey( id ) )
        {
            addRel( id, rel );
            add( rel.getStartNode() );
            add( rel.getEndNode() );
        }
    }

    public static SubGraph from( ExecutionResult result, boolean addBetween )
    {
        final CypherResultSubGraph graph = new CypherResultSubGraph();
        final List<String> columns = result.columns();
        for ( Map<String, Object> row : result )
        {
            for ( String column : columns )
            {
                final Object value = row.get( column );
                graph.addToGraph( value );
            }
        }
        if ( addBetween )
        {
            graph.addRelationshipsBetweenNodes();
        }
        return graph;
    }

    private void addRelationshipsBetweenNodes()
    {
        Set<Node> newNodes = new HashSet<Node>();
        for ( Node node : nodes.values() )
        {
            for ( Relationship relationship : node.getRelationships() )
            {
                if ( !relationships.containsKey( relationship.getId() ) )
                {
                    continue;
                }

                final Node other = relationship.getOtherNode( node );
                if ( nodes.containsKey( other.getId() ) || newNodes.contains( other ) )
                {
                    continue;
                }
                newNodes.add( other );
            }
        }
        for ( Node node : newNodes )
        {
            add( node );
        }
    }

    private void addToGraph( Object value )
    {
        if ( value instanceof Node )
        {
            add( (Node) value );
        }
        if ( value instanceof Relationship )
        {
            add( (Relationship) value );
        }
        if ( value instanceof Iterable )
        {
            for ( Object inner : (Iterable) value )
            {
                addToGraph( inner );
            }
        }
    }

    @Override
    public Iterable<Node> getNodes()
    {
        return nodes.values();
    }

    @Override
    public Iterable<Relationship> getRelationships()
    {
        return relationships.values();
    }

    void addRel( Long id, Relationship rel )
    {
        relationships.put( id, rel );
    }

    @Override
    public boolean contains( Relationship relationship )
    {
        return relationships.containsKey( relationship.getId() );
    }

    @Override
    public Iterable<IndexDefinition> indexes() {
        return Collections.emptyList();
    }
}
