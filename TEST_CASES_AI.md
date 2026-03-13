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

| ID Caso | Caso de Prueba (Descripción) | Ajuste del Probador | ¿Por qué se ajustó? |
|---------|------------------------------|---------------------|----------------------|
| **CP-001-01** | **Email Válido (RFC 5322):**<br>**Dado que** estoy en el formulario.<br>**Cuando** ingreso un email con formato válido y pass válida.<br>**Entonces** el sistema permite continuar el proceso. | Se agregó validación de código HTTP 200 OK, verificación de estructura del token JWT (header, payload, signature), validación de claims obligatorios (id, email, rol, iat, exp), y que el token sea decodificable. | El caso generado por SKAI no especifica el código de respuesta HTTP ni la estructura completa del JWT. Según BUSINESS_CONTEXT, el token debe incluir datos específicos del usuario y cumplir estándares de seguridad JWT. Es crítico validar la respuesta completa para pruebas de integración. |
| **CP-001-02** | **Formato Inválido / Pass corta / Falta Mayúscula / Minúscula / Número / Especial:**<br>**Dado que** el email o la pass no cumplen los requisitos de complejidad (8+ chars, regex).<br>**Cuando** intento autenticarme.<br>**Entonces** muestra mensaje genérico: *"Credenciales inválidas. Por favor, verifica tu email y contraseña"* y registra en auditoría. | Se agregó validación de código HTTP 400 Bad Request para errores de formato, verificación de que la validación ocurra con Joi antes de consultar Firebase, y que el mensaje de error sea genérico sin revelar qué campo específico falló. | Según BUSINESS_CONTEXT, se usa validación estricta con Joi en todos los endpoints. El mensaje genérico es una práctica de seguridad para evitar enumeración de usuarios. Es crítico validar en backend antes de llamadas externas (optimización y seguridad). |
| **CP-001-03** | **Credenciales Incorrectas (Lógica):**<br>**Dado que** el usuario existe pero la pass es errónea (o viceversa).<br>**Cuando** solicita acceso.<br>**Entonces** muestra el mensaje genérico y registra el intento fallido. | Se agregó código HTTP 401 Unauthorized, validación de que el contador de intentos fallidos se incremente para ese usuario, registro en auditoría con timestamp, IP y email intentado (sin contraseña), y consistencia en el mensaje de error. | Según BUSINESS_CONTEXT, el sistema debe rastrear intentos fallidos para implementar bloqueo anti-brute force. El mensaje debe ser idéntico al de CP-001-02 para no revelar si el usuario existe. La auditoría es obligatoria para cumplir con logging estructurado (Winston). |
| **CP-001-04** | **Bloqueo Temporal (Anti-Brute Force):**<br>**Dado que** realizo 6 intentos fallidos en < 15 min.<br>**Cuando** intento el 7mo ingreso.<br>**Entonces** bloquea al usuario por 30 min, envía notificación por email y rechaza intentos subsiguientes. | Se agregó código HTTP 429 Too Many Requests, validación de que incluso con credenciales correctas el acceso sea bloqueado durante el período de penalización, verificación de notificación por email al usuario, y registro del bloqueo en auditoría con duración. | Según BUSINESS_CONTEXT, usuarios con intentos de fuerza bruta deben bloquearse temporalmente. El código 429 es el estándar para rate limiting. Es crítico validar que el bloqueo sea efectivo incluso con credenciales válidas (seguridad). La notificación por email alerta al usuario legítimo de posible ataque. |
| **CP-001-05** | **Desbloqueo Automático:**<br>**Dado que** el usuario fue bloqueado por intentos fallidos.<br>**Cuando** pasan los 30 min de penalización.<br>**Entonces** el sistema permite nuevos intentos de acceso. | Se agregó validación de que el contador de intentos fallidos se resetee a cero tras el desbloqueo, verificación de que el usuario pueda autenticarse exitosamente con credenciales válidas, y registro del desbloqueo automático en auditoría. | El caso de SKAI no especifica qué sucede con el contador de intentos tras el desbloqueo. Es crítico resetear el contador para permitir nuevos intentos legítimos. La auditoría del desbloqueo es importante para trazabilidad completa del ciclo de vida del bloqueo. |
| **CP-001-06** | **JWT Claims & GDPR:**<br>**Dado que** la autenticación es exitosa.<br>**Cuando** se genera el token.<br>**Entonces** debe incluir: ID, email, rol, permisos, exp y el claim de cumplimiento GDPR. | Se agregó validación de estructura completa del JWT: algoritmo de firma (HS256/RS256), claims estándar (iss, sub, iat, exp), claims personalizados (id, email, rol), verificación de que exp sea futuro, y que no incluya datos sensibles (contraseña). | Según BUSINESS_CONTEXT, se usan estándares de seguridad para JWT y el sistema debe cumplir con GDPR. Es crítico validar todos los claims estándar y personalizados, verificar la firma, y asegurar que no se filtren datos sensibles en el token (puede ser decodificado sin la clave). |
| **CP-001-07** | **Caída de Firebase:**<br>**Dado que** el servicio de Firebase no está disponible.<br>**Cuando** intento iniciar sesión.<br>**Entonces** muestra: *"Servicio temporalmente no disponible. Intenta más tarde"* (sin filtrar errores de la API). | Se agregó código HTTP 503 Service Unavailable, validación de que el mensaje de error no exponga detalles técnicos de Firebase, verificación de retry logic con backoff exponencial, y registro del error en logs internos (Winston) sin exponer al usuario. | El caso de SKAI no especifica el código HTTP apropiado ni el manejo de errores de servicios externos. Según BUSINESS_CONTEXT, hay integración con Firebase y logging estructurado. Es crítico no exponer errores internos al usuario (seguridad) pero sí registrarlos para debugging. |
| **CP-001-08** | **Cifrado y Texto Plano:**<br>**Dado que** el sistema procesa credenciales.<br>**Cuando** finaliza el proceso.<br>**Entonces** se verifica que NADA se haya guardado en texto plano y la info sensible esté cifrada. | Se agregó validación de que las contraseñas nunca aparezcan en logs, respuestas HTTP, base de datos, o memoria después del procesamiento. Verificación de que se use hashing (bcrypt/argon2) para contraseñas, y que los logs de auditoría no contengan datos sensibles. | Según BUSINESS_CONTEXT, debe cumplirse con GDPR y estándares de seguridad para almacenamiento de contraseñas. Es crítico validar que ningún dato sensible se exponga en ningún punto del sistema (logs, BD, respuestas). Esta es una prueba de seguridad fundamental. |
| **CP-001-09** | **Límites de Longitud (Límite Superior):**<br>**Dado que** ingreso un email de 254 chars o contraseña al límite permitido.<br>**Cuando** intento autenticarme.<br>**Entonces** el sistema procesa normalmente (o rechaza con error genérico si se excede). | Se agregaron casos de valores límite específicos: email con 254 caracteres (máximo válido según RFC 5321), 255 caracteres (debe rechazar), contraseña con 128 caracteres (máximo válido), 129 caracteres (debe rechazar). Código HTTP 400 para valores excedidos. | Aplicación de técnica de valores límite (boundary value analysis). El caso de SKAI menciona límites pero no especifica pruebas en valores exactos de frontera. Es crítico probar justo en el límite, justo debajo y justo arriba para detectar errores off-by-one. |
| **CP-001-10** | **Pruebas de Integración y Unitarias:**<br>**Dado que** se ejecutan tests unitarios/integración.<br>**Cuando** se prueban flujos exitosos y fallidos contra Firebase.<br>**Entonces** se validan los retornos, la creación de logs y la activación de bloqueos. | Se agregó validación de cobertura de código (mínimo 80%), pruebas unitarias con mocks de Firebase, pruebas de integración con Firebase real en ambiente de staging, validación de todos los paths (happy path, error paths, edge cases), y verificación de logs estructurados. | Según BUSINESS_CONTEXT, hay integración con Firebase y logging con Winston. Es crítico tener pruebas automatizadas en múltiples niveles (unitarias con mocks para rapidez, integración para validar comportamiento real). La cobertura de código asegura que todos los escenarios estén probados. |

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
