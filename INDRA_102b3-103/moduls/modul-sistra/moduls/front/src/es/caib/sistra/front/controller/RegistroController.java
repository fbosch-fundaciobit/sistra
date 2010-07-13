package es.caib.sistra.front.controller;

import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.tiles.ComponentContext;

import org.apache.commons.lang.StringUtils;

import es.caib.sistra.front.Constants;
import es.caib.sistra.model.AsientoCompleto;
import es.caib.sistra.model.ConstantesSTR;
import es.caib.sistra.model.DocumentoFront;
import es.caib.sistra.model.TramiteFront;
import es.caib.util.ConvertUtil;
import es.caib.util.StringUtil;

import es.caib.xml.datospropios.factoria.impl.Instrucciones;
import es.caib.xml.registro.factoria.impl.DatosInteresado;

public class RegistroController extends FinalizacionController
{
	public void execute(ComponentContext tileContext,
			HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws Exception
	{
		
		//super.execute( tileContext, request, response, servletContext );
		
		
		TramiteFront tramite 			= this.getTramiteFront( request );
		
		
		//
		// 	COMPROBAMOS SI HAY FLUJO DE TRAMITACION Y HAY QUE PASAR EL TRAMITE
		//
		if (tramite.isFlujoTramitacion()) {			
			// Si hay que pasarlo a alg�n Nif lo indicamos:
			String nifFlujo=tramite.getFlujoTramitacionNif();
			if (!StringUtils.isEmpty(nifFlujo)){
				request.setAttribute("pasarFlujoTramitacion",nifFlujo);
				return;
			}				
		}
		
		//
		// 	COMPROBAMOS SI TENEMOS QUE PEDIR CONFIRMACION PARA NOTIFICACION TELEMATICA
		//		
		if (!ConstantesSTR.NOTIFICACIONTELEMATICA_NOPERMITIDA.equals(tramite.getHabilitarNotificacionTelematica())){
			if (tramite.getSeleccionNotificacionTelematica() == null){
				request.setAttribute( "confirmarSeleccionNotificacionTelematica","true" );
				return;
			}else{
				request.setAttribute("seleccionNotificacionTelematica",tramite.getSeleccionNotificacionTelematica());
			}
		}
		
		
		//
		//	EXTRAEMOS ETIQUETAS SEGUN CIRCUITO
		//
		
		char tipoTramitacion 			= tramite.getTipoTramitacion();
		char tipoTramitacionDependiente = tramite.getTipoTramitacionDependiente();
		boolean registro 				= tramite.getRegistrar();
		
		String tituloKey 		= registro ? "registro.titulo.registro" : "registro.titulo.envio";
		String botonKey 		= registro ? "registro.boton.registro" 	: "registro.boton.envio";
		String instruccionesKey = registro ? "registro.instrucciones.registro" : "registro.instrucciones.envio" ;
		String importanteKey 	= registro ? "registro.importante.registro" : "registro.importante.envio" ;
		String suffix = "";
			
		if ( Constants.TIPO_CIRCUITO_TRAMITACION_DEPENDE == tipoTramitacion  )
		{
			suffix = Constants.TIPO_CIRCUITO_TRAMITACION_PRESENCIAL == tipoTramitacionDependiente ? ".presencial" : ".telematico";
			 
		}
		else
		{
			suffix = Constants.TIPO_CIRCUITO_TRAMITACION_PRESENCIAL == tipoTramitacion ?  ".presencial" : ".telematico";
		}
		
		instruccionesKey += suffix;
		importanteKey 	 += suffix;
		
		request.setAttribute( "tituloKey", tituloKey );
		request.setAttribute( "instruccionesKey", instruccionesKey );
		request.setAttribute( "importanteKey", importanteKey );
		request.setAttribute( "botonKey", botonKey );
						
		//
		// 	EXTRAEMOS INFO PARA MOSTRAR RESUMEN TRAMITE
		//		
		
		AsientoCompleto asiento = (AsientoCompleto) this.getParametros( request ).get( "asiento" );
		String asientoB64 = ConvertUtil.cadenaToBase64UrlSafe( asiento.getAsiento() );
	  			
	  	Instrucciones instrucciones = obtenerInstrucciones( asiento );
	  	request.setAttribute( "instrucciones", instrucciones );
		request.setAttribute( "asiento", asientoB64 );
		request.setAttribute( "fechaTopeEntrega", StringUtil.obtenerCadenaPorDefecto( StringUtil.fechaACadena( instrucciones.getFechaTopeEntrega()), "" ) );				
		
		DatosInteresado representante = obtenerRepresentante( asiento );
		if ( representante != null )
		{
			request.setAttribute( "representante", representante );
		}
		
		// Controlamos si el tr�mite debe firmarse digitalmente:
		// - inicio de sesi�n con certificado, el tramite requiere firma y el tipo de tramitaci�n es telem�tica
		String mostrarFirmaDigital="N";
		if  (	tramite.getDatosSesion().getNivelAutenticacion() == 'C' &&
				tramite.getFirmar() &&
				((tramite.getTipoTramitacion() == 'T' || (tramite.getTipoTramitacion() == 'D' && tramite.getTipoTramitacionDependiente() != 'P')))
			){
			mostrarFirmaDigital="S";
		}		
		request.setAttribute(Constants.MOSTRAR_FIRMA_DIGITAL,mostrarFirmaDigital);
				
		// Comprobamos si:
		//	- existe alg�n pago realizado	
		String existenPagos = "N";		
		for (Iterator it=tramite.getPagos().iterator();it.hasNext();){			
			DocumentoFront pago = (DocumentoFront) it.next();
			if (pago.getEstado() == 'S') {
				existenPagos = "S";
				break;
			}
		}		
		request.setAttribute( "existenPagos", existenPagos);
		
		//  Comprobamos si:
		//	- existe alg�n documento anexado	
		String existenAnexosTelematicos = "N";		
		for (Iterator it=tramite.getAnexos().iterator();it.hasNext();){			
			DocumentoFront anexo = (DocumentoFront) it.next();
			if (anexo.getEstado() == 'S' && anexo.isAnexoPresentarTelematicamente() && anexo.getObligatorio() != 'D') {
				existenAnexosTelematicos = "S";
				break;
			}
		}	
		request.setAttribute( "existenAnexosTelematicos", existenAnexosTelematicos);
		
		
		// Controlamos si el tr�mite es presencial	
		String presencial = "false";
		if  (  (tramite.getTipoTramitacion() == 'P') ||
			   (tramite.getTipoTramitacion() == 'D' && tramite.getTipoTramitacionDependiente() == 'P')
			)
		{
				presencial="true";
		}
		request.setAttribute( "presencial", presencial );
				
		// Indicamos que permitimos registrar (no hay que flujo ni hay que confirmar la notificacion)
		request.setAttribute( "permitirRegistrar", "true" );
	}
	
	
	
	
}
