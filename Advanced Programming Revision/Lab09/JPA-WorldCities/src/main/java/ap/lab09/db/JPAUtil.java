package ap.lab09.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("worldPU");
    private JPAUtil(){}
    public static EntityManagerFactory emf(){ return EMF; }
}
