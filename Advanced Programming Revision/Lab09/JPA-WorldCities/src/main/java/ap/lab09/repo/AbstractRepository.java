package ap.lab09.repo;

import ap.lab09.db.JPAUtil;
import jakarta.persistence.*;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractRepository<T, ID> {
    private static final Logger LOG = Logger.getLogger("JPA");
    static {
        try {
            var fh = new FileHandler("jpa.log", true);
            LOG.addHandler(fh);
            LOG.setUseParentHandlers(true); // console + file
            LOG.setLevel(Level.INFO);
        } catch (Exception ignored) {}
    }

    private final Class<T> type;
    protected AbstractRepository(Class<T> type){ this.type = type; }

    public void create(T entity){
        long t0 = System.nanoTime();
        EntityManager em = JPAUtil.emf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            LOG.info("create " + type.getSimpleName() + " ok in " + msSince(t0) + " ms");
        } catch (RuntimeException e){
            if (tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "create failed", e);
            throw e;
        } finally { em.close(); }
    }

    public T findById(ID id){
        long t0 = System.nanoTime();
        EntityManager em = JPAUtil.emf().createEntityManager();
        try {
            T x = em.find(type, id);
            LOG.info("findById " + type.getSimpleName() + " in " + msSince(t0) + " ms");
            return x;
        } finally { em.close(); }
    }

    /** pattern may be "Ro%" or plain "Ro" (auto-wrapped with %). */
    public List<T> findByName(String pattern){
        long t0 = System.nanoTime();
        String pq = type.getSimpleName() + ".findByNameLike";
        EntityManager em = JPAUtil.emf().createEntityManager();
        try {
            if (!pattern.contains("%")) pattern = "%" + pattern + "%";
            List<T> list = em.createNamedQuery(pq, type)
                    .setParameter("name", pattern)
                    .getResultList();
            LOG.info("findByName " + type.getSimpleName() + " in " + msSince(t0) + " ms (n="+list.size()+")");
            return list;
        } finally { em.close(); }
    }

    private static String msSince(long t0){ return String.format("%.3f", (System.nanoTime()-t0)/1e6); }
}
