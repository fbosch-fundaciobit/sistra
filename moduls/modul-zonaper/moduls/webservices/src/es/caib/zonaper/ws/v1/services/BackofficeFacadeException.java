
package es.caib.zonaper.ws.v1.services;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.2.2
 * Mon Feb 01 13:07:33 CET 2010
 * Generated source version: 2.2.2
 * 
 */

@WebFault(name = "fault", targetNamespace = "urn:es:caib:zonaper:ws:v1:model:BackofficeFacade")
public class BackofficeFacadeException extends Exception {
    public static final long serialVersionUID = 20100201130733L;
    
    private es.caib.zonaper.ws.v1.model.BackofficeFacadeException fault;

    public BackofficeFacadeException() {
        super();
    }
    
    public BackofficeFacadeException(String message) {
        super(message);
    }
    
    public BackofficeFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackofficeFacadeException(String message, es.caib.zonaper.ws.v1.model.BackofficeFacadeException fault) {
        super(message);
        this.fault = fault;
    }

    public BackofficeFacadeException(String message, es.caib.zonaper.ws.v1.model.BackofficeFacadeException fault, Throwable cause) {
        super(message, cause);
        this.fault = fault;
    }

    public es.caib.zonaper.ws.v1.model.BackofficeFacadeException getFaultInfo() {
        return this.fault;
    }
}
