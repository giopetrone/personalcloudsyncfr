/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author giovanna
 */
public class AnimaConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        Long id = new Long(string);
        AnimaJpaController controller = (AnimaJpaController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "animaJpa");
        return controller.findAnima(id);
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Anima) {
            Anima o = (Anima) object;
            return o.getId() == null ? "" : o.getId().toString();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: entity.Anima");
        }
    }

}
