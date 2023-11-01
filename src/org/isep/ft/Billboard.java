package org.isep.ft;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Billboard extends Remote {

    /**
     * Returns the billboard message
     * @return
     * @throws RemoteException
     */
    String getMessage() throws RemoteException;

    /**
     * Sets the billboard message
     * @param message
     * @throws RemoteException
     */
    void setMessage(String message) throws RemoteException;
}
