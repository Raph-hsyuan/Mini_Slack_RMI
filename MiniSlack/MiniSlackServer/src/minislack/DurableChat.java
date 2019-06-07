package minislack; /**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Topic;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class DurableChat implements
        javax.jms.MessageListener      // to handle message subscriptions
{
    private static final long MESSAGE_LIFESPAN = 1800000; //30 minutes

    private javax.jms.Connection connection = null;
    private javax.jms.Session pubSession = null;
    private javax.jms.Session subSession = null;
    public String username = null;
    private Hashtable<String, javax.jms.MessageProducer> publishers = new Hashtable<>();
    ClientMessageInterface messageInterface = null;
    private Hashtable<String, Topic> topics = new Hashtable<>();
    private Hashtable<String, javax.jms.MessageConsumer> subscribers = new Hashtable<>();


    public DurableChat(String broker, String username, String password) {
        startDurableChat(broker,username,password);
    }


    public void startDurableChat(String broker, String username, String password){
        this.username = username;
        //Create a connection:
        try {
            javax.jms.ConnectionFactory factory;
            factory = new ActiveMQConnectionFactory(username, password, broker);
            connection = factory.createConnection(username, password);

            //Durable Subscriptions are indexed by username, clientID and subscription name
            //It is a good practice to set the clientID:
            connection.setClientID(username);
            pubSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            subSession = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        } catch (javax.jms.JMSException jmse) {
            System.err.println("Error: Cannot connect to Broker - " + broker);
            jmse.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Message Handler
     **/
    public void onMessage(javax.jms.Message aMessage) {
        try {
            // Cast the message as a text message.
            javax.jms.TextMessage textMessage = (javax.jms.TextMessage) aMessage;

            // This handler reads a single String from the
            // message and prints it to the standard output.
            try {
                if(messageInterface!=null){
                    messageInterface.displayMsg(textMessage.getText());
                }else {
                    while(messageInterface==null)
                        System.out.print(".");
                    messageInterface.displayMsg(textMessage.getText());
                }
            } catch (javax.jms.JMSException jmse) {
                jmse.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException rte) {
        }
    }


    /**
     * Cleanup resources cleanly and exit.
     */
    public void exit() {
        try {
            connection.close();
        } catch (javax.jms.JMSException jmse) {
            jmse.printStackTrace();
        }

        return;
    }
    public void start(){
        try {
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public boolean send(String s, String topic) {
        if (!topics.keySet().contains(topic)) {
            return false;
        }
        if (s.length() > 0) {
            try {
                javax.jms.TextMessage msg = pubSession.createTextMessage();
                msg.setText(this.username + " from " + topic + " : " + s);
                publishers.get(topic).send(
                        topics.get(topic),
                        msg,                               //message
                        javax.jms.DeliveryMode.PERSISTENT, //publish persistently
                        javax.jms.Message.DEFAULT_PRIORITY,//priority
                        MESSAGE_LIFESPAN);                 //Time to Live
                return true;
            } catch (javax.jms.JMSException jmse) {
                System.err.println("Error publishing message:" + jmse.getMessage());
                return false;
            }
        }
        return false;
    }

    public void bindMessageClient(String username, int port) {
        try {
            Registry reg = LocateRegistry.getRegistry("192.168.56.1", port);
            messageInterface = (ClientMessageInterface) reg.lookup(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean leaveTopic(String roomname) {
        if (topics.keySet().contains(roomname)) {
            topics.remove(roomname);
            try {
                subscribers.get(roomname).close();
                publishers.get(roomname).close();
                subscribers.remove(roomname);
                publishers.remove(roomname);
                return true;
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean joinTopic(String roomname) {
        if (topics.keySet().contains(roomname)) return false;
        javax.jms.Topic topic = null;
        //Create Publisher and Durable Subscriber:
        try {
            topic = pubSession.createTopic(roomname);
            javax.jms.MessageConsumer subscriber = null;
            subscriber = subSession.createDurableSubscriber(topic, username + Math.random());
            subscriber.setMessageListener(this);
            subscribers.put(roomname, subscriber);
            publishers.put(roomname, pubSession.createProducer(topic));
            topics.put(roomname, topic);
            return true;
        } catch (javax.jms.JMSException jmse) {
            System.out.println("Error: connection not started.");
            jmse.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public List<String> getTopics() {
        List<String> list = new ArrayList<>();
        list.addAll(this.topics.keySet());
        return list;
    }

}
