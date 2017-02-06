package com.cisco.oss.foundation.core.test;

import com.cisco.oss.foundation.message.RabbitMQAdmin;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Yair Ogen (yaogen) on 09/03/2016.
 */
public class AdminTest {

    @Ignore
    @Test
    public void testAdmin(){
        RabbitMQAdmin.INSTANCE.initAdmin();
        int testQueueLength = RabbitMQAdmin.INSTANCE.getQueueLength("Yair-test-queue");
        System.out.println("testQueueLength: " + testQueueLength);
        String overview = "";
        for (int i=0; i<10;i++) {
            overview = RabbitMQAdmin.INSTANCE.getOverview(true);
        }
        System.out.println("overview: " + overview);


    }
}
