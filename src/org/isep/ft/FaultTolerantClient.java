import org.isep.ft.FTBillboard;
import org.isep.ft.Billboard;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class FaultTolerantClient {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("USAGE: FaultTolerantClient serverAddress:port");
            System.exit(0);
        }

        String serverAddress = args[0];
        FTBillboard billboardService = connectToServer(serverAddress);

        if (billboardService == null) {
            System.out.println("Failed to connect to the server.");
            System.exit(1);
        }

        try {
            while (true) {
                // Get the list of available replicas (neighbors) from the leader
                List<String> neighbors = billboardService.getNeighbors();
                String leaderAddress = billboardService.getLeader();

                // Perform an operation on the leader
                String message = "Hello from client";
                System.out.println("Sending a message to the leader: " + message);
                billboardService.setMessage(message);

                // Try to connect to the leader and retry if it fails
                int retries = 2;  // Number of retries before trying a neighbor
                boolean success = false;

                while (retries > 0) {
                    try {
                        // Try to perform an operation on the leader
                        String response = billboardService.getMessage();
                        System.out.println("Received response from the leader: " + response);
                        success = true;
                        break;
                    } catch (RemoteException e) {
                        System.out.println("Leader communication failed: " + e.getMessage());
                        retries--;

                        if (retries > 0) {
                            System.out.println("Retrying the leader...");
                        } else {
                            System.out.println("Leader communication failed after retries.");
                            break;
                        }
                    }
                }

                if (!success) {
                    // The leader failed to respond; try connecting to a neighbor
                    if (neighbors.size() > 0) {
                        String newLeaderAddress = neighbors.get(0);

                        // Remove the leader from the neighbors list
                        neighbors.remove(leaderAddress);

                        // Connect to the new leader (the first neighbor) and retry the operation
                        FTBillboard newLeaderService = connectToServer(newLeaderAddress);

                        if (newLeaderService != null) {
                            System.out.println("Trying the new leader: " + newLeaderAddress);
                            retries = 2;

                            while (retries > 0) {
                                try {
                                    // Try to perform an operation on the new leader
                                    String response = newLeaderService.getMessage();
                                    System.out.println("Received response from the new leader: " + response);
                                    success = true;
                                    break;
                                } catch (RemoteException e) {
                                    System.out.println("New leader communication failed: " + e.getMessage());
                                    retries--;

                                    if (retries > 0) {
                                        System.out.println("Retrying the new leader...");
                                    } else {
                                        System.out.println("New leader communication failed after retries.");
                                        break;
                                    }
                                }
                            }
                        } else {
                            System.out.println("Failed to connect to the new leader.");
                        }
                    }
                }

                if (!success) {
                    System.out.println("All attempts to communicate with the leader and neighbors failed.");
                }

                Thread.sleep(2000);  // Sleep for 2 seconds before the next operation
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Helper method to connect to the RMI server
    private static FTBillboard connectToServer(String serverAddress) {
        try {
            String[] parsed = serverAddress.split(":");
            String host = parsed[0];
            int port = Integer.parseInt(parsed[1]);
            Registry registry = LocateRegistry.getRegistry(host, port);
            return (FTBillboard) registry.lookup(FTBillboard.LOOKUP_NAME);
        } catch (Exception e) {
            System.out.println("Failed to connect to the server at " + serverAddress + ": " + e.getMessage());
            return null;
        }
    }
}
