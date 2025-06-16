package leanersdts;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDiscovery.class);
    private static final int DISCOVERY_PORT = 3579; // Port for UDP broadcast
    private static final String DISCOVERY_MESSAGE = "ATTENDWISE_SERVER_DISCOVERY";
    private static final int SERVER_PORT = 3003; // Default server port

    private DatagramSocket socket;
    private boolean isRunning = false;
    private ExecutorService executorService;
    private ServerDiscoveryListener listener;

    public interface ServerDiscoveryListener {
        void onServerDiscovered(String ipAddress, int port);
        void onDiscoveryError(String message);
    }

    public ServerDiscovery(ServerDiscoveryListener listener) {
        this.listener = listener;
        executorService = Executors.newSingleThreadExecutor();
    }

    public void startDiscovery() {
        if (isRunning) {
            LOGGER.info("Discovery already running");
            return;
        }

        isRunning = true;
        executorService.submit(() -> {
            try {
                socket = new DatagramSocket(); // Bind to an ephemeral port
                socket.setBroadcast(true);
                socket.setSoTimeout(3000); // 3 seconds timeout for receive

                // Send broadcast message
                byte[] sendData = DISCOVERY_MESSAGE.getBytes();
                InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255"); // Global broadcast
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, DISCOVERY_PORT);
                socket.send(sendPacket);
                LOGGER.info("Sent discovery packet to {}:{}", broadcastAddress, DISCOVERY_PORT);

                // Listen for responses
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                while (isRunning) {
                    try {
                        socket.receive(receivePacket);
                        handleIncomingBroadcast(receivePacket);
                    } catch (SocketTimeoutException e) {
                        // Timeout occurred, continue listening
                        LOGGER.info("Receive timeout, sending another broadcast");
                        socket.send(sendPacket); // Resend broadcast on timeout
                    } catch (IOException e) {
                        if (isRunning) { // Only log if not intentionally stopped
                            LOGGER.error("Error during receive", e);
                            if (listener != null) {
                                listener.onDiscoveryError("Error receiving server response: " + e.getMessage());
                            }
                        }
                        break; // Exit loop on serious IO error
                    }
                }
            } catch (SocketException e) {
                LOGGER.error("Socket error", e);
                if (listener != null) {
                    listener.onDiscoveryError("Socket error during server discovery: " + e.getMessage());
                }
            } catch (UnknownHostException e) {
                LOGGER.error("Unknown host error", e);
                if (listener != null) {
                    listener.onDiscoveryError("Unknown host during server discovery: " + e.getMessage());
                }
            } catch (IOException e) {
                LOGGER.error("IO error", e);
                if (listener != null) {
                    listener.onDiscoveryError("IO error during server discovery: " + e.getMessage());
                }
            }
        });
    }

    private void handleIncomingBroadcast(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength());
        LOGGER.info("Received broadcast: " + message + " from " + packet.getAddress().getHostAddress());
        try {
            // Assuming the server responds with its IP in a JSON format
            // For example: {"ip": "192.168.1.100", "port": 3003}
            JSONObject json = new JSONObject(message);
            String serverIp = json.getString("ip");
            int serverPort = json.optInt("port", SERVER_PORT); // Use default if port not specified

            if (listener != null) {
                listener.onServerDiscovered(serverIp, serverPort);
                stopDiscovery(); // Stop discovery once server is found
            }
        } catch (JSONException e) {
            LOGGER.error("Failed to parse broadcast message", e);
            if (listener != null) {
                listener.onDiscoveryError("Failed to parse server response: " + e.getMessage());
            }
        }
    }

    public void stopDiscovery() {
        isRunning = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
            LOGGER.info("Discovery socket closed");
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            LOGGER.info("Discovery executor service shut down");
        }
    }
} 