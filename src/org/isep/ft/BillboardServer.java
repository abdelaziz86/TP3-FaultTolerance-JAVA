package org.isep.ft;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BillboardServer extends UnicastRemoteObject implements Billboard {

    private final Object msgLock = new Object();
    private String message;

    protected BillboardServer() throws RemoteException {
        super();
    }

    @Override
    public String getMessage() throws RemoteException {
        String out;
        synchronized (msgLock) {
            out = message;
        }
        return out;
    }

    @Override
    public void setMessage(String message) throws RemoteException {

        synchronized (msgLock) {
            this.message = message;
        }

    }
}
