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

import org.jboss.dna.common.util.CheckArg;
import org.jboss.dna.graph.GraphI18n;
import org.jboss.dna.graph.Location;
import org.jboss.dna.graph.property.Path;
import org.jboss.dna.graph.property.Property;

/**
 * Instruction to set a particular property on the node at the specified location. This request <i>never</i> removes the node,
 * even if the property is empty.
 * 
 * @author Randall Hauch
 */
public class SetPropertyRequest extends Request implements ChangeRequest {

    private static final long serialVersionUID = 1L;

    private final Location on;
    private final String workspaceName;
    private final Property property;
    private Location actualLocation;

    /**
     * Create a request to set the property on the node at the supplied location.
     * 
     * @param on the location of the node to be read
     * @param workspaceName the name of the workspace containing the node
     * @param property the new property on the node
     * @throws IllegalArgumentException if the location, workspace name, or property is null
     */
    public SetPropertyRequest( Location on,
                               String workspaceName,
                               Property property ) {
        CheckArg.isNotNull(on, "on");
        CheckArg.isNotNull(property, "property");
        CheckArg.isNotNull(workspaceName, "workspaceName");
        this.workspaceName = workspaceName;
        this.on = on;
        this.property = property;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.request.Request#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Get the location defining the node that is to be updated.
     * 
     * @return the location of the node; never null
     */
    public Location on() {
        return on;
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
     * Get the property that is being set.
     * 
     * @return the new property; never null
     */
    public Property property() {
        return property;
    }

    /**
     * Sets the actual and complete location of the node being updated. This method must be called when processing the request,
     * and the actual location must have a {@link Location#getPath() path}.
     * 
     * @param actual the actual location of the node being updated, or null if the {@link #on() current location} should be used
     * @throws IllegalArgumentException if the actual location does represent the {@link Location#isSame(Location) same location}
     *         as the {@link #on() current location}, or if the actual location does not have a path.
     */
    public void setActualLocationOfNode( Location actual ) {
        if (!on.isSame(actual)) { // not same if actual is null
            throw new IllegalArgumentException(GraphI18n.actualLocationIsNotSameAsInputLocation.text(actual, on));
        }
        assert actual != null;
        if (!actual.hasPath()) {
            throw new IllegalArgumentException(GraphI18n.actualLocationMustHavePath.text(actual));
        }
        this.actualLocation = actual;
    }

    /**
     * Get the actual location of the node that was updated.
     * 
     * @return the actual location, or null if the actual location was not set
     */
    public Location getActualLocationOfNode() {
        return actualLocation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.request.ChangeRequest#changes(java.lang.String, org.jboss.dna.graph.property.Path)
     */
    public boolean changes( String workspace,
                            Path path ) {
        return this.workspaceName.equals(workspace) && on.hasPath() && on.getPath().isAtOrBelow(path);
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
            SetPropertyRequest that = (SetPropertyRequest)obj;
            if (!this.on().equals(that.on())) return false;
            if (!this.property().equals(that.property())) return false;
            if (!this.inWorkspace().equals(that.inWorkspace())) return false;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.dna.graph.request.ChangeRequest#changedLocation()
     */
    public Location changedLocation() {
        return on;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "set property " + property().getName() + " on " + on() + " in the \"" + workspaceName + "\" workspace to "
               + property().getValuesAsArray();
    }
}