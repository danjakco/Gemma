package edu.columbia.gemma.externalDb;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import edu.columbia.gemma.BaseDAOTestCase;

/**
 * Tests connections to external databases.
 * <hr>
 * <p>
 * Copyright (c) 2004 - 2006 University of British Columbia
 * 
 * @author keshav
 * @version $Id$
 * @see ExternalDatabase
 */
public class ExternalDatabaseTest extends BaseDAOTestCase {
    Configuration conf;
    GoldenPathHumanDao db;

    /**
     * Get the bean name of the appropriate dao object. These dao objects will be replaced by the "table name + Dao" for
     * the database in question. For the moment, I have only set this up for the hg17 database as the other 3 will work
     * in the same manner.
     * 
     * @throws ConfigurationException
     */
    public void setUp() throws ConfigurationException {
        conf = new PropertiesConfiguration( "Gemma.properties" );
        db = ( GoldenPathHumanDaoHibernate ) ctx.getBean( conf.getString( "external.database.0" ) );
        // ctx.getBean( conf.getString( "external.database.1" ) );
        // ctx.getBean( conf.getString( "external.database.2" ) )
        // ctx.getBean( conf.getString( "external.database.3" ) )
    }

    public void tearDown() {
        db = null;
    }

    /**
     * Tests the database connection.
     * 
     * @throws HibernateException
     * @throws SQLException
     */
    public void testConnectToDatabase() throws Exception {
        boolean connectionIsClosed = db.connectToDatabase();
        assertEquals( connectionIsClosed, false );
    }

}