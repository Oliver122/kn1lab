import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * Die Klasse Sender liest einen String von der Konsole und zerlegt ihn in einzelne Worte.
 * Jedes Wort wird in ein einzelnes Packet verpackt und an das Medium verschickt.
 */
public class Sender {

    private static final String TARGET_HOST = "localhost";
    private static final int MEDIUM_PORT = 9997;
    private static final int SENDER_PORT = 9998; 
    private static final int TIMEOUT_MS = 1000;

    public static void main(String[] args) {
        Sender sender = new Sender();
        try {
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welche Nachricht möchten Sie senden?");
        
        String input = scanner.nextLine() + " EOT";
        String[] message = input.split(" ");

        InetAddress targetAddress = InetAddress.getByName(TARGET_HOST);

        DatagramSocket socket = new DatagramSocket(SENDER_PORT);
        
        socket.setSoTimeout(TIMEOUT_MS);

        int seqNumber = 0;

        for (String word : message) {
            byte[] payloadBytes = word.getBytes();
        
            Packet packet = new Packet(
                seqNumber,
                0,
                false,
                payloadBytes
            );

            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(bStream);
            oStream.writeObject(packet);
            oStream.flush();
            byte[] sendData = bStream.toByteArray();

            DatagramPacket udpSendPacket = new DatagramPacket(sendData, sendData.length, targetAddress, MEDIUM_PORT);
            boolean ackReceived = false;
            
            while (!ackReceived) {
                try {
                    socket.send(udpSendPacket);
                    
                    System.out.println("Gesendet [Seq: " + seqNumber + "]: " + new String(packet.getPayload()));

                    byte[] receiveBuffer = new byte[2048];
                    DatagramPacket udpReceivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                    socket.receive(udpReceivePacket);
                
                    ByteArrayInputStream bIn = new ByteArrayInputStream(
                        udpReceivePacket.getData(), 
                        0, 
                        udpReceivePacket.getLength()
                    );
                    ObjectInputStream oIn = new ObjectInputStream(bIn);
                    Packet ackPacket = (Packet) oIn.readObject();

                    if (ackPacket.isAckFlag() && ackPacket.getAckNum() == seqNumber + payloadBytes.length) {
                        System.out.println("--> ACK erhalten für Seq: " + ackPacket.getAckNum());
                        ackReceived = true;
                    } else {
                        System.out.println("--> Falsches ACK erhalten (erwartet: " + seqNumber + ", erhalten: " + ackPacket.getAckNum() + "). Wiederhole...");
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
                    System.out.println("Receive timed out, retrying...");
                }
            }
            
            seqNumber += payloadBytes.length;
        }
        
        socket.close();
        scanner.close();
        System.out.println("Übertragung beendet.");
    }
}