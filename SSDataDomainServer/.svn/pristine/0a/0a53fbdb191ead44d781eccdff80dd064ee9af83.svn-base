package com.zhuaiwa.dd.cmd;

import java.util.HashSet;
import java.util.Set;

import me.prettyprint.hector.api.Keyspace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.domain.Favorite;
import com.zhuaiwa.dd.hector.HectorFactory;

public class IterateCommandTest {
    Keyspace cassandra;

    @Before
    public void setUp() throws Exception {
        DDProperties.setProperty("dd.cassandra.addresses", "10.130.29.240");
        DDProperties.setProperty("dd.cassandra.port", "9160");
        cassandra = HectorFactory.getKeyspace();
    }

    @After
    public void tearDown() throws Exception {
        HectorFactory.shutdown();
    }

    @Test
    public void test() {
        IterateCommand c = new IterateCommand(cassandra);
        c.Object(Favorite.class);
        c.Where("", 100);
        c.Select();
        
        Set<String> seenset = new HashSet<String>();
        
        long count = 0;
        for (Favorite pubbox : (Iterable<Favorite>)c) {

            if (seenset.contains(pubbox.getUserid())) {
                System.out.println("XXXXXXXXXXXXXXXXX");
                break;
            }
            if ((count % 10000) == 0) {
                seenset.add(pubbox.getUserid());
                System.out.println("count: " + count);
            }
            
            count++;
        }
        System.out.println("count: " + count);
    }

}
