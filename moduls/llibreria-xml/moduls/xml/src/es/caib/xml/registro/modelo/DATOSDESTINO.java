//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.07 at 08:58:49 AM CEST 
//


package es.caib.xml.registro.modelo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CODIGO_ENTIDAD_REGISTRAL_DESTINO"/>
 *         &lt;element ref="{}DECODIFICACION_ENTIDAD_REGISTRAL_DESTINO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codigoentidadregistraldestino",
    "decodificacionentidadregistraldestino"
})
@XmlRootElement(name = "DATOS_DESTINO")
public class DATOSDESTINO {

    @XmlElement(name = "CODIGO_ENTIDAD_REGISTRAL_DESTINO", required = true)
    protected String codigoentidadregistraldestino;
    @XmlElement(name = "DECODIFICACION_ENTIDAD_REGISTRAL_DESTINO")
    protected String decodificacionentidadregistraldestino;

    /**
     * Gets the value of the codigoentidadregistraldestino property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOENTIDADREGISTRALDESTINO() {
        return codigoentidadregistraldestino;
    }

    /**
     * Sets the value of the codigoentidadregistraldestino property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOENTIDADREGISTRALDESTINO(String value) {
        this.codigoentidadregistraldestino = value;
    }

    /**
     * Gets the value of the decodificacionentidadregistraldestino property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDECODIFICACIONENTIDADREGISTRALDESTINO() {
        return decodificacionentidadregistraldestino;
    }

    /**
     * Sets the value of the decodificacionentidadregistraldestino property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDECODIFICACIONENTIDADREGISTRALDESTINO(String value) {
        this.decodificacionentidadregistraldestino = value;
    }

}
