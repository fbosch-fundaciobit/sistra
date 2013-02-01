package org.ibit.rol.form.admin.action.paleta.componente;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ibit.rol.form.admin.action.BaseAction;
import org.ibit.rol.form.admin.form.ComponenteForm;
import org.ibit.rol.form.model.Componente;
import org.ibit.rol.form.persistence.delegate.ComponenteDelegate;
import org.ibit.rol.form.persistence.delegate.DelegateUtil;

/**
 * Action para editar un componente combobox.
 *
 * @struts.action
 *  name="comboboxForm"
 *  scope="session"
 *  validate="true"
 *  input=".combobox.editar"
 *  path="/admin/combobox/editar"
 *
 * @struts.action-forward
 *  name="reload" path=".combobox.editar"
 *
 * @struts.action-forward
 *  name="cancel" path=".paleta.editar"
 *
 * @struts.action-forward
 *  name="success" path=".paleta.editar"
 */
public class EditarComboBoxAction extends BaseAction {
    protected static Log log = LogFactory.getLog(EditarComboBoxAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        log.debug("Entramos en EditarComboBox");

        ComponenteDelegate componenteDelegate = DelegateUtil.getComponenteDelegate();
        ComponenteForm componenteForm = (ComponenteForm) form;
        Componente componente = (Componente) componenteForm.getValues();

        if (isCancelled(request)) {
            log.debug("isCancelled");
            Long idPaleta = componenteForm.getIdPaleta();
            guardarPaleta(mapping, request, idPaleta);
            return mapping.findForward("cancel");
        }

        // Elimina traducciones que no son validas
        componenteForm.validaTraduccion(mapping, request);

        if (isAlta(request) || isModificacio(request)) {
            log.debug("isAlta || isModificacio");

            Long idPaleta = componenteForm.getIdPaleta();
            componenteDelegate.gravarComponentePaleta(componente, idPaleta);

            log.debug("Creat/Actualitzat " + componente.getId());

            guardarPaleta(mapping, request, idPaleta);

            return mapping.findForward("success");
        }

        // Cambio de idioma
        componenteForm.reloadLang();
        request.setAttribute("idComponente", componente.getId());

        return mapping.findForward("reload");
    }
}
