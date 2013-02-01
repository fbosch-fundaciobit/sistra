package es.caib.zonaper.modelInterfaz;

import java.io.Serializable;
import java.util.List;

/**
 * Filtro para buscar expedientes.
 * 
 */
public class FiltroBusquedaExpedientePAD implements Serializable {
	
	/**
	 * Lista de ids procedimiento.
	 */
	private List identificadorProcedimientos;
	/**
	 * Nif representante.
	 */
	private String nifRepresentante;	
	/**
	 * A�o.
	 */
	private int anyo;
	/**
	 * Mes (1 - 12).
	 */
	private int mes;
	/**
	 * Numero entrada que crea el expediente.
	 */
	private String numeroEntradaBTE;
	
	public List getIdentificadorProcedimientos() {
		return identificadorProcedimientos;
	}
	public void setIdentificadorProcedimientos(List identificadorProcedimiento) {
		this.identificadorProcedimientos = identificadorProcedimiento;
	}
	public String getNifRepresentante() {
		return nifRepresentante;
	}
	public void setNifRepresentante(String nifRepresentante) {
		this.nifRepresentante = nifRepresentante;
	}
	public int getAnyo() {
		return anyo;
	}
	public void setAnyo(int anyo) {
		this.anyo = anyo;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public String getNumeroEntradaBTE() {
		return numeroEntradaBTE;
	}
	public void setNumeroEntradaBTE(String numeroEntrada) {
		this.numeroEntradaBTE = numeroEntrada;
	}
}
