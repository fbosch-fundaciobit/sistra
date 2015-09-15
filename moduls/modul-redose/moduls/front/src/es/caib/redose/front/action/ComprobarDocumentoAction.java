package es.caib.redose.front.action;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import es.caib.redose.front.Constants;
import es.caib.redose.modelInterfaz.DocumentoVerifier;
import es.caib.redose.modelInterfaz.KeyVerifier;
import es.caib.redose.persistence.delegate.DelegateRDSUtil;
import es.caib.redose.persistence.delegate.RdsDelegate;

/**
 * @struts.action
 *  path="/comprobarDocumento"
 *  scope="request"
 *  validate="false"
 *  
 * @struts.action-forward
 *  name="success" path=".comprobarDocumento"
 *  
 * @struts.action-forward
 *  name="fail" path=".error"
 */
public class ComprobarDocumentoAction extends BaseAction
{	
	
	private static Log _log = LogFactory.getLog( ComprobarDocumentoAction.class );
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception 
    {
		
		try{
			// Obtenemos el documento a partir del id y lo almacenamos en la sesi�n para
			// optimizar el siguiente acceso para mostrar el pdf		
			String id = request.getParameter("id");		
			if (id == null) return mapping.findForward("fail");
			
			KeyVerifier key = new KeyVerifier(id);
			
			RdsDelegate rdsDelegate = DelegateRDSUtil.getRdsDelegate();
			DocumentoVerifier documento=rdsDelegate.verificarDocumento(key);
			
			request.setAttribute("documento",documento);
			
			if (documento.getFirmas() != null && documento.getFirmas().length > 0 ){
				request.setAttribute("firmas",Arrays.asList(documento.getFirmas()));
			}else{
				request.setAttribute("firmas",new ArrayList());
			}
				
			// Lo almacenamos en la sesi�n para mostrar el pdf en el iframe (optimizamos acceso)
			request.getSession().setAttribute(id,documento);
			return mapping.findForward("success");
		}catch(Exception ex){		
			// Redirigimos a error indicando que el documento no existe
			_log.error("Excepcion comprobando documento: " + ex.getMessage(), ex);
			request.setAttribute(Constants.MESSAGE_KEY,"comprobarDocumento.noExiste");
			return mapping.findForward("fail");
		}							
    }
}
