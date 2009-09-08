/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.util.PagingInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.faces.FacesException;
import entity.util.JsfUtil;
import entity.exceptions.NonexistentEntityException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

/**
 *
 * @author giovanna
 */
public class AnimaController {

    public AnimaController() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        jpaController = (AnimaJpaController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "animaJpa");
        pagingInfo = new PagingInfo();
        converter = new AnimaConverter();
    }
    private Anima anima = null;
    private List<Anima> animaItems = null;
    private AnimaJpaController jpaController = null;
    private AnimaConverter converter = null;
    private PagingInfo pagingInfo = null;

    public PagingInfo getPagingInfo() {
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(jpaController.getAnimaCount());
        }
        return pagingInfo;
    }

    public SelectItem[] getAnimaItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(jpaController.findAnimaEntities(), false);
    }

    public SelectItem[] getAnimaItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(jpaController.findAnimaEntities(), true);
    }

    public Anima getAnima() {
        if (anima == null) {
            anima = (Anima) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentAnima", converter, null);
        }
        if (anima == null) {
            anima = new Anima();
        }
        return anima;
    }

    public String listSetup() {
        reset(true);
        return "anima_list";
    }

    public String createSetup() {
        reset(false);
        anima = new Anima();
        return "anima_create";
    }

    public String create() {
        try {
            jpaController.create(anima);
            JsfUtil.addSuccessMessage("Anima was successfully created.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("anima_detail");
    }

    public String editSetup() {
        return scalarSetup("anima_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        anima = (Anima) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentAnima", converter, null);
        if (anima == null) {
            String requestAnimaString = JsfUtil.getRequestParameter("jsfcrud.currentAnima");
            JsfUtil.addErrorMessage("The anima with id " + requestAnimaString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String animaString = converter.getAsString(FacesContext.getCurrentInstance(), null, anima);
        String currentAnimaString = JsfUtil.getRequestParameter("jsfcrud.currentAnima");
        if (animaString == null || animaString.length() == 0 || !animaString.equals(currentAnimaString)) {
            String outcome = editSetup();
            if ("anima_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit anima. Try again.");
            }
            return outcome;
        }
        try {
            jpaController.edit(anima);
            JsfUtil.addSuccessMessage("Anima was successfully updated.");
        } catch (NonexistentEntityException ne) {
            JsfUtil.addErrorMessage(ne.getLocalizedMessage());
            return listSetup();
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return detailSetup();
    }

    public String destroy() {
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentAnima");
        Long id = new Long(idAsString);
        try {
            jpaController.destroy(id);
            JsfUtil.addSuccessMessage("Anima was successfully deleted.");
        } catch (NonexistentEntityException ne) {
            JsfUtil.addErrorMessage(ne.getLocalizedMessage());
            return relatedOrListOutcome();
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return relatedOrListOutcome();
    }

    private String relatedOrListOutcome() {
        String relatedControllerOutcome = relatedControllerOutcome();
        if (relatedControllerOutcome != null) {
            return relatedControllerOutcome;
        }
        return listSetup();
    }

    public List<Anima> getAnimaItems() {
        if (animaItems == null) {
            getPagingInfo();
            animaItems = jpaController.findAnimaEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }
        return animaItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "anima_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "anima_list";
    }

    private String relatedControllerOutcome() {
        String relatedControllerString = JsfUtil.getRequestParameter("jsfcrud.relatedController");
        String relatedControllerTypeString = JsfUtil.getRequestParameter("jsfcrud.relatedControllerType");
        if (relatedControllerString != null && relatedControllerTypeString != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            Object relatedController = context.getApplication().getELResolver().getValue(context.getELContext(), null, relatedControllerString);
            try {
                Class<?> relatedControllerType = Class.forName(relatedControllerTypeString);
                Method detailSetupMethod = relatedControllerType.getMethod("detailSetup");
                return (String) detailSetupMethod.invoke(relatedController);
            } catch (ClassNotFoundException e) {
                throw new FacesException(e);
            } catch (NoSuchMethodException e) {
                throw new FacesException(e);
            } catch (IllegalAccessException e) {
                throw new FacesException(e);
            } catch (InvocationTargetException e) {
                throw new FacesException(e);
            }
        }
        return null;
    }

    private void reset(boolean resetFirstItem) {
        anima = null;
        animaItems = null;
        pagingInfo.setItemCount(-1);
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Anima newAnima = new Anima();
        String newAnimaString = converter.getAsString(FacesContext.getCurrentInstance(), null, newAnima);
        String animaString = converter.getAsString(FacesContext.getCurrentInstance(), null, anima);
        if (!newAnimaString.equals(animaString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }

}
