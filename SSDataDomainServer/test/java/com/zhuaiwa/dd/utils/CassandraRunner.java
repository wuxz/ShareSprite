package com.zhuaiwa.dd.utils;


public class CassandraRunner {
    public static void main(String[] args) throws Exception {
        EmbeddedCassandraServer embedded = new EmbeddedCassandraServer();
        embedded.setup();
    }
}
