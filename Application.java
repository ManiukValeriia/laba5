import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

class CONFIG {
    public static String HOST = "localhost";
    public static int PORT = 6123; 
    public static int BUFFER_SIZE = 1010;
}

public class Application {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        try { Thread.sleep(500); } 
        catch (InterruptedException e) { e.printStackTrace(); }

        Client client_1 = new Client();
        client_1.connect_to_server();

        Client client_2 = new Client();
        client_2.connect_to_server();
        
        server.terminateServer();
    }
}

class Server extends Thread {
    private boolean threadAlive;
    private DatagramSocket serverSocket;
    private ArrayList<String> clientAddresses;

    public Server() {
        this.threadAlive = true;
        this.clientAddresses = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(CONFIG.PORT);
            System.out.println("Server started successfully on port: " + CONFIG.PORT);
            System.out.println("");
            int clientCounter = 0;
            while (threadAlive) {
                byte[] receiveData = new byte[CONFIG.BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String clientInfo = clientAddress.getHostAddress() + ":" + clientPort;
         
                if (!clientAddresses.contains(clientInfo)) {
                    clientAddresses.add(clientInfo);
                    clientCounter++;

                    System.out.println("client " + clientCounter + " got response:");
                }

                sendResponse(clientAddress, clientPort);
                try { Thread.sleep(100); } 
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private void sendResponse(InetAddress clientAddress, int clientPort) {
        try {
            StringBuilder sb = new StringBuilder();
            for (String address : clientAddresses) {
                sb.append(address);
                sb.append(" | ");
            }
            sb.delete(sb.length() - 3, sb.length());

            byte[] responseData = sb.toString().getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
            serverSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminateServer() {
        threadAlive = false;
    }
}

class Client {
    public void connect_to_server() {
        try { 
            DatagramSocket socket = new DatagramSocket();
            InetAddress server_addr = InetAddress.getByName(CONFIG.HOST);
            
            byte[] buffer = "nothing".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server_addr, CONFIG.PORT);
            socket.send(packet); 

            buffer = new byte[CONFIG.BUFFER_SIZE];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String received_message = new String(packet.getData(), 0, packet.getLength());
            System.out.println(received_message);
            System.out.println("");

            socket.close();
        } catch (IOException e) { 
            System.out.println("[error while connecting to the server]"); 
            e.printStackTrace();
        }
    }
}
