/*
 * JBoss DNA (http://www.jboss.org/dna)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * JBoss DNA is free software. Unless otherwise indicated, all code in JBoss DNA
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * JBoss DNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.dna.graph.query.validate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import org.jboss.dna.graph.query.model.QueryCommand;
import org.jboss.dna.graph.query.model.SelectorName;
import org.jboss.dna.graph.query.model.Visitors;
import org.jboss.dna.graph.query.validate.Schemata.Column;
import org.jboss.dna.graph.query.validate.Schemata.Key;
import org.jboss.dna.graph.query.validate.Schemata.View;

/**
 * 
 */
@Immutable
class ImmutableView extends ImmutableTable implements View {

    private final QueryCommand definition;

    protected ImmutableView( SelectorName name,
                             Iterable<Column> columns,
                             QueryCommand definition ) {
        super(name, columns);
        this.definition = definition;
    }

    protected ImmutableView( SelectorName name,
                             Iterable<Column> columns,
                             QueryCommand definition,
                             Iterable<Column>... keyColumns ) {
        super(name, columns, keyColumns);
        this.definition = definition;
    }

    protected ImmutableView( SelectorName name,
                             Map<String, Column> columnsByName,
                             List<Column> columns,
                             QueryCommand definition,
                             Set<Key> keys ) {
        super(name, columnsByName, columns, keys);
        this.definition = definition;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.query.validate.Schemata.View#getDefinition()
     */
    public QueryCommand getDefinition() {
        return definition;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName().getName());
        sb.append('(');
        boolean first = true;
        for (Column column : getColumns()) {
            if (first) first = false;
            else sb.append(", ");
            sb.append(column);
        }
        sb.append(") AS '");
        sb.append(Visitors.readable(definition));
        sb.append('\'');
        if (!getKeys().isEmpty()) {
            sb.append(" with keys ");
            first = true;
            for (Key key : getKeys()) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(key);
            }
        }
        return sb.toString();
    }
}