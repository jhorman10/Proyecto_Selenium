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
| **Independencia (I)** |  Cumple parcialmente. Aunque la autenticación es independiente, está ligada a Firebase y JWT, lo que podría generar dependencias técnicas. | Asegurar que la implementación de Firebase y JWT esté bien definida y documentada para evitar bloqueos en el desarrollo. | [Justificación] |
| **Negociable (N)** | Cumple. La historia puede ser refinada o ajustada según las necesidades del negocio. | [Análisis pendiente] | [Justificación] |
| **Valiosa (V)** |  Cumple. La autenticación es un componente crítico para el acceso seguro a los recursos del sistema. | [Análisis pendiente] | [Justificación] |
| **Estimable (E)** | Cumple parcialmente. Aunque la funcionalidad es clara, la falta de detalles específicos (como validaciones y manejo de errores) podría dificultar la estimación precisa. | Refinar los criterios de aceptación para facilitar la estimación | [Justificación] |
| **Pequeña (S)** | Cumple. La historia tiene un alcance limitado y puede desarrollarse en un sprint. | [Análisis pendiente] | [Justificación] |
| **Testeable (T)** | Cumple parcialmente. Los criterios de aceptación son verificables | necesitan más detalles para garantizar pruebas exhaustivas (por ejemplo, pruebas de validación de credenciales y manejo de errores). | [Justificación] |

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
| **Independencia (I)** | Cumple parcialmente. La historia depende de una implementación previa de autenticación (Firebase + JWT)  | [Análisis pendiente] |  Si estos no están listos, la historia podría no ser implementable de forma independiente.|
| **Negociable (N)** | Cumple | [Análisis pendiente] |  La historia está abierta a negociación sobre cómo se validan los tokens y qué datos se adjuntan al request, pero requiere mayor detalle para negociaciones más productivas. |
| **Valiosa (V)** | Cumple | [Análisis pendiente] |  Proporciona valor directo al negocio asegurando que solo usuarios autenticados y autorizados accedan a recursos sensibles. |
| **Estimable (E)** | Cumple parcialmente | [Análisis pendiente] | Sin detalles adicionales (por ejemplo, rutas específicas, estructura de roles, manejo de errores detallado), la estimación puede ser inexacta. |
| **Pequeña (S)** | Cumple parcialmente | [Análisis pendiente] | La historia podría dividirse en varias: (a) validación de autenticación base, (b) manejo de roles, (c) manejo de errores, para asegurar granularidad y entrega incremental. |
| **Testeable (T)** | Cumple | [Análisis pendiente] | Los criterios de aceptación permiten definir pruebas unitarias y de integración, aunque sería mejor especificar casos de prueba para cada tipo de error y para cada rol.|

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

**[PENDIENTE: Pegar aquí la salida de SKAI después de ejecutar la instrucción "Diagnosticar historias de usuario"]**

```
[Aquí irá la historia refinada que SKAI genere]
```

---

### Cuadro de diferencias detectadas

| Aspecto | Versión Original | Versión Refinada | Principio INVEST Aplicado |
|---------|------------------|------------------|---------------------------|
| **Independencia (I)** | [Análisis pendiente] | [Análisis pendiente] | [Justificación] |
| **Negociable (N)** | [Análisis pendiente] | [Análisis pendiente] | [Justificación] |
| **Valiosa (V)** | [Análisis pendiente] | [Análisis pendiente] | [Justificación] |
| **Estimable (E)** | [Análisis pendiente] | [Análisis pendiente] | [Justificación] |
| **Pequeña (S)** | [Análisis pendiente] | [Análisis pendiente] | [Justificación] |
| **Testeable (T)** | [Análisis pendiente] | [Análisis pendiente] | [Justificación] |

**Principales Mejoras Identificadas:**
1. [Pendiente: Completar después de recibir salida de SKAI]
2. [Pendiente]
3. [Pendiente]

---

## Conclusiones Generales

### Lecciones Aprendidas
- [Pendiente: Completar después del análisis completo]

### Impacto del Refinamiento
- [Pendiente: Completar después del análisis completo]

### Recomendaciones para Futuras Historias
- [Pendiente: Completar después del análisis completo]
