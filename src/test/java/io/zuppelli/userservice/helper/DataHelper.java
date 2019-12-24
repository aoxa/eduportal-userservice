package io.zuppelli.userservice.helper;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.BeforeClass;

import java.io.IOException;

public class DataHelper {
    public static void initialize() throws InterruptedException, IOException, TTransportException {

        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = Cluster.builder()
                .addContactPoint("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();

        final CQLDataLoader cqlDataLoader = new CQLDataLoader(session);
        cqlDataLoader.load(new ClassPathCQLDataSet("db.cql", false, true, "userkeyspace"));

    }

    public static void tearDown() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }
}
