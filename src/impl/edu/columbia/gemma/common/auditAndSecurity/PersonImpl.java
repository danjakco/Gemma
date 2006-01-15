/*
 * The Gemma project.
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package edu.columbia.gemma.common.auditAndSecurity;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2006 University of British Columbia
 * 
 * @author pavlidis
 * @version $Id$
 */
public class PersonImpl extends edu.columbia.gemma.common.auditAndSecurity.Person implements java.io.Serializable {
    /** The serial version UID of this class. Needed for serialization. */
    private static final long serialVersionUID = -3335182453066930211L;

    /**
     * @see edu.columbia.gemma.common.auditAndSecurity.Person#getFullName()
     */
    public java.lang.String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }

}