//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-661 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.07 at 08:58:37 AM CEST 
//


package es.caib.xml.justificantepago.modelo;

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
 *         &lt;element ref="{}LOCALIZADOR"/>
 *         &lt;element ref="{}DUI"/>
 *         &lt;element ref="{}FECHA_PAGO"/>
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
    "localizador",
    "dui",
    "fechapago"
})
@XmlRootElement(name = "DATOS_PAGO")
public class DATOSPAGO {

    @XmlElement(name = "LOCALIZADOR", required = true)
    protected String localizador;
    @XmlElement(name = "DUI", required = true)
    protected String dui;
    @XmlElement(name = "FECHA_PAGO", required = true)
    protected String fechapago;

    /**
     * Gets the value of the localizador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOCALIZADOR() {
        return localizador;
    }

    /**
     * Sets the value of the localizador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOCALIZADOR(String value) {
        this.localizador = value;
    }

    /**
     * Gets the value of the dui property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDUI() {
        return dui;
    }

    /**
     * Sets the value of the dui property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDUI(String value) {
        this.dui = value;
    }

    /**
     * Gets the value of the fechapago property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHAPAGO() {
        return fechapago;
    }

    /**
     * Sets the value of the fechapago property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHAPAGO(String value) {
        this.fechapago = value;
    }

}
