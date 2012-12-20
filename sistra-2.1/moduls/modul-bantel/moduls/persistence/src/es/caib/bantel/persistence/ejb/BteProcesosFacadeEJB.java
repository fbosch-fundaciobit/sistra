package es.caib.bantel.persistence.ejb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.jms.DeliveryMode;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.bantel.model.CriteriosBusquedaTramite;
import es.caib.bantel.model.GestorBandeja;
import es.caib.bantel.model.Procedimiento;
import es.caib.bantel.modelInterfaz.ConstantesBTE;
import es.caib.bantel.modelInterfaz.ExcepcionBTE;
import es.caib.bantel.persistence.delegate.DelegateException;
import es.caib.bantel.persistence.delegate.DelegateUtil;
import es.caib.bantel.persistence.delegate.GestorBandejaDelegate;
import es.caib.bantel.persistence.delegate.ProcedimientoDelegate;
import es.caib.bantel.persistence.delegate.TramiteBandejaDelegate;
import es.caib.bantel.persistence.plugins.UsernamePasswordCallbackHandler;
import es.caib.bantel.persistence.util.StringUtil;
import es.caib.mobtratel.modelInterfaz.MensajeEnvio;
import es.caib.mobtratel.modelInterfaz.MensajeEnvioEmail;
import es.caib.mobtratel.persistence.delegate.DelegateMobTraTelUtil;

//TODO: Referenciar localmente a los ejbs
/**
 * SessionBean que implementa la interfaz de la BTE para la gesti�n de proceso de avisos.
 * Estos procesos se ejecutaran con el usuario auto
 *  
 *
 * @ejb.bean
 *  name="bantel/persistence/BteProcesosFacade"
 *  jndi-name="es.caib.bantel.persistence.BteProcesosFacade"
 *  type="Stateless"
 *  view-type="remote"
 *  transaction-type="Container"
 *
 * @ejb.transaction type="Required"
 * 
 * @ejb.env-entry name="colaAvisos" value="queue/AvisadorBTE" 
 * 
 */
public abstract class BteProcesosFacadeEJB implements SessionBean  {

	private static Log log = LogFactory.getLog(BteProcesosFacadeEJB.class);
	private long intervaloSeguridad=0;
	private int maximoDiasAviso=0;
		
	/**
     * @ejb.create-method
     * @ejb.permission unchecked = "true"
     */
	public void ejbCreate() throws CreateException {	
		try{			 
			intervaloSeguridad =  Long.parseLong(DelegateUtil.getConfiguracionDelegate().obtenerConfiguracion().getProperty("avisoPeriodico.intervaloSeguridad"));			
		}catch(Exception ex){
			log.error("Excepcion obteniendo parametro de intervalo de seguridad. Se tomara valor="+intervaloSeguridad,ex );
		}
		try{			 
			maximoDiasAviso =  Integer.parseInt(DelegateUtil.getConfiguracionDelegate().obtenerConfiguracion().getProperty("avisoPeriodico.maxDiasAviso"));			
		}catch(Exception ex){
			log.error("Excepcion obteniendo parametro de maximo dias aviso. Se tomara valor="+maximoDiasAviso,ex );
		}
		
	}
	
    /**
     * Realiza proceso de aviso de nuevas entradas a BackOffices
     * 
     * @ejb.interface-method
     * @ejb.permission unchecked = "true"
     */
    public void avisoBackOffices()  throws ExcepcionBTE{
    	
    	LoginContext lc = null;
    	
    	try{
    		// Realizamos login JAAS con usuario para proceso automatico
			Properties props = DelegateUtil.getConfiguracionDelegate().obtenerConfiguracion();
			String user = props.getProperty("auto.user");
			String pass = props.getProperty("auto.pass");
			CallbackHandler handler = new UsernamePasswordCallbackHandler( user, pass ); 					
			lc = new LoginContext("client-login", handler);
			lc.login();	
    		
	    	// Recuperamos lista de procedimientos
	    	ProcedimientoDelegate td = DelegateUtil.getTramiteDelegate();
	    	List list = td.listarProcedimientos();
	    		    	
	    	// Para los tramites que tengan configurado el proceso de aviso consultamos nuevas entradas (aplicando intervalo de seguridad para evitar
	    	// solapamiento entre aviso inmediato y periodico)
	    	Date ahora = new Date();
	    	Date hasta = new Date( ahora.getTime() - (intervaloSeguridad * 60 * 1000) );
	    	
	    	
	    	for (Iterator it = list.iterator();it.hasNext();){
	    		Procedimiento procedimiento =  (Procedimiento) it.next();
	    		
	    		// Si no tiene un intervalo positivo no esta habilitado el proceso de aviso para el tr�mite 
	    		if (procedimiento.getIntervaloInforme() == null || procedimiento.getIntervaloInforme().intValue() <= 0) continue;
	    		
	    		// Comprobamos si se ha cumplido el intervalo
	    		if (procedimiento.getUltimoAviso() != null &&
	    			( (procedimiento.getUltimoAviso().getTime() + (procedimiento.getIntervaloInforme().longValue() * 60 * 1000)) > ahora.getTime() ) ) continue;
	    		
	    		// Marcamos con error las entradas que no han sido procesadas
	    		controlEntradasCaducadas(procedimiento.getIdentificador());
	    		
	    		// Si se ha cumplido el intervalo avisamos al backoffice de las nuevas entradas
	    		avisoBackOffice(procedimiento,hasta);
	    		
	    		// Guardamos tiempo en que se ha realizado el aviso	    		
	    		marcarAvisoRealizado(procedimiento.getIdentificador(), ahora);	   
	    	}	    		    	
    	}catch(Exception ex){
    		log.error("Excepci�n en proceso de aviso a BackOffices",ex);
    		throw new ExcepcionBTE("Excepci�n en proceso de aviso a BackOffices",ex);
    	}finally{
    		// Hacemos el logout
			if ( lc != null ){
				try{lc.logout();}catch(Exception exl){}
			}
    	}
	    	
    }

    /**
     * Marca aviso realizado para procedimiento.
     * @param idProcedimiento 
     * @param ahora
     * @throws DelegateException
     */
    private void marcarAvisoRealizado(String idProcedimiento,
			Date ahora) throws DelegateException {
		DelegateUtil.getBteOperacionesProcesosDelegate().marcarAvisoRealizado(idProcedimiento, ahora);    	
	}
    
    /**
     * Marca las entradas como procesadas con error si no se han procesado dentro del limite de dias establecido.
     * @param idProcedimiento Procedimiento
     * @param hasta Fecha limite busqueda
     * @throws DelegateException 
     */
    private void controlEntradasCaducadas(String idProcedimiento) throws DelegateException {
		if (maximoDiasAviso > 0) {
    		// Calculamos fecha limite: restamos a fecha actual el numero maximo de dias limite para avisar
    		Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, maximoDiasAviso * -1);
    		Date fechaLimite = calendar.getTime();
    		
    		DelegateUtil.getBteOperacionesProcesosDelegate().marcarEntradasCaducadas(idProcedimiento, fechaLimite);
    	}        	
	}

	/**
     * Realiza proceso de aviso de nuevas entradas a Gestores
     * 
     * @ejb.interface-method
     * @ejb.permission unchecked = "true"
     */
    public void avisoGestores()  throws ExcepcionBTE{
    	LoginContext lc = null;
    	
    	try {
    		// Realizamos login JAAS con usuario para proceso automatico
			Properties props = DelegateUtil.getConfiguracionDelegate().obtenerConfiguracion();
			String user = props.getProperty("auto.user");
			String pass = props.getProperty("auto.pass");
			CallbackHandler handler = new UsernamePasswordCallbackHandler( user, pass ); 					
			lc = new LoginContext("client-login", handler);
			lc.login();	
    		
    		// Recuperamos lista de gestores
    		GestorBandejaDelegate gb = DelegateUtil.getGestorBandejaDelegate();
    		TramiteBandejaDelegate tbd = (TramiteBandejaDelegate) DelegateUtil.getTramiteBandejaDelegate();
    		List list = gb.listarGestoresBandeja();
    		String numEntradas,intervalo,titulo;
    		StringBuffer mensajeIntervalo,mensaje;
    		Hashtable entradasProcedimiento = new Hashtable();
    		boolean existenEntradasTramite,existenEntradas;
    		long num;
    		
    		// Para los gestores que tengan configurado el proceso de aviso consultamos nuevas entradas
    		// Establecemos un intervalo de seguridad (10 min) para evitar que entradas recientes nos alerten de que
    		// aun no estan procesadas
    		long ventanaTiempo = 10;
	    	Date ahora = new Date();
	    	Date desde;
	    	Date hasta = new Date( ahora.getTime() - (ventanaTiempo * 60 * 1000) );
	    	
	    	
	    	// Montamos emails a enviar 	    	
	    	MensajeEnvio enviosEmail = new MensajeEnvio();
	    	
	    	// Recorremos los gestores y generamos mensaje personalizado
	    	for (Iterator it = list.iterator();it.hasNext();){
	    		
	    		// Obtenemos siguiente gestor
	    		GestorBandeja g =  (GestorBandeja) it.next();
	    		
	    		// Creamos string buffer para el mensaje
	    		mensaje = new StringBuffer(8192); 	
	    			    		
	    		// Si no tiene un intervalo positivo no esta habilitado el proceso de aviso 
	    		if (g.getIntervaloInforme() == null || g.getIntervaloInforme().intValue() <= 0) continue;
	    		
	    		// Comprobamos si se ha cumplido el intervalo
	    		if (g.getUltimoAviso() != null &&
	    			( (g.getUltimoAviso().getTime() + (g.getIntervaloInforme().longValue() * 60  * 60  * 1000)) > ahora.getTime() ) ) continue;	    	
	    		
	    		// Calculamos texto para intervalo
	    		SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss");
	    		intervalo ="";	    		
	    		if (g.getUltimoAviso() != null) {
	    			desde = new Date( g.getUltimoAviso().getTime() - (ventanaTiempo * 60 * 1000) );
	    			intervalo += "des de " + sdf.format( desde ) + " ";
	    		}else{
	    			desde = null;
	    		}
	    		intervalo += "fins a " + sdf.format( hasta );	 
	    		
	    		// Establecemos titulo e inicializamos mensaje
	    		titulo = "Safata Telem�tica  - " + intervalo;
	    		mensaje.append("<html><head><style type=\"text/css\">body	{font-family: verdana, arial, helvetica, sans-serif; font-size: 8pt; color: #515b67;}</style></head><body>");
	    		mensaje.append("Informe d'entrades en la Safata Telem&agrave;tica  (" + intervalo + ") <br/><br/>");
	    		
	    		// Marcamos como que no hay entradas xa avisar
	    		existenEntradas=false;
	    		
	    		// Obtenemos entradas nuevas para los tramites asociados al gestor
	    		for (Iterator it2 = g.getProcedimientosGestionados().iterator();it2.hasNext();){	    				    			
	    			
	    			// Obtenemos siguiente tramite gestionado por el gestor
	    			Procedimiento procedimiento = (Procedimiento) it2.next();	   	    			
	    			
	    			// Creamos mensaje para el intervalo / tramite en cuestion
	    			mensajeIntervalo = new StringBuffer(1024);
	    			
	    			// Indicamos que de momento no se ha producido ninguna entrada
	    			existenEntradasTramite=false;
	    			
	    			// TODO Para nueva version sustituir quitarAcentos por escapeHtml de commonslang
	    			String desc = procedimiento.getDescripcion(); 
	    			try{
	    				desc = es.caib.util.StringUtil.quitaAcentos(procedimiento.getDescripcion());
	    			}catch(Throwable tw){}	    				    		
	    			
	    			mensajeIntervalo.append("<strong> * " + procedimiento.getIdentificador() + " - " + desc + "</strong> <br/>");
	    			mensajeIntervalo.append(" 	Noves entrades produ&iuml;des en l'interval <br/>");	    			
	    			
	    			// Buscar entradas procesadas ok / ko / no proc en el intervalo	    			
	    			
	    			// - procesadas OK
	    			numEntradas = (String) entradasProcedimiento.get(procedimiento.getIdentificador() + " " + intervalo + " " + ConstantesBTE.ENTRADA_PROCESADA);
	    			if (numEntradas==null){	    			
	    				num = tbd.obtenerTotalEntradasProcedimiento(procedimiento.getIdentificador(),ConstantesBTE.ENTRADA_PROCESADA,desde,hasta);	    				
	    				entradasProcedimiento.put(procedimiento.getIdentificador() + " " + intervalo + " " + ConstantesBTE.ENTRADA_PROCESADA,Long.toString(num));	
	    			}else{
	    				num = Long.parseLong(numEntradas);
	    			}	
	    			if (num > 0) existenEntradasTramite=true;
	    			mensajeIntervalo.append("	 	- Processades correctament:" + num + " <br/>");
	    			
	    			// - procesadas KO
	    			numEntradas = (String) entradasProcedimiento.get(procedimiento.getIdentificador() + " " + intervalo + " " + ConstantesBTE.ENTRADA_PROCESADA_ERROR);
	    			if (numEntradas==null){	    			
	    				num = tbd.obtenerTotalEntradasProcedimiento(procedimiento.getIdentificador(),ConstantesBTE.ENTRADA_PROCESADA_ERROR,desde,hasta);	    				
	    				entradasProcedimiento.put(procedimiento.getIdentificador() + " " + intervalo + " " + ConstantesBTE.ENTRADA_PROCESADA_ERROR,Long.toString(num));	
	    			}else{
	    				num = Long.parseLong(numEntradas);
	    			}
	    			if (num > 0) existenEntradasTramite=true;
	    			mensajeIntervalo.append("	 	- Processades amb error:" + num + " <br/>");
	    			
	    			// - no procesadas
	    			numEntradas = (String) entradasProcedimiento.get(procedimiento.getIdentificador() + " " + intervalo + " " + ConstantesBTE.ENTRADA_NO_PROCESADA);
	    			if (numEntradas==null){	    			
	    				num = tbd.obtenerTotalEntradasProcedimiento(procedimiento.getIdentificador(),ConstantesBTE.ENTRADA_NO_PROCESADA,desde,hasta);	    				
	    				entradasProcedimiento.put(procedimiento.getIdentificador() + " " + intervalo + " " + ConstantesBTE.ENTRADA_NO_PROCESADA,Long.toString(num));	
	    			}else{
	    				num = Long.parseLong(numEntradas);
	    			}
	    			if (num > 0) existenEntradasTramite=true;
	    			mensajeIntervalo.append("	 	- No processades:" + num + " <br/>");
	    			mensajeIntervalo.append(" <br/>");
	    			
	    			
	    			// Avisos especiales (s�lo para tramites con procesos automaticos)
	    			if (procedimiento.getIntervaloInforme() != null && procedimiento.getIntervaloInforme().longValue() > 0){
		    			// 	- Buscar entradas ko (sin intervalo)
		    			num = tbd.obtenerTotalEntradasProcedimiento(procedimiento.getIdentificador(),ConstantesBTE.ENTRADA_PROCESADA_ERROR,null,hasta);
		    			if (num > 0) {
		    				existenEntradasTramite=true;	    			
		    				mensajeIntervalo.append(" 	ATENCI&Oacute;: EXISTEIXEN ENTRADES PROCESSADES AMB ERROR QUE HAN DE SER REVISADES (TOTAL:" + num + ") <br/>") ;
		    			}
		    			
		    			//   - Buscar entradas sin procesar anteriores al intervalo
		    			num = tbd.obtenerTotalEntradasProcedimiento(procedimiento.getIdentificador(),ConstantesBTE.ENTRADA_NO_PROCESADA,null,desde);
		    			if (num > 0) {
		    				existenEntradasTramite=true;	    			
		    				mensajeIntervalo.append(" 	ATENCI&Oacute;: SEGUEIXEN HAVENT ENTRADES SENSE PROCESSAR ANTERIORS A L'INTERVAL ACTUAL (TOTAL:" + num + ") <br/>" );
		    			}
	    			}
	    			
	    			// Si hay algun movimiento anexamos a mensaje
	    			if (existenEntradasTramite){
	    				existenEntradas=true;
	    				mensaje.append(mensajeIntervalo.toString() + "<br/>");	    	    				
	    			}
	    			
	    		}
	    		
	    		// Enviamos correo a gestor con nuevas entradas
	    		if (existenEntradas) {
	    			
	    			mensaje.append("</body></html>");
	    			
	    			MensajeEnvioEmail me = new MensajeEnvioEmail();
	    			String [] dest = {g.getEmail()};
	    			me.setHtml(true);
	    			me.setDestinatarios(dest);
	    			me.setTitulo(titulo);
	    			me.setTexto(mensaje.toString());
	    			enviosEmail.addEmail(me);	    		    				    			
	    		}
	    		
	    		// Guardamos tiempo en que se ha realizado el aviso
	    		g.setUltimoAviso(ahora);
		    	gb.avisoRealizado(g.getSeyconID(),ahora);
		    	
	    	}	    		
	    	
	    	// Enviamos al modulo de movilidad los emails
	    	if (enviosEmail.getEmails() != null && enviosEmail.getEmails().size() > 0){
	    	enviosEmail.setNombre("Avisos a gestores");
	    	enviosEmail.setCuentaEmisora(DelegateUtil.getConfiguracionDelegate().obtenerConfiguracion().getProperty("avisosGestores.cuentaEnvio"));
	    	enviosEmail.setFechaCaducidad(new Date(System.currentTimeMillis() + 86400000L )); // Damos 1 d�a para intentar enviar
	    	enviosEmail.setInmediato(true);
	    	DelegateMobTraTelUtil.getMobTraTelDelegate().envioMensaje(enviosEmail);
	    	}
	    	
    	}catch (Exception ex){
    		log.error("Excepci�n enviando correo a gestor",ex);
    		throw new ExcepcionBTE("Excepci�n en proceso de aviso a BackOffices",ex);
    	}finally{
    		// Hacemos el logout
			if ( lc != null ){
				try{lc.logout();}catch(Exception exl){}
			}
    	}
    	
    }
    
    /**
     * Realiza proceso de aviso a BackOffice para un tr�mite. Metemos en cola as�ncrona.
     * En caso de error lanza mensaje al log y permite continuar.
     * @param procedimiento
     */
    private void avisoBackOffice(Procedimiento procedimiento,Date hasta){    	
    	try{    		
    		
    		log.debug("Aviso a backoffice procedimiento  " + procedimiento.getIdentificador() + " (hasta " + es.caib.util.StringUtil.fechaACadena(hasta,es.caib.util.StringUtil.FORMATO_TIMESTAMP) + ")");
    		
    		// Obtenemos entradas no procesadas    		    		    	
    		TramiteBandejaDelegate tbd = (TramiteBandejaDelegate) DelegateUtil.getTramiteBandejaDelegate();
    		
    		// Obtenemos tramites del procedimiento que tienen entradas pendientes y generamos un mensaje por tramite
    		String idTramites[] = tbd.obtenerIdTramitesProcedimiento(procedimiento.getIdentificador(),ConstantesBTE.ENTRADA_NO_PROCESADA,null,hasta);
    		if (idTramites != null) {
    			for (int i = 0; i < idTramites.length; i++) {
    		
    				String  entradas [] = tbd.obtenerNumerosEntradas(procedimiento.getIdentificador(), idTramites[i], ConstantesBTE.ENTRADA_NO_PROCESADA,null,hasta);
		    		
		    		log.debug("Aviso de " + entradas.length + " nuevas entradas para backoffice tr�mite " + idTramites[i] + " hasta " + es.caib.util.StringUtil.fechaACadena(hasta,es.caib.util.StringUtil.FORMATO_TIMESTAMP) + " (Procedimiento: " + procedimiento.getIdentificador() + ")");
		    		
		    		if (entradas.length > 0){
						// Dejamos entrada en la cola de avisos
				    	InitialContext ctx = new InitialContext();
				    	String colaAvisos = (String) ctx.lookup("java:comp/env/colaAvisos");
					    Queue queue = (Queue) ctx.lookup(colaAvisos);		 
					    QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("java:/XAConnectionFactory");
					    QueueConnection cnn = factory.createQueueConnection();
					    QueueSession sess = cnn.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);    		  
						TextMessage msg = sess.createTextMessage(StringUtil.numeroEntradasToString(entradas));
						QueueSender sender = sess.createSender(queue);
						sender.send(msg,DeliveryMode.NON_PERSISTENT,4,0);				
		    		}
    			}
    		}
    	}catch(Exception ex){
    		log.error("Excepci�n en proceso de aviso a BackOffice para procedimiento " + procedimiento.getIdentificador(),ex);    		
    	}    		
    }    	    
    
}
