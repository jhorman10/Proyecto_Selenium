# Refinamiento de Historias de Usuario - CyberGuard System

## Introducción

Este documento presenta el análisis comparativo de 3 historias de usuario del proyecto CyberGuard System, mostrando la versión original, la versión refinada mediante la instrucción de SKAI "Diagnosticar historias de usuario" y las diferencias detectadas aplicando los principios INVEST.

---

## HU-001: Autenticación de Usuario (Login)

### Versión Original

**Historia de Usuario:**
```
Como: Usuario
Quiero: Autenticarme en la plataforma
Para: Poder acceder a los recursos del sistema
```

**Criterios de Aceptación:**
- El usuario debe ingresar email y contraseña válidos
- El sistema debe validar las credencias contra Firebase
- Si las credenciales son correctas, se genera un token JWT
- El token debe incluir información del usuario (id, email, rol)
- Si las credenciales son incorrectas, se muestra mensaje de error
- El sistema debe registrar intentos fallidos de autenticación

---

### Versión Refinada (Generada por SKAI)

**Título: Autenticación segura de usuarios**

```
Descripción: Como usuario de CyberGuard System, quiero autenticarme en la plataforma mediante email y contraseña, para acceder de manera segura a los recursos del sistema y garantizar el cumplimiento de los estándares de seguridad y privacidad exigidos por la regulación GDPR.
```
**Criterios de Aceptación:**
1. El usuario debe ingresar un email válido (formato estándar RFC 5322) y una contraseña que cumpla con los requisitos de seguridad definidos (mínimo 8 caracteres, incluir al menos una letra mayúscula, una minúscula, un número y un carácter especial).

2. El sistema debe validar las credenciales contra el servicio de autenticación de Firebase.

3. Si las credenciales son correctas:

    - Se genera un token JWT único.
    - El token debe incluir información del usuario: id, email, rol, permisos, fecha de expiración y claim de cumplimiento GDPR.
    - El usuario debe poder acceder a los recursos permitidos según su rol.

4. Si las credenciales son incorrectas:

    - Se muestra un mensaje genérico de error: “Credenciales inválidas. Por favor, verifica tu email y contraseña.”
    - El intento fallido se registra en el sistema de auditoría.

5. Si se detectan más de 5 intentos fallidos consecutivos para un mismo usuario en menos de 15 minutos, el sistema debe bloquear temporalmente el acceso a ese usuario por 30 minutos y notificarle mediante email.

6. El sistema debe cumplir con los requisitos de protección de datos personales según GDPR, asegurando que las credenciales y tokens no sean almacenados en texto plano y que la información sensible esté cifrada.

7. El sistema debe manejar errores de conexión con Firebase mostrando un mensaje de “Servicio temporalmente no disponible. Intenta más tarde.”

8. La autenticación debe ser testeable mediante pruebas unitarias y de integración, cubriendo los escenarios de éxito, error y bloqueo temporal.

### Notas adicionales:

- La historia de usuario es independiente del flujo de registro y recuperación de contraseña.

- Los mensajes de error no deben revelar si el email o la contraseña son incorrectos para evitar exposición indebida.


---

### Cuadro de diferencias detectadas

| Aspecto | Versión Original | Versión Refinada | Principio INVEST Aplicado |
|---------|------------------|------------------|---------------------------|
| **Independencia (I)** | Cumple parcialmente. Aunque la autenticación es independiente, está ligada a Firebase y JWT, lo que podría generar dependencias técnicas. | Cumple. Se define claramente la dependencia de Firebase como servicio externo y se agrega manejo de errores de conexión (criterio 7), lo que permite implementar con un mock si Firebase no está listo. | La versión refinada aísla la dependencia técnica al agregar un escenario de fallo de Firebase, permitiendo desarrollo independiente con mocks y pruebas sin bloqueo. |
| **Negociable (N)** | Cumple. La historia puede ser refinada o ajustada según las necesidades del negocio. | Cumple. Se mantiene negociable al separar requisitos de seguridad (GDPR, bloqueo) como criterios explícitos que pueden priorizarse por sprint. Los detalles técnicos (Joi, bcrypt) son implementación negociable. | La refinación convierte requisitos implícitos en criterios explícitos, facilitando la negociación entre producto y desarrollo sobre qué incluir en cada iteración. |
| **Valiosa (V)** | Cumple. La autenticación es un componente crítico para el acceso seguro a los recursos del sistema. | Cumple. Agrega valor adicional al incluir cumplimiento GDPR, bloqueo anti-brute force y notificación por email, incrementando la propuesta de valor de seguridad del sistema. | La versión refinada no solo autentica, sino que protege activamente al usuario contra ataques y cumple regulaciones, elevando el valor de negocio. |
| **Estimable (E)** | Cumple parcialmente. Aunque la funcionalidad es clara, la falta de detalles específicos (como validaciones y manejo de errores) podría dificultar la estimación precisa. | Cumple. Se especifican 8 criterios concretos con reglas verificables (5 intentos = bloqueo 30 min, formato RFC 5322, complejidad de contraseña), lo que permite estimar con precisión cada flujo. | La refinación transforma criterios vagos en reglas numéricas y formatos específicos, permitiendo al equipo estimar esfuerzo por criterio con alta confianza. |
| **Pequeña (S)** | Cumple. La historia tiene un alcance limitado y puede desarrollarse en un sprint. | Cumple. Aunque se agregaron más criterios, todos pertenecen al flujo de autenticación y pueden implementarse en un sprint. Los criterios 5-8 podrían ser sub-tareas si el equipo lo prefiere. | El alcance sigue siendo un solo flujo (login). Los criterios adicionales son extensiones naturales del mismo flujo, no nuevas funcionalidades. |
| **Testeable (T)** | Cumple parcialmente. Los criterios de aceptación son verificables pero necesitan más detalles para garantizar pruebas exhaustivas. | Cumple. Cada criterio tiene condiciones verificables: formato RFC 5322, regex de complejidad, umbral de 5 intentos/15 min, bloqueo de 30 min, estructura JWT con claims específicos, y mensajes de error exactos. | La refinación eliminó ambigüedad al definir umbrales, formatos y respuestas esperadas, haciendo cada criterio directamente traducible a un caso de prueba automatizable. |

**Principales Mejoras Identificadas:**
1. Especificar si el token JWT debe incluir información adicional sobre los roles y permisos del usuario para garantizar la autorización adecuada.

---

## HU-002: Middleware de Autenticación

### Versión Original

**Historia de Usuario:**
```
Como: Sistema
Quiero: Validar tokens JWT en rutas protegidas
Para: Asegurar que solo usuarios autenticados accedan
```

**Criterios de Aceptación:**
- El middleware debe interceptar todas las peticiones a rutas protegidas
- Debe extraer el token JWT del header Authorization
- Debe validar la firma y expiración del token
- Si el token es válido, permite el acceso y adjunta datos del usuario al request
- Si el token es inválido o expiró, retorna error 401 Unauthorized
- Debe validar roles específicos según el endpoint (admin, colaborador)

---

### Versión Refinada (Generada por SKAI)

**Titulo: Validación de Tokens JWT y Roles en Rutas Protegidas**

```
Como middleware del backend del CyberGuard System, quiero validar los tokens JWT y los roles de usuario en todas las rutas protegidas de la API, para asegurar que únicamente usuarios autenticados y autorizados accedan a recursos sensibles, conforme a los permisos definidos para cada endpoint. Además, quiero adjuntar al request los datos relevantes del usuario para facilitar la trazabilidad y auditoría de las acciones.
```

### Criterios de Aceptación:

1. Interceptar todas las peticiones a rutas protegidas:
- El middleware debe ejecutarse en todas las rutas que requieren autenticación y autorización, excluyendo endpoints públicos (ej. login, registro).

2. Validar token JWT:

    - Extraer el token JWT del header Authorization en formato “Bearer”.
    - Verificar la firma, expiración y validez del token utilizando la configuración de Firebase.

3. Validar roles del usuario:

    - Determinar el rol del usuario (administrador, colaborador, producer) según la información del JWT.
    - Permitir acceso solo si el rol cumple con los permisos definidos para el endpoint.
    - Los endpoints de administración solo pueden ser accedidos por usuarios con rol “administrador”.

4. Adjuntar información del usuario al request:

    - Incluir en el objeto request los datos relevantes del usuario autenticado (ID, correo electrónico, rol y estado de cuenta).

5. Manejo de errores:

    - Si el token es inválido, expiró o el usuario está bloqueado, retornar un error 401 Unauthorized con un mensaje descriptivo.
    - Registrar el intento fallido en el sistema de auditoría/logging estructurado.

6. Escenarios especiales:
    - Si el usuario ha excedido límites de uso o ha sido bloqueado, denegar el acceso y retornar error 401.

7. Pruebas y validaciones:
    - Se debe poder probar con diferentes escenarios: token válido, token expirado, token inválido, usuario sin permisos, usuario bloqueado.

---

### Notas Técnicas

- El middleware debe integrarse con el sistema de auditoría (Winston) para registrar accesos y rechazos.
- Los roles y permisos por endpoint deben estar definidos en una matriz de permisos configurable.
- La validación debe cumplir con los estándares de seguridad para JWT y protección de datos personales (GDPR).

### Cuadro de diferencias detectadas

| Aspecto | Versión Original | Versión Refinada | Principio INVEST Aplicado |
|---------|------------------|------------------|---------------------------|
| **Independencia (I)** | Cumple parcialmente. La historia depende de una implementación previa de autenticación (Firebase + JWT). | Cumple parcialmente. Se explicita la dependencia de Firebase/JWT y se define el contrato de entrada (token Bearer en header). Esto permite implementar con un mock de JWT si HU-001 no está lista. | Aunque sigue dependiendo del token JWT, la refinación define el contrato explícitamente, permitiendo desarrollo paralelo con tokens de prueba firmados localmente. |
| **Negociable (N)** | Cumple. | Cumple. Se definen 7 criterios concretos pero negociables: la matriz de permisos por endpoint es configurable, los datos adjuntados al request pueden ajustarse, y los escenarios especiales (bloqueo, límites) se pueden priorizar. | La refinación separa la lógica core (validar token + adjuntar datos) de los escenarios avanzados (bloqueo, límites), facilitando negociar qué entra en cada sprint. |
| **Valiosa (V)** | Cumple. | Cumple. Agrega valor al definir la matriz de roles por endpoint (administrador, colaborador, producer), la auditoría de accesos/rechazos y el manejo de usuarios bloqueados, elevando la seguridad del sistema. | El middleware es la puerta de entrada a todo el sistema. La refinación garantiza que cada acceso sea trazable y autorizado, valor directo para seguridad y cumplimiento. |
| **Estimable (E)** | Cumple parcialmente. Sin detalles de rutas, roles o errores, la estimación puede ser inexacta. | Cumple. Se definen 3 roles concretos, error 401 como respuesta estándar, datos específicos a adjuntar (ID, email, rol, estado), y escenarios de error enumerados. Esto permite estimar cada bloque por separado. | La refinación convierte "validar roles según endpoint" en una lista concreta de roles y comportamientos, eliminando incertidumbre en la estimación. |
| **Pequeña (S)** | Cumple parcialmente. Podría dividirse en sub-historias. | Cumple. El middleware es una sola pieza de infraestructura transversal. Dividirlo fragmentaría la lógica de seguridad. Los 7 criterios se implementan en un único middleware con ~200 líneas de código. | Aunque tiene múltiples criterios, todos operan en el mismo punto de intercepción (middleware). Dividirlo crearía dependencias circulares entre sub-historias. |
| **Testeable (T)** | Cumple. Los criterios permiten pruebas pero necesitan más detalle. | Cumple. Se enumeran escenarios concretos para prueba: token válido, expirado, inválido, sin permisos, usuario bloqueado. Cada escenario tiene una respuesta esperada (401 + mensaje descriptivo + log de auditoría). | La refinación define el resultado esperado para cada escenario, haciendo cada criterio directamente automatizable con pruebas unitarias y de integración. |

**Principales Mejoras Identificadas:**
1. Incluir criterios de aceptación para el registro de intentos fallidos y su integración con la auditoría.
2. Especificar el manejo de usuarios bloqueados o revocación de tokens.

---

## HU-003: Gestión de Amenazas Unificada

### Versión Original

**Historia de Usuario:**
```
Como: Sistema de Seguridad CyberGuard
Quiero: Reportar incidentes de seguridad de forma manual (vía analista) o automática (vía sistema)
Para: Centralizar la respuesta a amenazas en RabbitMQ y permitir el procesamiento asíncrono
```

**Criterios de Aceptación:**
- El sistema debe aceptar reportes manuales desde el endpoint POST /api/threats
- El sistema debe aceptar reportes automáticos desde servicios productores
- Todos los reportes deben validarse con Joi (tipo, severidad, descripción)
- Los eventos deben publicarse en RabbitMQ con ConfirmChannel
- Si la publicación falla, el evento debe enviarse a DLX
- Los datos de la amenaza deben persistirse en PostgreSQL
- El worker debe consumir eventos y notificar vía WebSocket
- El historial debe almacenarse en Redis para replay

---

### Versión Refinada (Generada por SKAI)

**Título: Gestión Centralizada de Reporte de Amenazas**

```
Descripción: Como analista de seguridad o servicio productor, quiero reportar incidentes de seguridad de forma manual (mediante la API) o automática (mediante servicios productores), para centralizar la respuesta a amenazas en RabbitMQ, permitir el procesamiento asíncrono, garantizar la persistencia y notificación en tiempo real, y facilitar la trazabilidad y replay de eventos.
```
**Criterios de Aceptación:**
1. Reporte Manual:

    - El sistema debe aceptar reportes manuales de amenazas mediante un endpoint autenticado POST /api/threats, con campos requeridos: tipo, severidad, descripción, fecha y fuente del incidente.
    - Solo usuarios autenticados con rol de "usuario/colaborador" pueden acceder al endpoint.

2. Reporte Automático:

    - El sistema debe aceptar reportes automáticos desde servicios productores autenticados, a través de RabbitMQ, con los mismos campos requeridos.

3. Validación de Datos:

    - Todos los reportes, manuales o automáticos, deben validarse con Joi siguiendo los siguientes criterios:
        - Tipo de amenaza debe pertenecer al catálogo predefinido.
        - Severidad debe ser uno de los valores permitidos (bajo, medio, alto, crítico).
        - Descripción obligatoria, con mínimo 20 caracteres.
        - Fecha debe ser válida y no futura.
        - Fuente debe identificarse correctamente.

4. Publicación de Eventos:

    - Los eventos de amenazas deben publicarse en RabbitMQ usando ConfirmChannel.
    - Si la publicación falla tras 3 intentos, el evento debe enviarse automáticamente a la Dead Letter Exchange (DLX).

5. Persistencia:

    - Los datos completos de la amenaza deben persistirse en PostgreSQL, garantizando las restricciones ACID y anonimización de datos personales conforme a GDPR.

6. Notificación y Replay:

    - Un worker debe consumir los eventos, notificar a los clientes conectados vía WebSocket y almacenar el historial de eventos en Redis.
    - Los usuarios deben poder solicitar el replay del historial de amenazas desde Redis, filtrando por usuario, rango de fechas o tipo de amenaza.

7. Auditoría y Logging:

    - Todas las acciones de reporte, validación, publicación y persistencia deben registrarse en el sistema de auditoría y logging estructurado.

8. Escenarios de Error:

    - En caso de error de validación o persistencia, el sistema debe retornar un mensaje descriptivo y registrar el incidente en los logs de auditoría.

### Notas Técnicas

- La API debe estar protegida mediante autenticación JWT.
- La gestión de roles y permisos debe seguir las políticas definidas en el contexto de negocio.
- El sistema debe cumplir las regulaciones de protección de datos aplicables.

### Notas adicionales:

- Esta versión refinada cumple INVEST, aborda ambigüedades, incluye criterios de aceptación detallados y está alineada con los objetivos de CyberGuard System.

- Preguntas abiertas para refinamiento adicional:
    - ¿Hay límites de frecuencia para reportes manuales por usuario?
    - ¿Cómo se notifica al usuario del éxito o fallo del reporte?
    - ¿Qué campos deben ser obligatorios para reportes automáticos?
    - ¿Qué parámetros debe permitir el replay del historial?

---


### Cuadro de diferencias detectadas

| Aspecto | Versión Original | Versión Refinada | Principio INVEST Aplicado |
|---------|------------------|------------------|---------------------------|
| **Independencia (I)** | Depende de endpoints, workers y colas | Depende parcialmente; recomendable dividir en sub-historias | Dividir en "Reporte Manual", "Ingesta Automática", "Worker" facilita independencia |
| **Negociable (N)** | Requisitos técnicos propuestos (ConfirmChannel, DLX) | Negociable: detalles técnicos pueden moverse a tareas técnicas | Mantener criterios de negocio claros; negociar implementación técnica |
| **Valiosa (V)** | Centralización y procesamiento asíncrono | Alta: aporta trazabilidad y resiliencia operativa | Genera valor directo para operaciones de seguridad |
| **Estimable (E)** | Falta detalle en flujos y datos | Estimable con contratos y esquemas definidos | Añadir especificaciones de payload y respuestas para estimaciones precisas |
| **Pequeña (S)** | Muy amplia | No — su alcance es grande; dividir para entregas incrementales | Fragmentar en historias más pequeñas mejora entrega |
| **Testeable (T)** | Criterios generales de validación | Testeable si se definen casos concretos (payloads, reintentos, DLX, replay) | Añadir casos de prueba concretos para cada escenario |

**Principales Mejoras Identificadas:**
1. Definir contrato de API y campos mínimos para reportes manuales y automáticos.
2. Especificar el comportamiento de publicación en RabbitMQ (reintentos, DLX, ConfirmChannel) y política de reintentos.
3. Aclarar qué datos van a PostgreSQL (registro canónico) y qué a Redis (replay/cache), y aplicar reglas GDPR/anonimización si procede.


---

## Conclusiones Generales

### Lecciones Aprendidas
- La aplicación sistemática de los principios INVEST permite detectar ambigüedades, dependencias ocultas y criterios de aceptación incompletos que, de otro modo, se descubrirían durante la implementación.
- Las historias de usuario que mezclan múltiples responsabilidades (ingesta, persistencia, notificación, replay) resultan difíciles de estimar y probar; fragmentarlas mejora la entrega incremental.
- Incluir requisitos de seguridad (autenticación, autorización, GDPR) desde la fase de refinamiento evita retrabajos y vulnerabilidades en etapas posteriores.

### Impacto del Refinamiento
- **HU-001:** Se mejoraron los criterios de validación de credenciales, manejo de bloqueo por intentos fallidos y cumplimiento GDPR, facilitando una estimación más precisa y pruebas exhaustivas.
- **HU-002:** Se detallaron los escenarios de validación de tokens, roles por endpoint y manejo de usuarios bloqueados, lo que permite definir pruebas unitarias y de integración concretas.
- **HU-003:** Se clarificaron actores (analista vs. servicio productor), se definieron campos obligatorios, reglas de validación con Joi, política de reintentos (3 intentos → DLX), criterios de persistencia (PostgreSQL + Redis) y filtros de replay, transformando una historia ambigua en criterios accionables.

### Recomendaciones para Futuras Historias
- Aplicar siempre los principios INVEST como checklist de calidad antes de aceptar una historia en el sprint.
- Separar criterios de negocio de detalles de implementación técnica; los últimos deben documentarse como tareas derivadas.
- Incluir desde el inicio criterios de autenticación, autorización, privacidad y auditoría para mantener coherencia con el contexto de seguridad del proyecto.
- Definir contratos de API (payloads, códigos de respuesta) y esquemas de datos como parte de los criterios de aceptación para facilitar la estimación y la automatización de pruebas.
- Dividir historias que abarquen más de un flujo funcional en sub-historias independientes y testeables.
