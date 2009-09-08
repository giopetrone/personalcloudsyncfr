/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.exceptions.NonexistentEntityException;
import entity.exceptions.RollbackFailureException;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.transaction.UserTransaction;

/**
 *
 * @author giovanna
 */
public class AnimaJpaController {
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "ZooAppPU")
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Anima anima) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(anima);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Anima anima) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            anima = em.merge(anima);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = anima.getId();
                if (findAnima(id) == null) {
                    throw new NonexistentEntityException("The anima with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Anima anima;
            try {
                anima = em.getReference(Anima.class, id);
                anima.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The anima with id " + id + " no longer exists.", enfe);
            }
            em.remove(anima);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Anima> findAnimaEntities() {
        return findAnimaEntities(true, -1, -1);
    }

    public List<Anima> findAnimaEntities(int maxResults, int firstResult) {
        return findAnimaEntities(false, maxResults, firstResult);
    }

    private List<Anima> findAnimaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Anima as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Anima findAnima(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Anima.class, id);
        } finally {
            em.close();
        }
    }

    public int getAnimaCount() {
        EntityManager em = getEntityManager();
        try {
            return ((Long) em.createQuery("select count(o) from Anima as o").getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
