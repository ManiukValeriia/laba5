import java.io.*;
import java.net.*;

class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private String task;
    private int[] data;

    public Request(String task, int[] data) {
        this.task = task;
        this.data = data;
    }

    public String getTask() {
        return task;
    }

    public int[] getData() {
        return data;
    }
}

class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private String message;
    private int result;
    private String time;

    public Response(String status, String message, int result, String time) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getResult() {
        return result;
    }

    public String getTime() {
        return time;
    }
}

public class Server extends Thread {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        start();
    }

    @Override
    public void run() {
        System.out.println("Server started successfully on port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                Request request = (Request) in.readObject();
                Response response = handleRequest(request);

                out.writeObject(response);

                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Response handleRequest(Request request) {
        switch (request.getTask()) {
            case "factorial":
                return calculateFactorial(request.getData()[0]);
            case "sum":
                return calculateSum(request.getData()[0], request.getData()[1]);
            case "difference":
                return calculateDifference(request.getData()[0], request.getData()[1]);
            default:
                return new Response("error", "Unknown task", 0, "0ns");
        }
    }

    private Response calculateFactorial(int n) {
        long startTime = System.nanoTime();
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        long elapsedTime = System.nanoTime() - startTime;
        return new Response("ok", "", (int) result, elapsedTime + "ns");
    }

    private Response calculateSum(int a, int b) {
        long startTime = System.nanoTime();
        int result = a + b;
        long elapsedTime = System.nanoTime() - startTime;
        return new Response("ok", "", result, elapsedTime + "ns");
    }

    private Response calculateDifference(int a, int b) {
        long startTime = System.nanoTime();
        int result = a - b;
        long elapsedTime = System.nanoTime() - startTime;
        return new Response("ok", "", result, elapsedTime + "ns");
    }

    public static void main(String[] args) {
        Server server = new Server(4040);
        server.startServer();
        

        Response responseSum = server.handleRequest(new Request("sum", new int[]{5, 5}));
        Response responseDiff = server.handleRequest(new Request("difference", new int[]{20, 5}));
        Response responseFac = server.handleRequest(new Request("factorial", new int[]{5}));

        System.out.println("[Server] Sum result: " + responseSum.getResult() + "| time: " + responseSum.getTime());
        System.out.println("[Server] Difference result: " + responseDiff.getResult() + "| time: " + responseDiff.getTime());
        System.out.println("[Server] Factorial result: " + responseFac.getResult() + "| time: " + responseFac.getTime());
    }
}
