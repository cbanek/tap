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

package ca.nrc.cadc.tap.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.MissingResourceException;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import ca.nrc.cadc.tap.TapProperties;
import ca.nrc.cadc.tap.UploadManager;
import ca.nrc.cadc.tap.parser.adql.impl.postgresql.sql.SqlPropertiesFactory;

public abstract class UploadTestCase extends TestCase {
    
    private static final Logger log = Logger.getLogger( UploadTestCase.class );

    protected static UploadManager manager;
    protected static Connection    conn;
    
    static {
        BasicConfigurator.configure();
    }

    public UploadTestCase() {

        super();

        if ( manager == null ) {
            
            DataSource ds = getDataSource();

            manager = new UploadManager(ds);
            
            try {
                conn = ds.getConnection();
            }
            catch (SQLException e) {
                assertTrue( e.getMessage(), false );
            }
        }
    }
    
    /* Read database credentials from properties file 
     * found in a directory on the classpath.
     */
    private DataSource getDataSource() {

        try {
            TapProperties properties = new TapProperties("postgresql_upload.properties");

            final String  JDBC_DRIVER    = properties.getProperty("JDBC_DRIVER");
            final String  JDBC_URL       = properties.getProperty("JDBC_URL");
            final String  USERNAME       = properties.getProperty("USERNAME");
            final String  PASSWORD       = properties.getProperty("PASSWORD");
            final boolean SUPPRESS_CLOSE = properties.getPropertyBooloean("SUPPRESS_CLOSE");

            try {
                Class.forName(JDBC_DRIVER);
            }
            catch (ClassNotFoundException e) {
                String msg = "JDBC Driver not found: "+JDBC_DRIVER;
                log.error(msg, e);
                throw new MissingResourceException(msg, JDBC_DRIVER, null);
            }
            
            DataSource ds = new SingleConnectionDataSource( JDBC_URL,
                                                            USERNAME,
                                                            PASSWORD,
                                                            SUPPRESS_CLOSE );
            return ds;
        }
        catch (Exception ex) {
            log.error(ex);
        }
        
        return null;
    }

}
