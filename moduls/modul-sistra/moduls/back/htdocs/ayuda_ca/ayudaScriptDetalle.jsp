 <p>
  <b>Sintaxi</b><br/>
  Els scripts es codifiquen mitjan�ant sent�ncies de Javascript. Dins d'aquest codi
  es podran utilitzar una s�rie de <strong>Plugins</strong> que ens permetran
  consultar dominis, obtindre valors de camps de formularis, obtindre dades
  de l'usuari, etc.</p>
<p>
  Per a retornar un resultat <b>no s'ha d'utilitzar</b> la sent�ncia <b>return</b>, 
  sin� que cal deixar una &uacute;ltima l&iacute;nia amb la variable a retornar.<br>
  <em> Per exemple si volem retornar el resultat de la variable result, la 
  &uacute;ltima l&iacute;nia del nostre script haur� de ser:</em></p>

    <p><em>&lt;&lt; resta del script &gt;&gt;<br>
      </em><em>result; <br>
      </em></p>
      
<p><b>Plugins</b> </p>
<ul>
  <li><b>Gesti&oacute; d'errors<br/>
    </b>La gesti&oacute; d'errors es realitza mitjant�ant el plugin <strong>ERRORSCRIPT. 
    </strong>Mitjant�ant aquest plugin indicarem si s'ha produ�t un 
    error en el script. A mes podem establir establecir el missatge d'error de dues formes distintes:
    <ul>
      <li>mitjan&ccedil;ant un codi de missatge 
        definit en els missatges de validaci&oacute; del tr&agrave;mit.</li>
      <li>mitjan&ccedil;ant un texte din&agrave;mic directament des de el script</li>
    </ul>
    <blockquote>
      <p>(Si no establim 
        un missatge s'utilitzar&agrave; un gen&egrave;ric que indicar&agrave; que no s'ha passat la   validaci&oacute;.)<br />
            <br />
            <em>ERRORSCRIPT.setExisteError(true);</em><br />
            <em>ERRORSCRIPT.setMensajeError(&quot;ERR1&quot;);</em> // Establix missatge per codi d'error <br />
            <em>ERRORSCRIPT.setMensajeDinamicoError(&quot;Texto de error&quot;);</em> // Establix missatge amb text din&agrave;mic </p>
    </blockquote>
  </li>
</ul>
<ul>
  <li><b>Acceso a missatges<br/>
    </b>Podrem accedir a missatges de validaci� definits en el tramit a traves del plugin <strong>PLUGIN_MENSAJES. 
    </strong>
    <br>
    <em>PLUGIN_MENSAJES.getMensaje(&quot;ERR1&quot;);</em><br>
  </li>
</ul>
<ul>
  <li><b>Acc�s a dominis<br/>
    </b>Mitjant�ant <strong>PLUGIN_DOMINIOS</strong> podem recuperar 
    dominis (consultes) definides en la plataforma<br>
    <br>
    <em>id = PLUGIN_DOMINIOS.crearDominio('LOCALIDADES');<br>
    PLUGIN_DOMINIOS.establecerParametro(id,'03');<br>
    PLUGIN_DOMINIOS.recuperaDominio(id);<br>
    num=PLUGIN_DOMINIOS.getValoresDominio(id).getNumeroFilas();<br>
    for (i=1;i&le;num;i++){<br>
    nombre = PLUGIN_DOMINIOS.getValoresDominio(id).getValor(i,&quot;LOCALIDAD&quot;);<br>
    codigo = PLUGIN_DOMINIOS.getValoresDominio(id).getValor(i,&quot;CODIGO&quot;);<br>
    } <br>
    PLUGIN_DOMINIOS.removeDominio(id);</em> </li>
  <br/>
  <br/>
  <li><b>Acc�s a dades de formularis<br/>
    </b>Mitjant�ant <strong>PLUGIN_FORMULARIOS</strong> podem obtindre 
    dades dels formularis. Com par&agrave;metre cal passar l'identificador 
    del document, la inst�ncia (ser&agrave; 1) i el nom del camp. .El nom del camp es construir&agrave;  a partir del XPATH de la seg&uuml;ent forma:<br />
/FORMULARIO/DATOS_PERSONALES/NOMBRE = DATOS_PERSONALES.NOMBRE<br>
    <br>
    <em> valor = PLUGIN_FORMULARIOS.getDatoFormulario('FOR1',1,'campo1');</em> 
    <br>
    <br>Si l'element del formulari �s una <strong>llista desplegable</strong> es pot accedir al codi de l'element seleccionat de la seg�ent forma:
    <br>
    <br>
    <em>valor = PLUGIN_FORMULARIOS.getDatoFormulario('FOR1',1,'campo1[CODIGO]');</em>
    <br/>
    <br/>
    Si el camp del formulari �s <strong>multivaluat</strong> (llista desplegada, llista arbre, ...) es pot accedir al nombre de valors i a cada valor en particular de la seg�ent forma:
    <br>
    <br/>
    <em>numValores = PLUGIN_FORMULARIOS.getNumeroValoresCampo('FOR1',1,'campo1'); </em>
    <br/>
    for (i = 0; i < numValores; i++) {
    <br/>
    <em> valor = PLUGIN_FORMULARIOS.getDatoFormulario('FOR1',1,'campo1',i); </em>
    <br/>
    <em> codigo = PLUGIN_FORMULARIOS.getDatoFormulario('FOR1',1,'campo1[CODIGO]',i); </em>
	<br/>
	<em>} </em>
	<br/>
    <br>Per accedir al estat actual d'un formulari (p.e. per consultar si un formulari s'ha completat correctament):
    <br>
    <br>
    <em>// Valors estat: Correcte (S) - Incorrecte (N) - No rellenat (V) <br/>
    	valor = PLUGIN_FORMULARIOS.getEstadoFormulario('FOR1',1);</em>   </li>
  <br/>
  <br/>
  <li><b> Acc�s a dades de la sessi&oacute;<br/>
    </b>Mitjant�ant <strong>PLUGIN_DATOSSESION</strong> podem obtindre informaci&oacute; 
    relativa a l'usuari que ha iniciat sessi&oacute;.<br>
    <br>
    <em> codUsuario = PLUGIN_DATOSSESION.getCodigoUsuario();</em> <br>
    <em> nivel = PLUGIN_DATOSSESION.getNivelAutenticacion(); // Certificado (C) / Usuario (U) / An&oacute;nimo (A)</em> <br>
    <em> nif = PLUGIN_DATOSSESION.getNifUsuario();</em><br>
    <em> nombre = PLUGIN_DATOSSESION.getNombreUsuario();</em><br>
    <em> apellido1 = PLUGIN_DATOSSESION.getApellido1Usuario();</em><br>
    <em> apellido2 = PLUGIN_DATOSSESION.getApellido2Usuario();</em><br>
    <em> nombreCompleto = PLUGIN_DATOSSESION.getNombreCompletoUsuario();</em><br>
    <em> idioma = PLUGIN_DATOSSESION.getIdioma();</em>   <br> 
    <em> email = PLUGIN_DATOSSESION.getEmail();</em> <br>
	<em> movil = PLUGIN_DATOSSESION.getTelefonoMovil();</em> <br>
	<em> fijo = PLUGIN_DATOSSESION.getTelefonoFijo();</em> <br>
	<em> cp = PLUGIN_DATOSSESION.getCodigoPostal();</em> <br>
	<em> direccion = PLUGIN_DATOSSESION.getDireccion();</em> <br>
	<em> provincia = PLUGIN_DATOSSESION.getProvincia();</em> <br>	
	<em> municipio = PLUGIN_DATOSSESION.getMunicipio();</em><br>
	<em> habilitarAvisos = PLUGIN_DATOSSESION.getHabilitarAvisos();</em><br/>
	<em> perfilAcceso = PLUGIN_DATOSSESION.getPerfilAcceso(); // CIUDADANO / DELEGADO </em>	
	<em> nifRpte = PLUGIN_DATOSSESION.getRepresentanteNifCertificado(); // En caso de certificado, si hay representado </em></br>
	<em> nomRpte = PLUGIN_DATOSSESION.getNombreRepresentanteCertificado(); // En caso de certificado, si hay representado </em></br>
	<em> ape1Rpte = PLUGIN_DATOSSESION.getApellido1RepresentanteCertificado(); // En caso de certificado, si hay representado </em></br>
	<em> ape2Rpte = PLUGIN_DATOSSESION.getApellido2RepresentanteCertificado(); // En caso de certificado, si hay representado </em></br>
	<br/>
	<br/>
	En cas que el perfil d'acc&eacute;s sigui DELEGADO les dades anteriors es referiran a l'entitat que delega 
	i es podr&agrave; accedir a les dades del delegat que realitza el tr&agrave;mit mitjan�ant:
	<br/>
	<em> nifDelegado = PLUGIN_DATOSSESION.getNifDelegado();</em><br>
    <em> nombreDelegado = PLUGIN_DATOSSESION.getNombreDelegado();</em><br>
    <em> apellido1Delegado = PLUGIN_DATOSSESION.getApellido1Delegado();</em><br>
    <em> apellido2Delegado = PLUGIN_DATOSSESION.getApellido2Delegado();</em><br>
    <em> nombreCompletoDelegado = PLUGIN_DATOSSESION.getNombreCompletoDelegado();</em><br>
	 </li>
</ul>
<ul>
  <li><b> Acc&eacute;s a dades del tr&agrave;mit <br/>
    </b>Mitjant&ccedil;ant<strong>PLUGIN_TRAMITE</strong>  podem obtindre informaci&oacute; 
    relativa a la definici&oacute; del tr&agrave;mit.<br />
        <br />
        <em> fechaini = PLUGIN_TRAMITE.getPlazoInicio();</em> <em>//en format dd/MM/yyyy HH:mm:ss </em><br />
        <em> fechafin = PLUGIN_TRAMITE.getPlazoFin();</em> <em>//en format dd/MM/yyyy HH:mm:ss </em><br />
  </li>
</ul>
<ul>
  <li><b>Acc�s a par&agrave;metres inici<br/>
    </b>Mitjant�ant  <strong>PLUGIN_PARAMETROSINICIO</strong> podem accedir 
    a par&agrave;metres d'inici que s'indiquen en la url a l'iniciar el tr&agrave;mit. 
    <br/>
    <br/>
    Per exemple, la url para iniciar un tr&agrave;mit �s: /sistrafront/init.do?language=es&amp;modelo=TRAMITE&amp;version=1<br>
    hi ha casos en els quals interesa parametritzar un tr&agrave;mit mitjant�ant par&agrave;metres 
    d'inicio de forma que a la url d'inici li afegim par&agrave;metres: /sistrafront/init.do?language=es&amp;modelo=TRAMITE&amp;version=1&amp;par1=dd&amp;par2=ee 
    <br>
    Aquestos par&agrave;metres s�n accesibles mitjant�ant aquest plugin:<br>
    <em>par1 = PLUGIN_PARAMETROSINICIO.getParametro('par1');</em><br>
    <br/>   
    Per als tr&agrave;mits de subsanaci&oacute; hi ha dos par&agrave;metres impl&iacute;cits:
    <ul>
    	<li>subsanacionExpedienteId: indica id de l'expedient al que pertany el tr&agrave;mit </li>
    	<li>subsanacionExpedienteUA: indica unitat administrativa de l'expedient al que pertany el tr&agrave;mit</li>
    </ul>
    <br/>
    
    
  </li>
  <li><b> Debug de script<br/>
    </b>Els scripts es poden tracejar internament mitjant�ant el plugin <strong>PLUGIN_LOG</strong>. 
    <br>
    <br>
    <em>PLUGIN_LOG.debug(res);</em><br>
  </li>
</ul>
<ul>
  <li><b>Validacions de Forms<br/>
    </b>Esten disponibles les validacions que es poden realitzar en Forms: <strong>ValidacionesPlugin</strong> i <strong>DataPlugin</strong>
  </li>
</ul>
<br/>
<br/>
<br/>
<div style="TEXT-ALIGN: center;"></div><a href="javascript:window.history.back();" class="enlaceAyuda">Tornar</a></div>