# Gutti - Sistema de Gestión

Sistema de gestión de pedidos, stock y ventas para el comercio Gutti, desarrollado como proyecto de la materia **Metodologías de Programación II** (UNAJ).

> ⚠️ **Estado del proyecto:** en etapa inicial. Este README refleja el alcance planificado; se irá actualizando a medida que avance el desarrollo.

## Descripción

El comercio Gutti anota sus pedidos de forma manual, lo que dificulta el control de lo pendiente de preparar y genera errores al calcular la recaudación diaria. El sistema está pensado para el empleado de caja o atención al mostrador de la tienda.

## Funcionalidades

- Gestión del catálogo de productos (precio, stock)
- Registro de ventas, diferenciando pedidos de mostrador y delivery
- Marcado de pedidos como entregados
- Historial de ventas ordenado, para visualizar la recaudación
- Login para clientes y administradores

**Alcance mínimo comprometido:** backend tipo API REST con panel de administración web, cinco clases de negocio con sus pruebas unitarias, persistencia en base de datos. Sin facturación electrónica, control de proveedores ni pasarelas de pago reales.

## Stack tecnológico

- **Backend:** Java, Spring Boot, Maven, JPA, Hibernate
- **Base de datos:** PostgreSQL
- **Configuración:** CORS configurado para conexión con el frontend
- **Testing:** frameworks de testing automatizado y mocks
- **Metodología:** Scrum, con cortes semanales atados al calendario de clases, seguimiento en Trello o Jira

## Estructura del repositorio

- `backend/` — API REST en Spring Boot
- `frontend/` — Panel de administración web
- `docs/` — Diagramas de secuencia y clases (UML)
- `README.md`

## Documentación

- [Diagrama de secuencia](docs/diagrama-secuencia.md)
- [Diagrama de clases (UML)](docs/diagrama-uml.md)

## Integrantes

- Guanes Franco
- Bianco Lorenzo
- Monte Alexis

**Materia:** Metodologías de Programación II
**Profesor:** Ing. Christian Nahuel Botta
**Universidad:** Universidad Nacional Arturo Jauretche (UNAJ)
