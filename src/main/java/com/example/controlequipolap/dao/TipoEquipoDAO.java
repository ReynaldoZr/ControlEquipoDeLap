package com.example.controlequipolap.dao;

import com.example.controlequipolap.database.JPAUtil;
import com.example.controlequipolap.model.TipoEquipo;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TipoEquipoDAO {
    public List<TipoEquipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoEquipo t ORDER BY t.nombre", TipoEquipo.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
