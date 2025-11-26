package com.incloud.hcp.service.wsdlSunat;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import java.rmi.RemoteException;

public class BillConsultServiceProxy implements BillConsultService{
    private String _endpoint = null;
    private BillConsultService billConsultService = null;

    public BillConsultServiceProxy() {
        _initBillServiceProxy();
    }

    public BillConsultServiceProxy(String endpoint) {
        _endpoint = endpoint;
        _initBillServiceProxy();
    }

    private void _initBillServiceProxy() {
        try {
            billConsultService = (new BillConsultPortBidingServiceLocator()).getBillConsultServicePort();
            if (billConsultService != null) {
                if (_endpoint != null)
                    ((Stub)billConsultService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
                else
                    _endpoint = (String)((Stub)billConsultService)._getProperty("javax.xml.rpc.service.endpoint.address");
            }

        }
        catch (ServiceException serviceException) {}
    }

    @Override
    public StatusResponse getStatus(String rucComprobante, String tipoComprobante, String serieComprobante, Integer nroComprobante) throws RemoteException {
        if (billConsultService == null)
            _initBillServiceProxy();
        return billConsultService.getStatus(rucComprobante, tipoComprobante, serieComprobante, nroComprobante);
    }
}
