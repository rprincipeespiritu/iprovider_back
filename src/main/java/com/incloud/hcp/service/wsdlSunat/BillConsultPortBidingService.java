package com.incloud.hcp.service.wsdlSunat;

import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import java.net.URL;

public interface BillConsultPortBidingService extends Service {
    BillConsultService getBillConsultServicePort() throws ServiceException;
    BillConsultService getBillConsultServicePort(URL portAddress) throws ServiceException;
}
