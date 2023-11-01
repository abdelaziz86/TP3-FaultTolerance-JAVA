package org.isep.ft;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {


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


}
