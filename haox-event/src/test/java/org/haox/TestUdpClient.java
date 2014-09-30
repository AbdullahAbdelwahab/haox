package org.haox;

import junit.framework.Assert;
import org.haox.event.Event;
import org.haox.event.EventHub;
import org.haox.event.EventWaiter;
import org.haox.event.InternalEventHandler;
import org.haox.transport.*;
import org.haox.transport.event.MessageEvent;
import org.haox.transport.event.TransportEventType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class TestUdpClient extends TestUdpBase {

    private EventHub eventHub;
    private EventWaiter eventWaiter;

    @Before
    public void setUp() throws IOException {
        setUpServer();
        setUpClient();
    }

    private void setUpServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doRunServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doRunServer() throws IOException {
        DatagramChannel serverSocketChannel;
        Selector selector = Selector.open();
        serverSocketChannel = DatagramChannel.open();
        serverSocketChannel.configureBlocking(false);
        DatagramSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(serverPort));
        serverSocketChannel.register(selector, SelectionKey.OP_READ);

        while (true) {
            if (selector.selectNow() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        ByteBuffer recvBuffer = ByteBuffer.allocate(65536);
                        InetSocketAddress fromAddress = (InetSocketAddress) serverSocketChannel.receive(recvBuffer);
                        if (fromAddress != null) {
                            recvBuffer.flip();
                            serverSocketChannel.send(recvBuffer, fromAddress);
                        }
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpClient() throws IOException {
        eventHub = new EventHub();

        MessageHandler messageHandler = new MessageHandler(eventHub) {
            @Override
            protected void doHandle(Event event) throws Exception {
                MessageEvent msgEvent = (MessageEvent) event;
                if (msgEvent.getEventType() == TransportEventType.INBOUND_MESSAGE) {
                    ByteBuffer buffer = msgEvent.getMessage().getContent();
                    clientRecvedMessage = recvBuffer2String(buffer);
                    System.out.println("Recved clientRecvedMessage: " + clientRecvedMessage);
                    Boolean result = TEST_MESSAGE.equals(clientRecvedMessage);
                    dispatch(new Event(TestEventType.FINISHED, result));
                }
            }
        };
        eventHub.register(messageHandler);

        Connector connector = new UdpConnector(eventHub);
        eventHub.register((InternalEventHandler) connector);
        TransportHandler transportHandler = new TransportHandler(eventHub) {
            @Override
            protected void onNewTransport(Transport transport) {
                transport.sendMessage(new Message(ByteBuffer.wrap(TEST_MESSAGE.getBytes())));
            }
        };
        eventHub.register(transportHandler);

        eventWaiter = eventHub.waitEvent(TestEventType.FINISHED);

        eventHub.start();
        connector.connect(serverHost, serverPort);
    }

    @Test
    public void testUdpTransport() {
        Event event = eventWaiter.waitEvent();
        if (event != null) {
            Assert.assertTrue((Boolean) event.getEventData());
        } else {
            Assert.fail();
        }
    }
}
