/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *  $Revision: 4 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.tap.upload;

import ca.nrc.cadc.util.Log4jInit;
import org.apache.log4j.Logger;
import ca.nrc.cadc.db.ConnectionConfig;
import ca.nrc.cadc.db.DBConfig;
import ca.nrc.cadc.db.DBUtil;

import javax.sql.DataSource;

import ca.nrc.cadc.tap.upload.datatype.DatabaseDataType;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


/**
 * @author jburke
 */
public class DatabaseDataTypeFactoryTest {
    private static final Logger log = Logger.getLogger(DatabaseDataTypeFactoryTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc.tap", org.apache.log4j.Level.INFO);
    }

    public DatabaseDataTypeFactoryTest() {
    }

    /**
     * TODO - We expect Open Source users to have a .dbrc file in their home directory?  This really isn't a unit
     * TODO - test.  The logic has been captured below, anyway.
     *
     * @throws Exception If anything goes wrong, report it.
     */
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        try {
//            DBConfig dbrc = new DBConfig();
//            ConnectionConfig conf = dbrc.getConnectionConfig("TAP_UPLOAD_TEST", "cadctest");
//            ds = DBUtil.getDataSource(conf, true, true);
//        } catch (FileNotFoundException e) {
//            // .dbrc file not found. Skip the tests
//            log.warn("No .dbrc file found. Skipping all the DatabaseDataTypeFactoryTest tests", e);
//            assumeTrue(false);
//        }
//    }
    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    DataSource loadDataSource() {
        try {
            final DBConfig dbrc = new DBConfig();
            final ConnectionConfig conf = dbrc.getConnectionConfig("TAP_UPLOAD_TEST", "cadctest");
            return DBUtil.getDataSource(conf, true, true);
        } catch (Exception e) {
            log.warn(String.format("No .dbrc file found, or unable to create connection: '%s'.", e.getMessage()));
            return null;
        }
    }

    /**
     * Test of getDatabaseDataType method, of class DatabaseDataTypeFactory.
     */
    @Test
    public void testGetDatabaseDataType() throws Exception {
        log.debug("testGetDatabaseDataType");
        final DataSource ds = loadDataSource();
        if (ds == null) {
            testGetDatabaseDataTypeMock();
        } else {
            testGetDatabaseDataTypeDBRC(ds);
        }
    }

    void testGetDatabaseDataTypeMock() throws Exception {
        log.info("Running with Mock (testGetDatabaseDataTypeMock).");
        final Connection mockConnection = createMock(Connection.class);
        final DatabaseMetaData mockDatabaseMetaData = createMock(DatabaseMetaData.class);

        expect(mockConnection.getMetaData()).andReturn(mockDatabaseMetaData).once();
        expect(mockDatabaseMetaData.getDatabaseProductName()).andReturn("postgres").once();

        replay(mockConnection, mockDatabaseMetaData);
        try {
            final DatabaseDataType result = DatabaseDataTypeFactory.getDatabaseDataType(mockConnection);
            assertNotNull(result);
            log.info("testGetDatabaseDataTypeMock passed.");
        } catch (Exception unexpected) {
            log.error("unexpected exception", unexpected);
            throw unexpected;
        } finally {
            verify(mockConnection, mockDatabaseMetaData);
        }
    }

    void testGetDatabaseDataTypeDBRC(final DataSource ds) throws Exception {
        log.info("Running with DBRC (testGetDatabaseDataTypeDBRC).");
        try {
            DatabaseDataType result = DatabaseDataTypeFactory.getDatabaseDataType(ds.getConnection());
            assertNotNull(result);
            log.info("testGetDatabaseDataTypeDBRC passed.");
        } catch (Exception unexpected) {
            log.error("unexpected exception", unexpected);
            throw unexpected;
        }
    }
}
