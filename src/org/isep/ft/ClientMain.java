package org.isep.ft;

import java.rmi.NotBoundException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ClientMain {

//java -jar FTLeader.jar localhost:1099
//java -jar FTLeader.jar localhost:1250
//java -cp FTLeader.jar org.isep.ft.ClientMain localhost 1099
	
	public static void main(String[] args) throws RemoteException {
        if (args.length != 1) {
            System.out.println("USAGE: FaultTolerantClient serverAddress:port");
            System.exit(0);
        }

        String serverAddress = args[0] ;
        FTBillboard billboardService = connectToServer(serverAddress);

        if (billboardService == null) {
            System.out.println("Failed to connect to the server.");
            System.exit(1);
        }

        try {
            while (true) { 
                List<String> neighbors = billboardService.getNeighbors();
                String leaderAddress = billboardService.getLeader();
 
                String message = "Hello from client";
                System.out.println("Sending a message to the leader: " + message);
                billboardService.setMessage(message);
 
                int retries = 2;   
                boolean success = false;

                while (retries > 0) {
                    try { 
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
                    if (neighbors.size() > 0) {
                        String newLeaderAddress = neighbors.get(0);
 
                        neighbors.remove(leaderAddress);
 
                        FTBillboard newLeaderService = connectToServer(newLeaderAddress);

                        if (newLeaderService != null) {
                            System.out.println("Trying the new leader: " + newLeaderAddress);
                            retries = 2;

                            while (retries > 0) {
                                try { 
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

                Thread.sleep(2000);  
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	
	
	
	
	/*
    static private Registry reg = null;
    static private FTBillboard currentServer = null;
    static private String serverID = null;

    static public void main(String [] args) throws NotBoundException {



        if(args.length !=2) {
            System.out.println("USAGE: ServerMain master port");
            System.exit(0);
        }

        String address = args[0];
        int port = Integer.parseInt(args[1]);
        serverID = address+":"+port;

        try {
            reg = LocateRegistry.getRegistry(address,port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            currentServer = (FTBillboard) reg.lookup(FTBillboard.LOOKUP_NAME);
        } catch (RemoteException e) {
            e.printStackTrace();
        }



        System.out.println("Starting Stupid client");

        for(int i = 0; i<1000 ; i++) {
            String message = "Hello guys " + i, received="";

            System.out.println("Test with message " + message);


            try {

                currentServer.setMessage(message);
                Thread.sleep(2500);
                received = currentServer.getMessage();
            } catch (RemoteException e) {


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(received.equals(message)) {
                System.out.println("no problem");
            }
            else {
                System.out.println("Problem: " + received + " instead of " + message );
            }
        }
    }
    
    */


}
