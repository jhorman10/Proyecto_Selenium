# Contexto de Negocio - CyberGuard System

## 1. Descripción del Proyecto

- **Nombre del Proyecto:** CyberGuard System
- **Objetivo del Proyecto:** Desarrollar una plataforma de alertas de ciberseguridad en tiempo real que permita detectar, persistir y notificar amenazas, ofreciendo una API segura y un flujo de eventos confiable para consumidores y clientes.

## 2. Flujos Críticos del Negocio

- **Principales Flujos de Trabajo:**
  - Registro y autenticación de usuarios a través de Firebase + JWT.
  - Publicación de eventos de amenazas por parte de productores hacia RabbitMQ.
  - Consumo de eventos por parte de un worker que persiste historial (Redis) y notifica clientes vía WebSocket.
  - Acceso a endpoints CRUD de amenazas (/api/threats) y estadísticas (/api/statistics).
  - Gestión de roles y usuarios por parte de administradores.

- **Módulos o Funcionalidades Críticas:**
  - Gestión de amenazas (creación, listado y eliminación).
  - Publicación y consumo de eventos (RabbitMQ producer/worker).
  - Autenticación y autorización (tokens JWT y roles).
  - Historial en tiempo real y reproducción (Redis + WebSocket).
  - Auditoría y logging estructurado.

## 3. Reglas de Negocio y Restricciones

- **Reglas de Negocio Relevantes:**
  - Solo administradores pueden modificar roles de usuarios.
  - Los eventos de amenazas deben confirmarse en RabbitMQ (ConfirmChannel) y reenviarse a DLX en caso de fallo.
  - Validación estricta de datos de entrada con Joi en todos los endpoints.
  - Las amenazas persisten en PostgreSQL y deben cumplir restricciones ACID.

- **Regulaciones o Normativas:**
  - Debe cumplirse con leyes de protección de datos (p.ej. GDPR) en el manejo de información de usuarios.
  - Uso de estándares de seguridad para JWT y almacenamiento de contraseñas.

## 4. Perfiles de Usuario y Roles

- **Perfiles o Roles de Usuario en el Sistema:**
  - **Administrador:** configura el sistema, gestiona usuarios y roles.
  - **Usuario/Colaborador:** consume la API para reportar amenazas y consulta datos.
  - **Servicio/Producer:** componentes automáticos que publican eventos (no interactúan manualmente).

- **Permisos y Limitaciones de Cada Perfil:**
  - **Administrador:** Accede a endpoints de administración (`/api/admin/users` y `/api/admin/*`).
  - Crea, actualiza y elimina usuarios del sistema (activación, desactivación).
  - Modifica roles y permisos de cualquier usuario (p.ej. asignar o revocar roles de administrador/colaborador).
  - Consulta registros de auditoría y logs de actividad para supervisar acciones de usuarios.
  - Visualiza y exporta estadísticas globales del sistema (número de amenazas, uso del API, eventos procesados).
  - Gestiona configuraciones generales (parámetros de RabbitMQ, límites de la cola, políticas de DLX).
  - **Usuario/Colaborador:** puede autenticarse, reportar amenazas y consultar/listar amenazas propias.
  - **Producer:** publica eventos a RabbitMQ sin acceso directo a la API de administración.

## 5. Condiciones del Entorno Técnico

- **Plataformas Soportadas:**
  - Aplicación backend orientada a servicios; accesible desde web y desde servicios REST y WebSocket.
  - Clientes frontend (Angular) se conectan via HTTP y WebSocket.

- **Tecnologías o Integraciones Clave:**
  - PostgreSQL para persistencia de datos (usuarios, amenazas, auditoría).
  - RabbitMQ como bus de mensajes para eventos de seguridad.
  - Redis para caché y historial de eventos en tiempo real.
  - Autenticación con Firebase y tokens JWT.
  - Integración con servicios de logging (Winston) y validación con Joi.

## 6. Casos Especiales o Excepciones (Opcional)

- **Escenarios Alternos o Excepciones que Deben Considerarse:**
  - Si un productor publica más eventos de los que el worker puede procesar, deben encolarse y, en caso de fallo repetido, enviarse a una DLX.
  - Usuarios que exceden límites de uso (p.ej. intentos de fuerza bruta) deben ser bloqueados temporalmente.
  - Clientes que quedan desconectados del WebSocket deben poder reconectarse y solicitar replay del historial desde Redis.
