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
package org.jboss.dna.graph.request;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jboss.dna.common.util.CheckArg;
import org.jboss.dna.graph.GraphI18n;
import org.jboss.dna.graph.Location;
import org.jboss.dna.graph.connector.RepositoryConnection;
import org.jboss.dna.graph.property.Name;
import org.jboss.dna.graph.property.Path;
import org.jboss.dna.graph.property.Property;

/**
 * Instruction to read the properties and children of the node at the specifed location.
 * 
 * @author Randall Hauch
 */
public class ReadNodeRequest extends CacheableRequest implements Iterable<Location> {

    private static final long serialVersionUID = 1L;

    private final Location at;
    private final String workspaceName;
    private final Map<Name, Property> properties = new HashMap<Name, Property>();
    private final List<Location> children = new LinkedList<Location>();
    private Location actualLocation;

    /**
     * Create a request to read the properties and number of children of a node at the supplied location.
     * 
     * @param at the location of the node to be read
     * @param workspaceName the name of the workspace containing the node
     * @throws IllegalArgumentException if the location or workspace name is null
     */
    public ReadNodeRequest( Location at,
                            String workspaceName ) {
        CheckArg.isNotNull(at, "at");
        CheckArg.isNotNull(workspaceName, "workspaceName");
        this.workspaceName = workspaceName;
        this.at = at;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.request.Request#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Get the location defining the node that is to be read.
     * 
     * @return the location of the node; never null
     */
    public Location at() {
        return at;
    }

    /**
     * Get the name of the workspace in which the node exists.
     * 
     * @return the name of the workspace; never null
     */
    public String inWorkspace() {
        return workspaceName;
    }

    /**
     * Get the properties that were read from the {@link RepositoryConnection}.
     * 
     * @return the properties, as a map of property name to property; never null
     */
    public Map<Name, Property> getPropertiesByName() {
        return properties;
    }

    /**
     * Get the properties that were read from the {@link RepositoryConnection}.
     * 
     * @return the collection of properties; never null
     */
    public Collection<Property> getProperties() {
        return properties.values();
    }

    /**
     * Add a property that was read from the {@link RepositoryConnection}
     * 
     * @param property the property that was read
     * @return the previous property that had the same name, or null if there was no previously-recorded property with the same
     *         name
     * @throws IllegalArgumentException if the property is null
     */
    public Property addProperty( Property property ) {
        return this.properties.put(property.getName(), property);
    }

    /**
     * Add a property that was read from the {@link RepositoryConnection}
     * 
     * @param properties the properties that were read
     * @throws IllegalArgumentException if the property is null
     */
    public void addProperties( Property... properties ) {
        for (Property property : properties) {
            this.properties.put(property.getName(), property);
        }
    }

    /**
     * Get the children that were read from the {@link RepositoryConnection} after the request was processed. Each child is
     * represented by a location.
     * 
     * @return the children that were read; never null
     */
    public List<Location> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Location> iterator() {
        return children.iterator();
    }

    /**
     * Add to the list of children that has been read the child with the given path and identification properties. The children
     * should be added in order.
     * 
     * @param child the location of the child that was read
     * @throws IllegalArgumentException if the location is null
     * @see #addChild(Path, Property)
     * @see #addChild(Path, Property, Property...)
     */
    public void addChild( Location child ) {
        CheckArg.isNotNull(child, "child");
        this.children.add(child);
    }

    /**
     * Add to the list of children that has been read the child with the given path and identification properties. The children
     * should be added in order.
     * 
     * @param pathToChild the path of the child that was just read
     * @param firstIdProperty the first identification property of the child that was just read
     * @param remainingIdProperties the remaining identification properties of the child that was just read
     * @throws IllegalArgumentException if the path or identification properties are null
     * @see #addChild(Location)
     * @see #addChild(Path, Property)
     */
    public void addChild( Path pathToChild,
                          Property firstIdProperty,
                          Property... remainingIdProperties ) {
        Location child = new Location(pathToChild, firstIdProperty, remainingIdProperties);
        this.children.add(child);
    }

    /**
     * Add to the list of children that has been read the child with the given path and identification property. The children
     * should be added in order.
     * 
     * @param pathToChild the path of the child that was just read
     * @param idProperty the identification property of the child that was just read
     * @throws IllegalArgumentException if the path or identification properties are null
     * @see #addChild(Location)
     * @see #addChild(Path, Property, Property...)
     */
    public void addChild( Path pathToChild,
                          Property idProperty ) {
        Location child = new Location(pathToChild, idProperty);
        this.children.add(child);
    }

    /**
     * Sets the actual and complete location of the node whose children and properties have been read. This method must be called
     * when processing the request, and the actual location must have a {@link Location#getPath() path}.
     * 
     * @param actual the actual location of the node being read, or null if the {@link #at() current location} should be used
     * @throws IllegalArgumentException if the actual location does not represent the {@link Location#isSame(Location) same
     *         location} as the {@link #at() current location}, or if the actual location does not have a path.
     */
    public void setActualLocationOfNode( Location actual ) {
        if (!at.isSame(actual)) { // not same if actual is null
            throw new IllegalArgumentException(GraphI18n.actualLocationIsNotSameAsInputLocation.text(actual, at));
        }
        assert actual != null;
        if (!actual.hasPath()) {
            throw new IllegalArgumentException(GraphI18n.actualLocationMustHavePath.text(actual));
        }
        this.actualLocation = actual;
    }

    /**
     * Get the actual location of the node whose children and properties were read.
     * 
     * @return the actual location, or null if the actual location was not set
     */
    public Location getActualLocationOfNode() {
        return actualLocation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == this) return true;
        if (this.getClass().isInstance(obj)) {
            ReadNodeRequest that = (ReadNodeRequest)obj;
            if (!this.at().equals(that.at())) return false;
            if (!this.inWorkspace().equals(that.inWorkspace())) return false;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "read node at " + at() + " in the \"" + workspaceName + "\" workspace";
    }

}