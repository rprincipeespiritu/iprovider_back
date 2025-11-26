package com.incloud.hcp.service.wsdlSunat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BillConsultService extends Remote {
    StatusResponse getStatus(String rucComprobante, String tipoComprobante, String serieComprobante, Integer nroComprobante) throws RemoteException;
}
