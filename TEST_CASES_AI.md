# Casos de Prueba Generados por IA - CyberGuard System

## Introducción

Este documento contiene los casos de prueba generados mediante la instrucción de SKAI "Generación de casos de prueba" para las 3 historias de usuario refinadas del proyecto CyberGuard System. Incluye los ajustes realizados por el probador con justificación técnica.


## HU-001: Autenticación de Usuario (Login)

### Casos de Prueba Generados por SKAI

**Matriz de prueba 1: Validación de combinaciones de email y contraseña (partición de equivalencia y valores límite)**

```gherkin
    Esquema del Caso de Prueba: Autenticación con diferentes combinaciones de email y contraseña
    Dado que el usuario se encuentra en la pantalla de inicio de sesión
    Y ha ingresado el email "<email>" y la contraseña "<contraseña>"
    Cuando solicita autenticarse
    Entonces el sistema responde con "<resultado>"

    Ejemplos:
        | email                        | contraseña           | resultado                                  |
        | usuario@dominio.com          | Passw0rd!            | Autenticación exitosa                      |
        | usuario@dominio.com          | incorrecta           | Credenciales incorrectas                   |
        | usuario_inexistente@fake.com | Passw0rd!            | Credenciales incorrectas                   |
        | usuario@dominio.com          |                      | Campos obligatorios faltantes              |
        |                             | Passw0rd!            | Campos obligatorios faltantes              |
        | usuario@dominio.com          | 123                  | Longitud inválida de la contraseña         |
        | u@d.c                        | Passw0rd!            | Longitud inválida del email                |
        | usuario@dominio.com          | Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd!Passw0rd! | Longitud inválida de la contraseña         |
        | usuario_dominio.com          | Passw0rd!            | Formato de email inválido                  |
        |                             |                      | Campos obligatorios faltantes              |
        | "     "                      | "     "              | Campos obligatorios faltantes              |
    
```

**Matriz de prueba 2: Validación de bloqueo por intentos fallidos**
```gherkin

Esquema del Caso de Prueba: Bloqueo temporal tras múltiples intentos fallidos
  Dado que el usuario se encuentra en la pantalla de inicio de sesión
  Y ha realizado "<intentos_fallidos>" intentos fallidos consecutivos
  Cuando solicita autenticarse nuevamente con "<credenciales>"
  Entonces el sistema responde con "<resultado>"

  Ejemplos:
    | intentos_fallidos | credenciales          | resultado                      |
    | 4                 | válidas               | Autenticación exitosa          |
    | 5                 | válidas               | Usuario bloqueado temporalmente|
    | 5                 | inválidas             | Usuario bloqueado temporalmente|
    | 3                 | inválidas             | Credenciales incorrectas       |
    | 0                 | válidas               | Autenticación exitosa          |

```

**Matriz de prueba 3: Inclusión de datos en el token JWT**

```gherkin
    Esquema del Caso de Prueba: Validez de los datos retornados en el token JWT
  Dado que el usuario se autentica exitosamente con email "<email>"
  Cuando recibe el token JWT
  Entonces el token contiene el "<id_usuario>", "<email>", y "<rol>"

  Ejemplos:
    | email                | id_usuario | rol          |
    | admin@cyber.com      | 123        | administrador|
    | colaborador@cyber.com| 456        | colaborador  |
    | user@cyber.com       | 789        | colaborador  |
```

**Matriz de prueba 4: Validación de canal seguro**
```gherkin
    Esquema del Caso de Prueba: Solicitudes de autenticación por canal seguro o inseguro
    Dado que el usuario intenta autenticarse usando el protocolo "<protocolo>"
    Cuando envía las credenciales
    Entonces el sistema responde con "<resultado>"

    Ejemplos:
        | protocolo | resultado                                  |
        | HTTPS     | Solicitud aceptada                         |
        | HTTP      | Solicitud rechazada. Solo se permite HTTPS |
        
```

### Ajustes Realizados por el Probador

| ID Caso | Caso de Prueba generado por la instrucción | Ajuste del realizado por el probador | ¿Por qué se ajustó? |
|---------|--------------------------|------------------|----------------------|
| **CP-001-01** <br>Autenticación exitosa con credenciales válidas | Autenticación exitosa con email y contraseña válidos. <br> Dado que el usuario se encuentra en la pantalla de inicio de sesió.<br> Y ha ingresado un email registrado y una contraseña correcta. <br>Cuando solicita autenticarse. <br> Entonces el sistema valida las credenciales contra Firebase. <br> Y genera un token JWT válido. <br> Y el token contiene el id, email y rol del usuario.<br> Y el usuario puede acceder a los recursos permitidos por su rol. | Se agregó verificación explícita de que el token JWT debe tener tiempo de expiración válido y que la respuesta HTTP debe ser 200 OK con estructura JSON definida. | El caso generado por SKAI no especifica el código de respuesta HTTP ni la estructura del token. Según BUSINESS_CONTEXT, el token debe incluir datos específicos (id, email, rol) y debe ser verificable. Es crítico validar la estructura completa de la respuesta para pruebas de integración. |
| **CP-001-02**<br>Autenticación fallida por email no registrado  | Autenticación fallida por email no registrado <br> Dado que el usuario se encuentra en la pantalla de inicio de sesión <br> Y ha ingresado un email no registrado y una contraseña cualquiera <br> Cuando solicita autenticarse <br> Entonces el sistema valida las credenciales contra Firebase <br> Y muestra un mensaje de error indicando credenciales incorrectas <br>  Y registra el intento fallido de autenticación| Se agregó verificación de que el código de respuesta HTTP debe ser 401 Unauthorized y que el mensaje de error no debe revelar si el email existe o no (seguridad). También se valida que el intento se registre en el sistema de auditoría con timestamp. | Por seguridad, el sistema no debe diferenciar entre "email no existe" vs "contraseña incorrecta" para evitar enumeración de usuarios. Según BUSINESS_CONTEXT, todos los intentos fallidos deben auditarse. El caso de SKAI no especifica el código HTTP ni las consideraciones de seguridad. |
| **CP-001-03**<br>Autenticación fallida por contraseña incorrecta | Autenticación fallida por contraseña incorrecta <br>  Dado que el usuario se encuentra en la pantalla de inicio de sesión <br>  Y ha ingresado un email registrado y una contraseña incorrecta <br> Cuando solicita autenticarse <br> Entonces el sistema valida las credenciales contra Firebase <br>  Y muestra un mensaje de error indicando credenciales incorrectas <br> Y registra el intento fallido de autenticación | Se agregó validación de que el mensaje de error debe ser genérico (igual que CP-001-02), código HTTP 401, y que el contador de intentos fallidos se incremente para ese usuario específico. | Consistencia con CP-001-02 en mensajes de error por seguridad. Según BUSINESS_CONTEXT, el sistema debe rastrear intentos fallidos para implementar bloqueo temporal tras 5 intentos. El caso de SKAI no menciona el incremento del contador de intentos. |
| **CP-001-04** <br>Validación de campos obligatorios | Validación de campos obligatorios vacíos <br> Dado que el usuario se encuentra en la pantalla de inicio de sesión <br> Y deja vacío el campo email o el campo contraseña <br> Cuando solicita autenticarse <br> Entonces el sistema rechaza la solicitud <br> Y muestra un mensaje indicando campos obligatorios faltantes <br> Y no consulta a Firebase | Se agregó verificación de código HTTP 400 Bad Request, validación de que la respuesta incluya detalles específicos del campo faltante (email o password), y que la validación ocurra en el backend con Joi antes de llamar a Firebase. | Según BUSINESS_CONTEXT, se usa Joi para validación estricta de datos de entrada. El caso de SKAI no especifica el código HTTP ni que la validación debe ser server-side. Es crítico validar que no se hagan llamadas innecesarias a Firebase por datos inválidos (optimización y seguridad). |
| **CP-001-05**<br>Validación de formato de email | **Dado que** el usuario ingresa un email con formato inválido y una contraseña cualquiera.<br>**Cuando** solicita autenticarse.<br>**Entonces** el sistema rechaza la solicitud.<br>**Y** muestra un mensaje indicando formato de email inválido.<br>**Y** no consulta a Firebase. | Se agregaron casos específicos de formato inválido: sin @, múltiples @, dominio sin TLD, caracteres especiales no permitidos. Código HTTP 400. Validación con regex de Joi que cumpla RFC 5322. | El caso de SKAI es genérico. Según BUSINESS_CONTEXT, Joi valida todos los endpoints. Se requieren casos específicos de partición de equivalencia para formatos inválidos comunes. Esto mejora la cobertura de pruebas de validación de entrada. |
| **CP-001-06**<br>Longitud de email | **Dado que** el usuario ingresa un email con menos de 5 o más de 254 caracteres y una contraseña válida.<br>**Cuando** solicita autenticarse.<br>**Entonces** el sistema rechaza la solicitud.<br>**Y** muestra un mensaje indicando longitud inválida del email.<br>**Y** no consulta a Firebase. | Se agregaron casos de valores límite específicos: 4 caracteres (justo debajo del mínimo), 5 caracteres (mínimo válido), 254 caracteres (máximo válido), 255 caracteres (justo sobre el máximo). Código HTTP 400. | Aplicación de técnica de valores límite (boundary value analysis). El caso de SKAI menciona los límites pero no especifica pruebas en los valores exactos de frontera. Según estándar RFC 5321, 254 es el máximo para emails. Crítico para validación robusta. |
| **CP-001-07**<br>Longitud de contraseña | **Dado que** el usuario ingresa un email válido y una contraseña con menos de 8 o más de 128 caracteres.<br>**Cuando** solicita autenticarse.<br>**Entonces** el sistema rechaza la solicitud.<br>**Y** muestra un mensaje indicando longitud inválida de la contraseña.<br>**Y** no consulta a Firebase. | Se agregaron casos de valores límite: 7 caracteres (debajo del mínimo), 8 caracteres (mínimo válido), 128 caracteres (máximo válido), 129 caracteres (sobre el máximo). Código HTTP 400. Validación de que el mensaje no revele la contraseña ingresada. | Aplicación de valores límite. El caso de SKAI no especifica pruebas en fronteras exactas. Según BUSINESS_CONTEXT, se usan estándares de seguridad para contraseñas. Es crítico que los mensajes de error no expongan la contraseña por seguridad (no debe aparecer en logs ni respuestas). |
| **CP-001-08**<br>Bloqueo temporal | **Dado que** el usuario ha ingresado credenciales incorrectas cinco veces consecutivas.<br>**Cuando** solicita autenticarse por sexta vez.<br>**Entonces** el sistema rechaza la solicitud.<br>**Y** muestra un mensaje indicando el bloqueo temporal.<br>**Y** registra el bloqueo del usuario. | Se agregó validación de código HTTP 429 Too Many Requests, verificación de que el mensaje incluya tiempo estimado de desbloqueo, validación de que incluso con credenciales correctas el acceso se bloquee, y que el bloqueo se registre en auditoría con timestamp y duración. | Según BUSINESS_CONTEXT, usuarios con intentos de fuerza bruta deben bloquearse temporalmente. El caso de SKAI no especifica el código HTTP apropiado (429 es estándar para rate limiting), ni valida el escenario crítico de que incluso credenciales válidas sean rechazadas durante el bloqueo. |
| **CP-001-09**<br>Registro de intentos | **Dado que** el usuario ingresa credenciales incorrectas en diferentes momentos.<br>**Cuando** solicita autenticarse.<br>**Entonces** el sistema registra cada intento fallido en el historial de auditoría.<br>**Y** asocia el intento al email ingresado y la fecha/hora. | Se agregó validación de que el registro de auditoría incluya: IP de origen, user-agent, timestamp preciso (ISO 8601), email intentado (sin la contraseña), y que los registros sean inmutables. También validar que intentos exitosos también se auditen. | Según BUSINESS_CONTEXT, debe haber auditoría y logging estructurado (Winston). El caso de SKAI no especifica qué datos adicionales deben auditarse. Para cumplir con regulaciones (GDPR mencionado en contexto), es crítico registrar IP y contexto, pero nunca contraseñas. Los intentos exitosos también deben auditarse para trazabilidad completa. |
| **CP-001-10**<br>Datos en JWT | **Dado que** el usuario ingresa un email registrado y una contraseña correcta.<br>**Cuando** solicita autenticarse.<br>**Entonces** el sistema genera un token JWT.<br>**Y** el token contiene el id, email y el rol asignado al usuario. | Se agregó validación de estructura completa del JWT: header (algoritmo HS256/RS256), payload (iss, sub, iat, exp, id, email, rol), signature válida. Verificar que exp sea futuro y que no incluya datos sensibles (contraseña). Validar formato de roles (administrador/colaborador). | El caso de SKAI solo menciona que el token contiene datos, pero no valida la estructura JWT completa. Según BUSINESS_CONTEXT, se usan estándares de seguridad para JWT y hay roles específicos (administrador/colaborador). Es crítico validar claims estándar (iat, exp, iss) y que no se filtren datos sensibles en el token. |


**Técnicas de Diseño Aplicadas:**
- **Partición de equivalencia:** Se dividieron las entradas en clases de equivalencia: emails válidos/inválidos (formato, longitud), contraseñas válidas/inválidas (longitud), credenciales existentes/no existentes, campos vacíos/con espacios/con datos. Cada partición representa un comportamiento esperado del sistema.
- **Valores límite:** Se probaron los límites exactos para longitud de email (4, 5, 254, 255 caracteres) y contraseña (7, 8, 128, 129 caracteres). También se validó el límite de intentos fallidos (4 intentos OK, 5 intentos = bloqueo).
- **Tabla de decisión:** Se aplicó en la Matriz 2 (bloqueo temporal) combinando condiciones: número de intentos fallidos (0, 3, 4, 5) × tipo de credenciales (válidas/inválidas) = diferentes resultados (autenticación exitosa, error, bloqueo).
- **Pruebas de estado:** Se validaron transiciones de estado del usuario: No autenticado → Autenticado (login exitoso), No autenticado → Bloqueado (5 intentos fallidos), Bloqueado → No autenticado (después del tiempo de bloqueo).

---

## HU-002: Middleware de Autenticación

### Casos de Prueba Generados por SKAI

**[PENDIENTE: Pegar aquí la salida completa de SKAI en formato Gherkin]**

```gherkin
# Aquí irán los escenarios Gherkin generados por SKAI
```

---

### Ajustes Realizados por el Probador

| ID Caso | Caso de Prueba generado por la instrucción | Ajuste del realizado por el probador | ¿Por qué se ajustó? |
|---------|--------------------------|------------------|----------------------|
| CP-002-01 | [Ejemplo: No valida token con formato incorrecto] | [Agregar escenario con token malformado] | [Validación de seguridad: el middleware debe rechazar tokens con formato inválido antes de intentar decodificar] |
| CP-002-02 | [Pendiente] | [Pendiente] | [Pendiente] |
| CP-002-03 | [Pendiente] | [Pendiente] | [Pendiente] |

**Técnicas de Diseño Aplicadas:**
- Partición de equivalencia: [Explicar aplicación]
- Valores límite: [Explicar aplicación]
- Pruebas de seguridad: [Explicar aplicación]
- Pruebas negativas: [Explicar aplicación]

---

## HU-003: Gestión de Amenazas Unificada

### Casos de Prueba Generados por SKAI

**[PENDIENTE: Pegar aquí la salida completa de SKAI en formato Gherkin]**

```gherkin
# Aquí irán los escenarios Gherkin generados por SKAI
```

---

### Ajustes Realizados por el Probador

| ID Caso | Caso de Prueba generado por la instrucción | Ajuste del realizado por el probador | ¿Por qué se ajustó? |
|---------|--------------------------|------------------|----------------------|
| CP-003-01 | [Ejemplo: Falta validar comportamiento cuando RabbitMQ no está disponible] | [Agregar escenario de fallo de conexión a RabbitMQ] | [Según arquitectura, debe enviarse a DLX. Crítico para resiliencia del sistema] |
| CP-003-02 | [Ejemplo: No valida persistencia en PostgreSQL tras publicación exitosa] | [Agregar verificación de persistencia en BD] | [Regla de negocio: amenazas deben cumplir ACID en PostgreSQL] |
| CP-003-03 | [Pendiente] | [Pendiente] | [Pendiente] |

**Técnicas de Diseño Aplicadas:**
- Partición de equivalencia: [Explicar aplicación]
- Pruebas de integración: [Explicar aplicación]
- Pruebas de resiliencia: [Explicar aplicación]
- Pruebas de flujo de eventos: [Explicar aplicación]

---

## Resumen de Cobertura

### Cobertura por Tipo de Prueba

| Tipo de Prueba | HU-001 | HU-002 | HU-003 | Total |
|----------------|--------|--------|--------|-------|
| Funcionales | [Pendiente] | [Pendiente] | [Pendiente] | [Pendiente] |
| Seguridad | [Pendiente] | [Pendiente] | [Pendiente] | [Pendiente] |
| Integración | [Pendiente] | [Pendiente] | [Pendiente] | [Pendiente] |
| Negativas | [Pendiente] | [Pendiente] | [Pendiente] | [Pendiente] |
| **TOTAL** | **[X]** | **[X]** | **[X]** | **[X]** |

### Análisis de Riesgos Cubiertos

| Riesgo Identificado | Historia Relacionada | Casos que lo Mitigan |
|---------------------|---------------------|---------------------|
| Acceso no autorizado | HU-001, HU-002 | [Pendiente] |
| Pérdida de eventos | HU-003 | [Pendiente] |
| Ataques de fuerza bruta | HU-001 | [Pendiente] |
| Tokens comprometidos | HU-002 | [Pendiente] |
| Fallo de servicios externos | HU-003 | [Pendiente] |

---

## Conclusiones

### Calidad de la Generación por IA
- **Fortalezas:** [Pendiente: Completar después de recibir salidas de SKAI]
- **Debilidades:** [Pendiente]
- **Áreas que requirieron más ajuste humano:** [Pendiente]

### Recomendaciones para Automatización
1. [Pendiente: Identificar casos prioritarios para Selenium]
2. [Pendiente]
3. [Pendiente]

### Próximos Pasos
- [ ] Implementar casos de prueba en Serenity BDD con patrón Screenplay
- [ ] Configurar ambiente de pruebas con datos de prueba
- [ ] Ejecutar suite completa y generar reporte
- [ ] Integrar con pipeline CI/CD
