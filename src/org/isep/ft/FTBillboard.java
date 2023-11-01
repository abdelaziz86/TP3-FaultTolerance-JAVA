package org.isep.ft;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FTBillboard extends Remote,Billboard {

    String LOOKUP_NAME = "FTBillboard";

    /**
     *
     * Returns the name of the current leader for this instance
     * @return String containing address:port of the leader's registry
     * @throws RemoteException
     */
    String getLeader() throws RemoteException;

    /**
     * List of address:port of neighbors of this node
     * @return
     * @throws RemoteException
     */
    List<String> getNeighbors() throws RemoteException;

    /**
     * Registers a replica to a leader.
     * @param server address:port of the caller
     * @param replica callback
     * @throws RemoteException
     */
    void registerReplica(String server, FTBillboard replica) throws RemoteException;
}
