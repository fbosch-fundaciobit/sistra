package es.caib.bantel.admin.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import es.caib.bantel.persistence.delegate.BteProcesosDelegate;
import es.caib.bantel.persistence.delegate.DelegateUtil;

/**
 * 
 * Accion que lanza el proceso de avisos monitorizacion a gestores (para pruebas, sin que se haya 
 * que esperar a la ejecucion del job)
 * 
 * @struts.action
 *  path="/avisoMonitorizacion"
 *  scope="request"
 *  validate="false"
 */
public class AvisoMonitorizacionAction extends Action
{
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception 
    {
		BteProcesosDelegate delegate = DelegateUtil.getBteProcesosDelegate();
		delegate.avisoMonitorizacion();
		
		response.getOutputStream().write("Proceso finalizado".getBytes());
		return null;
	}    
	

}
