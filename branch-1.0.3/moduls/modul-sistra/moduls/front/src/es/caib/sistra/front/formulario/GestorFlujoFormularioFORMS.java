package es.caib.sistra.front.formulario;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.sistra.front.Constants;
import es.caib.sistra.front.util.Util;
import es.caib.sistra.model.ConfiguracionFormulario;
import es.caib.sistra.model.ConfiguracionGestorFlujoFormulario;
import es.caib.sistra.model.DocumentoFront;
import es.caib.sistra.model.TramiteFront;
import es.caib.sistra.persistence.delegate.DelegateUtil;
import es.caib.util.StringUtil;
import es.caib.xml.ConstantesXML;
import es.caib.xml.formsconf.factoria.FactoriaObjetosXMLConfForms;
import es.caib.xml.formsconf.factoria.ServicioConfFormsXML;
import es.caib.xml.formsconf.factoria.impl.ConfiguracionForms;
import es.caib.xml.formsconf.factoria.impl.Datos;
import es.caib.xml.formsconf.factoria.impl.Propiedad;


public class GestorFlujoFormularioFORMS implements GestorFlujoFormulario, Serializable
{
	// Propiedades est�ticas del manejador
	private static boolean initialized = false; // Solo las inicializar� la primera instancia que se cree
	private static Log log = LogFactory.getLog( GestorFlujoFormularioFORMS.class );
	//  - Urls sistra
	private static String URL_OK = null;
	private static String URL_REDIRECCION_OK = null;
	private static String URL_CANCEL = null;
	private static String URL_REDIRECCION_CANCEL = null;
	private static String URL_SISTRA = null;
	// - Nombre parametros
	private static String RESULT_PARAM 	= "es.caib.sistra.front.formulario.result@";
	private static String CANCEL_PARAM 	= "es.caib.sistra.front.formulario.cancelacio@";
	private static String TOKEN_NAME = Constants.GESTOR_FORM_PARAM_TOKEN_LLAMADA;
	private static String TOKEN_NAME_RETORNO = Constants.GESTOR_FORM_PARAM_TOKEN_RETORNO;
	private static String PARAM_XML_DATOS_FIN_NAME = Constants.GESTOR_FORM_PARAM_XML_DATOS_FIN;
	private static String PARAM_XML_DATOS_INICIO_NAME = Constants.GESTOR_FORM_PARAM_XML_DATOS_INI;
	
	// TODO rafa: XA REVISAR !!!
	// Propiedades forms
	private static String DEFAULT_PERFIL = "CAIB_AZUL";
	private static String DEFAULT_LAYOUT = "caib";
		
	// Propiedades de instancia
	// - Configuracion formulario
	private Map initParams;
	// - Almacenamiento datos del formulario
	private long expirationTime;
	private HashMap storingArea = new HashMap();	
	
	// Url server forms
	//private static String urlForms;
	
	public void init ( Map initParams )
	{		
		// Guardamos las propiedades de inicio para las siguientes llamadas
		this.initParams = initParams;
		
		// Inicializamos propiedades estaticas de la clase (solo la primera instancia que se crea de la clase) 
		if ( !initialized )
		{
			try
			{
				String urlSistra = DelegateUtil.getConfiguracionDelegate().obtenerConfiguracion().getProperty("sistra.url");
				
				// Urls sistra
				URL_SISTRA 					= urlSistra;
				URL_OK  					= urlSistra + getParametroConfiguracion( initParams,  "sistra.urlSisTraOK" );
				URL_REDIRECCION_OK 			= urlSistra + getParametroConfiguracion( initParams,  "sistra.urlRedireccionOK" );
				URL_CANCEL  				= urlSistra + getParametroConfiguracion( initParams,  "sistra.urlSisTraCancel" );
				URL_REDIRECCION_CANCEL 		= urlSistra + getParametroConfiguracion( initParams,  "sistra.urlRedireccionCancel" );
				
			}
			catch ( Exception exc )
			{
				log.error( exc );
			}
			initialized = true;
		}
	}
	

	
	public boolean continuarCancelacion(String token)
	{
		log.debug( "Continuando tramitaci�n con cancelaci�n" );
		Boolean cancelacio = ( Boolean ) storingArea.get( CANCEL_PARAM + token );
		cancelacio = cancelacio == null ? Boolean.FALSE : cancelacio;
		storingArea.remove( CANCEL_PARAM + token );
		storingArea.clear();
		storingArea = null;
		return cancelacio.booleanValue();
	}

	
	public String cancelarFormulario()
	{
		log.debug( "Cancelando formulario" );
		String token = Util.generateToken();
		storingArea.remove( RESULT_PARAM );
		storingArea.put( CANCEL_PARAM + token, Boolean.TRUE );
		return token;
	}

	public ResultadoProcesoFormulario continuarTramitacion(String token)
	{
		ResultadoProcesoFormulario resultado = 
		 ( ResultadoProcesoFormulario ) storingArea.get( RESULT_PARAM  + token );
		storingArea.clear();
		storingArea = null;
		return resultado;
	}

	public String guardarDatosFormulario(String xmlInicial, String xmlActual)
	{
		try
		{
	        String token = Util.generateToken();
	        ResultadoProcesoFormulario resultado = ( ResultadoProcesoFormulario ) storingArea.get( RESULT_PARAM );
	        storingArea.remove( RESULT_PARAM );
	        resultado.setXmlActual( xmlActual );
	        resultado.setXmlInicial( xmlInicial );
	        
	        storingArea.put( RESULT_PARAM  + token, resultado );
	        return token;
		}
		catch ( Exception exc )
		{
			log.error( exc );
			return null;
		}
	}

	public String irAFormulario( ConfiguracionGestorFlujoFormulario confGestorForm, 
								 DocumentoFront formulario,
								 TramiteFront informacionTramite, 								 
								 Map parametrosRetorno  )
	{
		
		// Inicializamos urls gestor formulario
		String urlGestor = null;
		String URL_TRAMITACION_FORMULARIO = null;
		String URL_REDIRECCION_FORMULARIO = null;
				
		try{
			urlGestor = confGestorForm.getGestorFormulario().getUrlGestor();
			urlGestor = StringUtil.replace(urlGestor,"@sistra.url@",URL_SISTRA);
			
			URL_TRAMITACION_FORMULARIO 	= confGestorForm.getGestorFormulario().getUrlTramitacionFormulario();
			URL_REDIRECCION_FORMULARIO 	= confGestorForm.getGestorFormulario().getUrlRedireccionFormulario();
			
			// Reemplazamos urls que pueden llevar parametrizada la url
			URL_TRAMITACION_FORMULARIO = StringUtil.replace(URL_TRAMITACION_FORMULARIO,"@forms.server@",urlGestor);
			URL_REDIRECCION_FORMULARIO 	= StringUtil.replace(URL_REDIRECCION_FORMULARIO,"@forms.server@",urlGestor);			
			
			log.debug( "URL_TRAMITACION_FORMULARIO:" + URL_TRAMITACION_FORMULARIO);
		}catch(Exception ex){
			 log.error("Error obteniendo urls gestor formulario '" + confGestorForm.getGestorFormulario().getIdentificador() + "'");
             return null;
		}
		
		Locale locale = informacionTramite.getDatosSesion().getLocale();
		
		HttpClientParams paramsHttp = new HttpClientParams();
		paramsHttp.setConnectionManagerTimeout(30 * 1000); // Esperamos 30 seg a conectar con Forms
		paramsHttp.setSoTimeout(30 * 1000);
        HttpClient client = new HttpClient(paramsHttp);
        PostMethod method = new PostMethod(URL_TRAMITACION_FORMULARIO);
        try 
        {
	        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        method.getParams().setContentCharset("UTF-8");
	        method.addParameter("xmlData", confGestorForm.getDatosActualesFormulario());
	        //obtener la representacion xml del objeto de configuracion del formulario
	        method.addParameter("xmlConfig", 
	        			obtenerXMLConfiguracion(confGestorForm.getPropiedad("tituloAplicacion"),
	        									formulario,
	        									confGestorForm.getPropiedad("modelo"),
	        									Integer.parseInt(confGestorForm.getPropiedad("version")),
	        									locale,
	        									informacionTramite,
	        									confGestorForm.getConfiguracionFormulario(),
	        									parametrosRetorno ) );
	        	      
            int status = client.executeMethod(method);

            if (status != HttpStatus.SC_OK) {
                log.error("Error iniciando tramite: " + status);
                return null;
            }
                      
            String token= method.getResponseBodyAsString();            
            Map params = new HashMap();
            params.put(TOKEN_NAME, token);                                  
            String url = this.appendParametersToURL( URL_REDIRECCION_FORMULARIO, params);
            
            
            ResultadoProcesoFormulario resultado = new ResultadoProcesoFormulario();
            resultado.setFormulario( formulario );
            //resultado.setXmlInicial( xmlInicial );
            
            storingArea.put( RESULT_PARAM, resultado );
            

            return url;

        } catch (Exception e) {
        	log.error( "Error conectando con Forms: " + e.getMessage(),e );
            return null;
        } finally {
            method.releaseConnection();
        }
	}
	
	private String obtenerXMLConfiguracion( String tituloAplicacion,DocumentoFront formulario,String modelo, int version, Locale locale, 
											TramiteFront informacionTramite, ConfiguracionFormulario configuracionFormulario, 
											Map parametrosRetorno ) throws Exception
	{
		FactoriaObjetosXMLConfForms factory = ServicioConfFormsXML.crearFactoriaObjetosXML();
		factory.setEncoding( ConstantesXML.ENCODING);
		
		ConfiguracionForms objXmlConfiguracion = factory.crearConfiguracionForms();
		
		// Datos
		Datos datos = factory.crearDatos();				
		datos.setIdioma( locale.getLanguage() );
		datos.setModelo( modelo );
		datos.setVersion( new Integer(version) );
		datos.setCodigoPerfil( DEFAULT_PERFIL );
		datos.setLayout( DEFAULT_LAYOUT );
		datos.setUrlSisTraOK( this.appendParametersToURL( URL_OK, parametrosRetorno )  );
		datos.setUrlRedireccionOK( this.appendParametersToURL( URL_REDIRECCION_OK, parametrosRetorno )  );
		datos.setUrlSisTraCancel( this.appendParametersToURL( URL_CANCEL, parametrosRetorno )  );
		datos.setUrlRedireccionCancel( this.appendParametersToURL( URL_REDIRECCION_CANCEL, parametrosRetorno )  );
		datos.setUrlSisTraMantenimientoSesion( Util.generaUrlMantenimientoSesion((String) parametrosRetorno.get(TOKEN_NAME)) );
		
		datos.setNomParamTokenRetorno( TOKEN_NAME_RETORNO );
		datos.setNomParamXMLDatosFin( PARAM_XML_DATOS_FIN_NAME );
		datos.setNomParamXMLDatosIni( PARAM_XML_DATOS_INICIO_NAME );
		objXmlConfiguracion.setDatos( datos );
		
		
		// Propiedades: T�tulo Aplicaci�n, Nombre usuario, Nombre Formulario y Nombre Tr�mite
		Propiedad propiedadTituloAplicacion = factory.crearPropiedad();
		propiedadTituloAplicacion.setNombre( "aplicacion" );
		propiedadTituloAplicacion.setValor(tituloAplicacion); 
		objXmlConfiguracion.getPropiedades().put(propiedadTituloAplicacion.getNombre(),propiedadTituloAplicacion);
				
		String nombreUsuario = informacionTramite.getDatosSesion().getNombreCompletoUsuario();
		if ( !StringUtils.isEmpty( nombreUsuario ) )
		{
			Propiedad propiedadNombreUsuario = factory.crearPropiedad();
			propiedadNombreUsuario.setNombre( "usuario" );
			propiedadNombreUsuario.setValor( nombreUsuario );
			objXmlConfiguracion.getPropiedades().put(propiedadNombreUsuario.getNombre(),propiedadNombreUsuario);
		}		
		
		Propiedad propiedadNombreTramite = factory.crearPropiedad();
		propiedadNombreTramite.setNombre( "tramite" );
		propiedadNombreTramite.setValor( informacionTramite.getDescripcion() ); 
		objXmlConfiguracion.getPropiedades().put(propiedadNombreTramite.getNombre(),propiedadNombreTramite);		
		
		Propiedad propiedadNombreFormulario = factory.crearPropiedad();
		propiedadNombreFormulario.setNombre( "formulario" );
		propiedadNombreFormulario.setValor( formulario.getDescripcion() ); 
		objXmlConfiguracion.getPropiedades().put(propiedadNombreFormulario.getNombre(),propiedadNombreFormulario);
		

		// Propiedades espec�ficas establecidas por script
		for (Iterator it=configuracionFormulario.getPropiedades().keySet().iterator();it.hasNext();){
			String nombrePropiedad = (String) it.next();
			String valorPropiedad = (String)configuracionFormulario.getPropiedades().get(nombrePropiedad);
			
			Propiedad propiedadEspecifica = factory.crearPropiedad();
			propiedadEspecifica.setNombre( nombrePropiedad );
			propiedadEspecifica.setValor( valorPropiedad ); 
			objXmlConfiguracion.getPropiedades().put(propiedadEspecifica.getNombre(),propiedadEspecifica);			
		}
		
		
		// Bloqueo de campos
		configuracionFormulario.getCamposReadOnly();
		ArrayList arlCamposReadOnly = configuracionFormulario.getCamposReadOnly();		
		for ( int i = 0; i < arlCamposReadOnly.size(); i++ )
		{
			String xpathBloqueo = ( String ) arlCamposReadOnly.get( i ); 
			objXmlConfiguracion.getBloqueo().add(xpathBloqueo);
		}					
		
		// Generamos XML
		return factory.guardarConfiguracionForms( objXmlConfiguracion );
		
	}
	
	private String appendParametersToURL( String URL, Map params )
	{
		
		String strReturn = URL;
		for ( Iterator it = params.keySet().iterator(); it.hasNext(); )
		{
			String paramName = ( String ) it.next();
			String paramValue =( String ) params.get( paramName );
			strReturn = appendParameterToURL( strReturn, paramName, paramValue );
		}
		return strReturn;
		//return URL;
	}
	
	
	private String appendParameterToURL( String URL, String paramName, String paramValue )
	{
		StringBuffer url = new StringBuffer(URL);
	    url.append( URL.indexOf('?') == -1 ? '?' : '&');
	    url.append(paramName);
	    url.append('=');
	    url.append(paramValue);
	    return url.toString();
	}

	public long getExpirationTime()
	{
		return this.expirationTime;
	}

	public void setExpirationTime(long expirationTime)
	{
		this.expirationTime = expirationTime;	
	}
	
	private String getParametroConfiguracion( Map initParams, String key ) throws Exception
	{
		String value = ( String ) initParams.get( key );
		if ( value == null )
		{
			throw new Exception( "La propiedad <" + key + "> no est� definida en el fichero de propiedades GestorFlujoFormularioFORMS.properties" );
		}
		return value;	
	}
	
}
