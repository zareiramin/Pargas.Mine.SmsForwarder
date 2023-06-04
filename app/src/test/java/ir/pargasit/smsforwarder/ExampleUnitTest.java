package ir.pargasit.smsforwarder;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException {

        DatagramSocket s = new DatagramSocket();
        String str = "تست 123";
        byte[] message = str.getBytes();

        Integer port = 5000;
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        DatagramPacket p = new DatagramPacket(message, message.length, ip, port);
        s.send(p);
        assertEquals(4, 2 + 2);
    }
}