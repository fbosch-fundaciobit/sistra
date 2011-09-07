package es.caib.zonaper.persistence.delegate;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import es.caib.zonaper.modelInterfaz.ExcepcionPAD;
import es.caib.zonaper.modelInterfaz.PersonaPAD;
import es.caib.zonaper.persistence.intf.PadAplicacionFacade;
import es.caib.zonaper.persistence.util.PadAplicacionFacadeUtil;

/**
 * Interfaz para operar con la Aplicacion PAD
 */
public class PadAplicacionDelegate implements StatelessDelegate {

    public PersonaPAD obtenerDatosPersonaPADporUsuario( String usuario ) throws DelegateException
	{
		try
		{
			return getFacade().obtenerDatosPersonaPADporUsuario( usuario );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
	public PersonaPAD obtenerDatosPersonaPADporNif( String nif ) throws DelegateException
	{
		try
		{
			return getFacade().obtenerDatosPersonaPADporNif( nif );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
	 public boolean existePersonaPADporUsuario( String usuario )throws DelegateException
		{
			try
			{
				return getFacade().existePersonaPADporUsuario( usuario );
			}
			catch (Exception e) 
			{
	            throw new DelegateException(e);
	        }
		}
	
	public PersonaPAD altaPersona( PersonaPAD personaPAD ) throws DelegateException
	{
		try
		{
			return getFacade().altaPersona( personaPAD );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
	public void modificarPersona( PersonaPAD personaPAD ) throws DelegateException
	{
		try
		{
			getFacade().modificarPersona( personaPAD );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
	public int[] validarModificacionDatosPersonaPAD(PersonaPAD persona)throws DelegateException
	{
		try
		{
			return getFacade().validarModificacionDatosPersonaPAD( persona );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
    public PersonaPAD obtenerHelpdeskDatosPersonaPorUsuario( String codUsu) throws DelegateException{
		try
		{
			return getFacade().obtenerHelpdeskDatosPersonaPorUsuario( codUsu );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
    
    public PersonaPAD obtenerHelpdeskDatosPersonaPorNif( String nif) throws DelegateException{
		try
		{
			return getFacade().obtenerHelpdeskDatosPersonaPorNif( nif );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
	public void modificarHelpdeskCodigoUsuario( String usuOld, String usuNew) throws DelegateException{
		try
		{
			getFacade().modificarHelpdeskCodigoUsuario( usuOld, usuNew);
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
	public List buscarEntidades( String nifEntidad) throws DelegateException{
		try
		{
			return getFacade().buscarEntidades( nifEntidad );
		}
		catch (Exception e) 
		{
            throw new DelegateException(e);
        }
	}
	
    /* ========================================================= */
    /* ======================== REFERENCIA AL FACADE  ========== */
    /* ========================================================= */
    private PadAplicacionFacade getFacade() throws NamingException,CreateException,RemoteException {      	    	
    	return PadAplicacionFacadeUtil.getHome( ).create();
    }

    protected PadAplicacionDelegate() throws DelegateException {       
    }                  
}

