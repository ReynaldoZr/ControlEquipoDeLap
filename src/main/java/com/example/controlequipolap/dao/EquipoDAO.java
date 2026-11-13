package com.example.controlequipolap.dao;

import com.example.controlequipolap.database.JPAUtil;
import com.example.controlequipolap.model.Equipo;
import com.example.controlequipolap.model.TipoEquipo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EquipoDAO {
    private TipoEquipo obtenerTipo(EntityManager em, Equipo equipo) {
        if (equipo.getTipoEquipo() == null || equipo.getTipoEquipo().getId() == null) {
            throw new IllegalArgumentException("Selecciona un tipo existente.");
        }
        TipoEquipo tipo = em.find(TipoEquipo.class, equipo.getTipoEquipo().getId());
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo ya no existe.");
        }
        return tipo;
    }

    public void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            equipo.setTipoEquipo(obtenerTipo(em, equipo));
            em.persist(equipo);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void actualizar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo guardado = em.find(Equipo.class, equipo.getId());
            if (guardado == null) {
                throw new IllegalArgumentException("El equipo ya no existe.");
            }
            // guardado esta managed: Hibernate detecta estos cambios.
            guardado.setCodigo(equipo.getCodigo());
            guardado.setNombre(equipo.getNombre());
            guardado.setMarca(equipo.getMarca());
            guardado.setEstado(equipo.getEstado());
            guardado.setCantidad(equipo.getCantidad());
            guardado.setTipoEquipo(obtenerTipo(em, equipo));
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipo = em.find(Equipo.class, id);
            if (equipo == null) {
                throw new IllegalArgumentException("El equipo ya no existe.");
            }
            em.remove(equipo);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Equipo> listar() {
        return buscar("", null);
    }

    public List<Equipo> buscar(String nombre, TipoEquipo tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Los valores se pasan como parametros, no se concatenan.
            String jpql = "SELECT e FROM Equipo e "
                    + "WHERE LOWER(e.nombre) LIKE :nombre";
            if (tipo != null) {
                jpql += " AND e.tipoEquipo.id = :idTipo";
            }
            jpql += " ORDER BY e.id";
            TypedQuery<Equipo> consulta = em.createQuery(jpql, Equipo.class);
            consulta.setParameter("nombre", "%" + nombre.trim().toLowerCase(java.util.Locale.ROOT) + "%");
            if (tipo != null) {
                consulta.setParameter("idTipo", tipo.getId());
            }
            return consulta.getResultList();
        } finally {
            em.close();
        }
    }
}
