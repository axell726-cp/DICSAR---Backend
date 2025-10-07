# 📦 DICSAR Backend — Gestión de Inventario, Proveedores y Clientes

Este repositorio contiene el backend del sistema de gestión de inventario, precios y stock de **DICSAR S.A.C.**, desarrollado en **Spring Boot** con arquitectura **MVC en capas**.

---

## 🚀 Objetivo del Proyecto
Construir un sistema modular para la administración de inventarios, precios, proveedores, clientes y seguridad de usuarios.  

- **Sprint 1:** Inventario y precios (productos, categorías, stock, vencimientos y notificaciones).  
- **Sprint 2:** Proveedores y adquisiciones (módulo de compras, historial y movimientos de inventario).  
- **Sprint 3:** Clientes y seguridad (módulo de clientes, usuarios, roles y control de acceso).  

---

## 🌱 Arquitectura y Tecnologías
- **Backend:** Spring Boot 3 + JPA/Hibernate + Maven  
- **Base de datos:** MySQL  
- **IDE recomendado:** Spring Tools Suite / IntelliJ 
- **Conexión DB:** `application.properties` ya definido para evitar conflictos  
- **Control de versiones:** Git + GitHub  
- **Metodología:** Scrum (con historias de usuario, sprints y tareas definidas en Jira)  

---

## 🌳 Flujo de ramas (Git Flow Adaptado)

Para mantener orden, seguimos este esquema de ramas:

- **`master`** → Rama oficial, estable y validada al final de cada sprint.  
- **`develop-sprintX`** → Rama de integración de cada sprint (ej: `develop-sprint1`).  
- **`feature/HU#-descripcion`** → Ramas de desarrollo por Historia de Usuario (ej: `feature/HU3-modificar-estados`).  

> 💡 Nota: **No trabajar en `master` directamente.** Cada dev trabaja en su rama `feature` y luego hace merge a `develop-sprintX` cuando esté probado.

---

## 🛠️ Guía de uso (Comandos principales)

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/axell726-cp/DICSAR-Backend.git
   cd DICSAR-Backend
2. **Listar ramas disponibles**
   ```bash
   git branch -a
3. **Cambiar a la HU que se va a trabajar**
   ```bash
   git checkout feature/HU3-modificar-estados
   git pull origin feature/HU3-modificar-estados
4. **Subir tus cambios**
   ```bash
   git add .
   git commit -m "HU3: implementar endpoint para modificar estado de producto" (:ejemplo)
   git push origin feature/HU3-modificar-estados
5. **verificar remoto (cambios realizados)**
   ```bash
   git remote -v
