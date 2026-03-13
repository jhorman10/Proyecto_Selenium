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

**Caso de prueba 1: Acceso autorizado con token válido y rol correcto**

```gherkin
Caso de prueba: Acceso exitoso a ruta protegida por un administrador autenticado

  Dado que tengo un token JWT válido en el header Authorization en formato "Bearer"
  Y el token contiene el rol "administrador" y el usuario está activo
  Cuando realizo una petición a un endpoint de administración protegido
  Entonces el acceso es permitido
  Y los datos del usuario (ID, correo electrónico, rol, estado de cuenta) se adjuntan al request
  Y se registra el acceso exitoso en el sistema de auditoría
```

**Caso de prueba 2: Acceso autorizado a endpoint de colaborador**

```gherkin
Caso de prueba: Acceso exitoso a ruta protegida por un colaborador autenticado

  Dado que tengo un token JWT válido en el header Authorization en formato "Bearer"
  Y el token contiene el rol "colaborador" y el usuario está activo
  Cuando realizo una petición a un endpoint de reporte de amenazas
  Entonces el acceso es permitido
  Y los datos del usuario se adjuntan al request
  Y se registra el acceso exitoso en el sistema de auditoría
```

**Caso de prueba 3: Acceso denegado a endpoint de administración por colaborador**

```gherkin
Caso de prueba: Intento de acceso a endpoint de administración por usuario colaborador

  Dado que tengo un token JWT válido en el header Authorization en formato "Bearer"
  Y el token contiene el rol "colaborador" y el usuario está activo
  Cuando realizo una petición a un endpoint de administración protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje descriptivo de falta de permisos
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 4: Token JWT expirado**

```gherkin
Caso de prueba: Intento de acceso con token JWT expirado

  Dado que tengo un token JWT expirado en el header Authorization en formato "Bearer"
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje descriptivo indicando token expirado
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 5: Token JWT inválido (firma incorrecta)**

```gherkin
Caso de prueba: Intento de acceso con token JWT con firma inválida

  Dado que tengo un token JWT inválido en el header Authorization en formato "Bearer"
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje descriptivo indicando token inválido
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 6: Ausencia del header Authorization**

```gherkin
Caso de prueba: Intento de acceso sin header Authorization

  Dado que no incluyo el header Authorization en la petición
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje indicando que falta el token
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 7: Token JWT en formato incorrecto**

```gherkin
Caso de prueba: Intento de acceso con formato incorrecto del token JWT

  Dado que incluyo un header Authorization pero no en formato "Bearer"
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje descriptivo de formato incorrecto
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 8: Usuario bloqueado intenta acceder**

```gherkin
Caso de prueba: Acceso denegado por usuario bloqueado

  Dado que tengo un token JWT válido en el header Authorization en formato "Bearer"
  Y el usuario correspondiente está bloqueado en el sistema
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje indicando usuario bloqueado
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 9: Usuario excede límites de uso**

```gherkin
Caso de prueba: Acceso denegado por exceder límites de uso

  Dado que tengo un token JWT válido en el header Authorization en formato "Bearer"
  Y el usuario ha excedido los límites de uso permitidos
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje indicando límite de uso excedido
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 10: Token de un producer accediendo a endpoint de administración**

```gherkin
Caso de prueba: Acceso denegado a endpoint de administración por usuario producer

  Dado que tengo un token JWT válido en el header Authorization en formato "Bearer"
  Y el token contiene el rol "producer"
  Cuando realizo una petición a un endpoint de administración protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje de falta de permisos
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 11: Acceso a endpoint público sin autenticación**

```gherkin
Caso de prueba: Acceso exitoso a endpoint público sin autenticación

  Dado que no incluyo el header Authorization en la petición
  Cuando realizo una petición a un endpoint público (por ejemplo, login o registro)
  Entonces el acceso es permitido
  Y no se requiere validación del token
  Y se registra el acceso en el sistema de auditoría como público
```

**Caso de prueba 12: Longitud máxima del token en el header Authorization**

```gherkin
Caso de prueba: Intento de acceso con token JWT que excede la longitud máxima permitida

  Dado que incluyo un token JWT en el header Authorization que excede la longitud máxima configurada
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje indicando token inválido por longitud
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 13: Token JWT con datos de usuario incompletos**

```gherkin
Caso de prueba: Intento de acceso con token JWT sin datos completos de usuario

  Dado que tengo un token JWT válido pero faltan datos requeridos (ejemplo: rol o ID)
  Cuando realizo una petición a cualquier endpoint protegido
  Entonces el acceso es denegado con código 401 Unauthorized
  Y se retorna un mensaje indicando datos insuficientes en el token
  Y se registra el intento fallido en el sistema de auditoría
```

**Caso de prueba 14: Validación de combinación de roles y endpoints**

```gherkin
Caso de prueba: Acceso según matriz de permisos configurada

  Dado que tengo un token JWT válido con un rol específico
  Cuando realizo una petición a un endpoint cuya autorización depende de la matriz de permisos
  Entonces el acceso es permitido o denegado según la configuración actual de la matriz
  Y el resultado es registrado en el sistema de auditoría
```

**Caso de prueba 15: Acceso concurrente con múltiples tokens para el mismo usuario**

```gherkin
Caso de prueba: Acceso concurrente con diferentes tokens JWT válidos para el mismo usuario

  Dado que tengo dos tokens JWT válidos para el mismo usuario en diferentes sesiones
  Cuando realizo peticiones simultáneas a endpoints protegidos
  Entonces ambos accesos se permiten si los tokens no están revocados ni expirados
  Y se registran ambos accesos en el sistema de auditoría con los datos de usuario correspondientes
```

### Ajustes Realizados por el Probador

| ID Caso | Caso de Prueba (Descripción) | Ajuste del Probador | ¿Por qué se ajustó? |
|---------|------------------------------|---------------------|----------------------|
| **CP-002-01** | **Token malformado no rechazado:**<br>**Dado que** envío un token con estructura inválida (ej. sin punto separador).<br>**Cuando** el middleware intenta decodificar.<br>**Entonces** retorna 401 con mensaje genérico. | Se agregó validación de que el middleware rechace tokens con estructura malformada (no 3 segmentos base64) **antes** de intentar verificar la firma, retornando 401 con mensaje genérico que no revele detalles de implementación. | La instrucción SKAI no diferencia entre token con firma incorrecta y token con estructura inválida. Según BUSINESS_CONTEXT, la seguridad JWT requiere validar estructura antes de firma para evitar ataques de inyección y reducir carga de procesamiento criptográfico innecesario. |
| **CP-002-02** | **Rol no contemplado en matriz:**<br>**Dado que** el token contiene un rol no definido en la matriz de permisos.<br>**Cuando** accede a cualquier ruta protegida.<br>**Entonces** se deniega acceso por defecto (deny-by-default). | Se agregó escenario donde un rol desconocido (ej. "auditor") intenta acceder. El sistema debe aplicar política deny-by-default: si el rol no está en la matriz, se deniega con 401. Verificar que no se aplique fail-open. | La instrucción no contempla roles fuera de la matriz configurada. Según BUSINESS_CONTEXT, los roles son administrador, colaborador y producer. Si un token válido contiene un rol inexistente, el middleware debe denegar por defecto (principio de menor privilegio). Evita escalamiento de privilegios. |
| **CP-002-03** | **Token revocado en lista negra:**<br>**Dado que** un token JWT válido fue agregado a una lista de revocación.<br>**Cuando** se usa para acceder a ruta protegida.<br>**Entonces** se deniega el acceso. | Se agregó escenario de token revocado: aunque no haya expirado, si fue invalidado (ej. usuario cambió contraseña, admin lo revocó), el middleware debe verificar contra lista de revocación y denegar con 401. Registrar en auditoría. | La instrucción no contempla revocación de tokens antes de su expiración. Según BUSINESS_CONTEXT, HU-002 debe manejar usuarios bloqueados y revocación. Si un administrador desactiva un usuario, los tokens activos deben dejar de funcionar inmediatamente. Crítico para seguridad ante tokens comprometidos. |
| **CP-002-04** | **Validación de claims obligatorios:**<br>**Dado que** el token es decodificable pero falta el claim "rol".<br>**Cuando** el middleware lo procesa.<br>**Entonces** deniega acceso con 401. | Se agregó validación exhaustiva de claims obligatorios: id, email, rol deben estar presentes. Si falta alguno, denegar con 401 y mensaje indicando "datos insuficientes en el token" sin detallar qué claim falta. Registrar en auditoría qué claim faltó (solo en logs internos). | La instrucción genera un caso genérico (caso 13) pero no distingue qué claims son obligatorios vs opcionales. Según BUSINESS_CONTEXT, el JWT debe incluir id, email y rol como mínimo para que el middleware pueda adjuntar datos al request y validar autorización. |
| **CP-002-05** | **Inyección en header Authorization:**<br>**Dado que** el header contiene caracteres especiales o scripts.<br>**Cuando** el middleware lo procesa.<br>**Entonces** rechaza sin procesarlo. | Se agregó escenario de inyección: header Authorization con contenido malicioso (ej. Bearer <script>alert(1)</script> o cadenas SQL). El middleware debe sanitizar/rechazar antes de cualquier procesamiento del token. Retornar 401 genérico. | La instrucción no contempla ataques de inyección a través del header. Es una prueba de seguridad fundamental (OWASP Top 10). El middleware es la primera línea de defensa y debe sanitizar todo input antes de procesarlo. |

**Técnicas de Diseño Aplicadas:**
- **Partición de equivalencia:** Se dividieron los tokens en clases: válido/expirado/firma inválida/malformado/revocado/incompleto. Los roles en: administrador (acceso total), colaborador (acceso parcial), producer (sin acceso admin), rol desconocido (deny-by-default). Los endpoints en: protegidos y públicos.
- **Valores límite:** Se probó el límite de longitud máxima del token en el header Authorization. Se validaron tokens con exactamente los claims mínimos requeridos vs. tokens con un claim faltante (frontera entre válido e inválido).
- **Pruebas de seguridad:** Se incluyeron escenarios de inyección en header, tokens revocados, política deny-by-default para roles desconocidos, y validación de que los mensajes de error no revelen detalles internos de implementación (prevención de information disclosure).
- **Pruebas negativas:** Se cubrieron todos los escenarios de rechazo: sin token, token expirado, firma inválida, formato incorrecto, usuario bloqueado, límites excedidos, rol sin permisos, datos incompletos y acceso concurrente.

---

## HU-003: Gestión de Amenazas Unificada

### Casos de Prueba Generados por SKAI

**Caso de prueba 1: Reporte manual exitoso por usuario autenticado**

```gherkin
Caso de prueba: Reporte manual exitoso
Dado que un usuario autenticado con el rol "usuario/colaborador" posee un JWT válido
Y tiene acceso al endpoint POST /api/threats
Cuando envía un reporte con los campos requeridos: tipo (válido del catálogo), severidad (bajo), descripción (25 caracteres), fecha (hoy), fuente (identificada correctamente)
Entonces el sistema acepta el reporte, publica el evento en RabbitMQ, persiste la amenaza en PostgreSQL y notifica vía WebSocket
Y la acción queda registrada en el sistema de auditoría y logging estructurado
```

**Caso de prueba 2: Acceso denegado a endpoint por usuario no autenticado**

```gherkin
Caso de prueba: Acceso denegado sin autenticación
Dado que un usuario no está autenticado
Cuando intenta acceder al endpoint POST /api/threats
Entonces el sistema rechaza la solicitud con un mensaje de error de autenticación
Y registra el intento fallido en los logs de auditoría
```

**Caso de prueba 3: Acceso denegado por rol incorrecto**

```gherkin
Caso de prueba: Acceso denegado por rol incorrecto
Dado que un usuario autenticado con un rol diferente a "usuario/colaborador" (por ejemplo, "administrador")
Cuando intenta reportar una amenaza usando POST /api/threats
Entonces el sistema rechaza la solicitud por falta de permisos
Y registra el intento fallido en los logs de auditoría
```

**Caso de prueba 4: Validación de tipo de amenaza fuera de catálogo**

```gherkin
Caso de prueba: Tipo de amenaza inválido
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte cuyo tipo de amenaza no pertenece al catálogo predefinido
Entonces el sistema rechaza la solicitud con un mensaje de error de validación
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 5: Validación de severidad fuera de valores permitidos**

```gherkin
Caso de prueba: Severidad inválida
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte con severidad distinta a "bajo", "medio", "alto" o "crítico"
Entonces el sistema rechaza la solicitud con un mensaje de error de validación
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 6: Descripción por debajo del mínimo**

```gherkin
Caso de prueba: Descripción demasiado corta
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte con una descripción de menos de 20 caracteres
Entonces el sistema rechaza la solicitud con un mensaje de error indicando la longitud mínima requerida
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 7: Fecha en el futuro**

```gherkin
Caso de prueba: Fecha de amenaza futura
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte con una fecha posterior al día actual
Entonces el sistema rechaza la solicitud con un mensaje de error de validación
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 8: Fuente de amenaza no identificada**

```gherkin
Caso de prueba: Fuente no identificada
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte sin especificar la fuente del incidente
Entonces el sistema rechaza la solicitud con un mensaje de error por campo obligatorio faltante
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 9: Reporte automático exitoso desde servicio productor**

```gherkin
Caso de prueba: Reporte automático exitoso
Dado que un servicio productor autenticado publica un evento en RabbitMQ con todos los campos requeridos y válidos
Cuando el evento es recibido por el sistema
Entonces el evento se valida, publica en RabbitMQ, se persiste en PostgreSQL y se notifica a los clientes vía WebSocket
Y la acción queda registrada en el sistema de auditoría y logging estructurado
```

**Caso de prueba 10: Reporte automático con dato inválido**

```gherkin
Caso de prueba: Reporte automático con error de validación
Dado que un servicio productor autenticado publica un evento en RabbitMQ con un campo inválido (por ejemplo, severidad "urgente")
Cuando el sistema procesa el evento
Entonces rechaza el evento, lo registra en los logs de auditoría y retorna un mensaje descriptivo de error
```

**Caso de prueba 11: Publicación fallida a RabbitMQ con reintentos y envío a DLX**

```gherkin
Caso de prueba: Publicación a RabbitMQ falla y reintenta
Dado que un usuario autenticado reporta una amenaza válida
Y RabbitMQ no está disponible para publicar el evento
Cuando el sistema intenta publicar el evento hasta 3 veces sin éxito
Entonces el evento es enviado automáticamente a la Dead Letter Exchange (DLX)
Y se registra el incidente en los logs de auditoría
```

**Caso de prueba 12: Persistencia fallida en PostgreSQL**

```gherkin
Caso de prueba: Persistencia fallida en base de datos
Dado que un usuario autenticado reporta una amenaza válida
Y ocurre un error al persistir los datos en PostgreSQL
Cuando el sistema intenta guardar la amenaza
Entonces retorna un mensaje descriptivo de error al usuario y registra el incidente en los logs de auditoría
```

**Caso de prueba 13: Replay del historial exitoso por usuario**

```gherkin
Caso de prueba: Replay de historial exitoso
Dado que un usuario autenticado está conectado vía WebSocket
Y existen amenazas históricas almacenadas en Redis
Cuando solicita el replay del historial filtrando por usuario, rango de fechas y tipo de amenaza
Entonces el sistema retorna únicamente los eventos que cumplen los filtros
```

**Caso de prueba 14: Replay con filtros sin coincidencias**

```gherkin
Caso de prueba: Replay sin resultados
Dado que un usuario autenticado está conectado vía WebSocket
Cuando solicita el replay del historial con filtros que no corresponden a ningún evento registrado
Entonces el sistema retorna una respuesta vacía o mensaje indicando ausencia de resultados
```

**Caso de prueba 15: Logging y auditoría de acciones exitosas**

```gherkin
Caso de prueba: Registro de acciones exitosas
Dado que un usuario autenticado reporta una amenaza válida
Cuando la operación se completa exitosamente
Entonces la acción debe quedar registrada en el sistema de auditoría y logging estructurado con todos los detalles relevantes
```

**Caso de prueba 16: Logging y auditoría de acciones fallidas**

```gherkin
Caso de prueba: Registro de errores en auditoría
Dado que ocurre un error durante la validación, publicación o persistencia de un reporte de amenaza
Cuando ocurre el incidente
Entonces el sistema debe registrar el fallo con suficiente detalle en el sistema de auditoría y logging estructurado
```

**Caso de prueba 17: Ingreso de datos excediendo los límites permitidos (longitud máxima)**

```gherkin
Caso de prueba: Campos exceden longitud máxima
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte donde la descripción o el campo fuente excede la longitud máxima permitida por el sistema
Entonces el sistema rechaza la solicitud con un mensaje de error de validación
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 18: Combinación de campos válidos y no válidos**

```gherkin
Caso de prueba: Envío de reporte con combinación de campos válidos y no válidos
Dado que un usuario autenticado accede al endpoint POST /api/threats
Cuando envía un reporte con tipo válido, severidad válida, pero fecha inválida y descripción demasiado corta
Entonces el sistema rechaza la solicitud y retorna mensajes descriptivos por cada campo inválido
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 19: Bloqueo temporal por exceder límite de uso**

```gherkin
Caso de prueba: Bloqueo por exceso de reportes
Dado que un usuario autenticado realiza múltiples intentos de reporte en un corto período de tiempo, excediendo el límite permitido
Cuando intenta realizar un nuevo reporte
Entonces el sistema bloquea temporalmente al usuario y retorna un mensaje informativo
Y registra el incidente en los logs de auditoría
```

**Caso de prueba 20: Reconexión y replay tras desconexión de WebSocket**

```gherkin
Caso de prueba: Reconexión y replay tras desconexión
Dado que un usuario autenticado estaba conectado vía WebSocket y se desconectó
Cuando se reconecta al sistema y solicita el replay del historial
Entonces el sistema permite la reconexión y entrega el historial solicitado desde Redis
```

### Ajustes Realizados por el Probador

| ID Caso | Caso de Prueba (Descripción) | Ajuste del Probador | ¿Por qué se ajustó? |
|---------|------------------------------|---------------------|----------------------|
| **CP-003-01** | **Fallo de RabbitMQ sin validar DLX:**<br>**Dado que** un reporte válido se publica.<br>**Cuando** RabbitMQ falla tras 3 reintentos.<br>**Entonces** se envía a DLX. | Se agregó verificación de que el evento efectivamente llegue a la DLX y sea recuperable. Validar que el ConfirmChannel retorne nack y que el sistema no pierda el evento. Verificar que el usuario reciba mensaje de "reporte encolado para procesamiento posterior". | La instrucción genera el escenario de fallo (caso 11) pero no valida que el evento sea recuperable desde la DLX. Según BUSINESS_CONTEXT, los eventos deben confirmarse en RabbitMQ (ConfirmChannel) y reenviarse a DLX en caso de fallo. Es crítico garantizar zero-event-loss para la resiliencia del sistema. |
| **CP-003-02** | **Persistencia sin validar ACID:**<br>**Dado que** un reporte se persiste en PostgreSQL.<br>**Cuando** la transacción se completa.<br>**Entonces** se verifica consistencia. | Se agregó validación de que la persistencia en PostgreSQL cumpla ACID: verificar que si falla el commit, se haga rollback completo (no queden registros parciales). Validar que los campos almacenados coincidan con los enviados. Verificar anonimización de datos personales según GDPR. | La instrucción no valida la integridad transaccional ni el cumplimiento GDPR en la persistencia. Según BUSINESS_CONTEXT, las amenazas deben cumplir restricciones ACID en PostgreSQL y los datos personales deben anonimizarse conforme a GDPR. Sin esta validación, podrían existir registros corruptos o violaciones de privacidad. |
| **CP-003-03** | **Replay sin validar límites de Redis:**<br>**Dado que** se solicita replay de historial.<br>**Cuando** hay miles de eventos.<br>**Entonces** se aplica paginación. | Se agregó validación de que el replay desde Redis aplique paginación o límite máximo de eventos por solicitud. Verificar que los filtros (usuario, fecha, tipo) se apliquen correctamente en Redis y no en memoria. Validar rendimiento con volumen alto de datos. | La instrucción genera escenarios de replay (casos 13-14) pero no contempla volúmenes altos. Según BUSINESS_CONTEXT, el historial se almacena en Redis para replay. En producción, sin paginación, una solicitud de replay podría causar timeout o consumo excesivo de memoria en el servidor. |
| **CP-003-04** | **WebSocket sin validar autenticación:**<br>**Dado que** un cliente se conecta vía WebSocket.<br>**Cuando** no envía token de autenticación.<br>**Entonces** se rechaza la conexión. | Se agregó validación de que las conexiones WebSocket requieran autenticación JWT válida. Si el token expira durante una sesión activa, el servidor debe cerrar la conexión y forzar re-autenticación. El cliente no autenticado no debe recibir notificaciones. | La instrucción asume que el usuario "está conectado vía WebSocket" sin validar que la conexión esté autenticada. Según BUSINESS_CONTEXT, el acceso debe estar protegido por JWT. Las conexiones WebSocket sin autenticación representan un vector de ataque que permitiría a atacantes recibir notificaciones de amenazas en tiempo real. |
| **CP-003-05** | **Reporte manual sin validar respuesta al usuario:**<br>**Dado que** un reporte manual es aceptado.<br>**Cuando** se completa el flujo completo.<br>**Entonces** el usuario recibe confirmación. | Se agregó validación de que tras un reporte exitoso, el usuario reciba una respuesta HTTP 201 Created con el ID de la amenaza creada y un timestamp. El sistema debe confirmar que el evento fue publicado en RabbitMQ antes de responder al usuario. No responder 200 si la publicación aún está pendiente. | La instrucción genera el happy path (caso 1) pero no especifica qué recibe el usuario como confirmación. Según BUSINESS_CONTEXT, se usa ConfirmChannel en RabbitMQ, lo que implica que el sistema espera confirmación antes de responder. Es crítico que el usuario sepa que su reporte fue procesado correctamente y tenga un ID para seguimiento. |

**Técnicas de Diseño Aplicadas:**
- **Partición de equivalencia:** Se dividieron los reportes en clases: manuales (vía API) vs automáticos (vía RabbitMQ). Los campos se particionaron en válidos/inválidos para tipo (catálogo/fuera de catálogo), severidad (bajo|medio|alto|crítico vs otros), descripción (≥20 chars vs <20), fecha (pasada/hoy vs futura) y fuente (presente vs ausente).
- **Pruebas de integración:** Se validó el flujo completo end-to-end: API → validación Joi → RabbitMQ (ConfirmChannel) → PostgreSQL (ACID) → WebSocket → Redis (replay). Cada punto de integración tiene escenarios de éxito y fallo para garantizar que la cadena no se rompa silenciosamente.
- **Pruebas de resiliencia:** Se cubrieron escenarios de fallo en cada componente externo: RabbitMQ no disponible (reintentos + DLX), PostgreSQL caído (rollback + mensaje de error), Redis sin datos (respuesta vacía), WebSocket desconectado (reconexión + replay). Se validó que el sistema degrade gracefully sin perder eventos.
- **Pruebas de flujo de eventos:** Se verificó el ciclo de vida completo del evento: publicación → confirmación (ConfirmChannel) → consumo por worker → persistencia → notificación → almacenamiento en Redis → replay. Se incluyeron escenarios de filtrado por usuario, rango de fechas y tipo de amenaza para validar que el replay sea preciso.

---

## Resumen de Cobertura

### Cobertura por Tipo de Prueba

| Tipo de Prueba | HU-001 | HU-002 | HU-003 | Total |
|----------------|--------|--------|--------|-------|
| Funcionales | 4 | 3 | 9 | 16 |
| Seguridad | 4 | 7 | 2 | 13 |
| Integración | 1 | 2 | 6 | 9 |
| Negativas | 1 | 3 | 3 | 7 |
| **TOTAL** | **10** | **15** | **20** | **45** |

### Análisis de Riesgos Cubiertos

| Riesgo Identificado | Historia Relacionada | Casos que lo Mitigan |
|---------------------|---------------------|---------------------|
| Acceso no autorizado | HU-001, HU-002 | CP-001-02, CP-001-03, CP-002: casos 3-10, 13 |
| Pérdida de eventos | HU-003 | CP-003: casos 9, 11, 12; ajustes CP-003-01, CP-003-02 |
| Ataques de fuerza bruta | HU-001 | CP-001-04, CP-001-05 (bloqueo temporal y desbloqueo) |
| Tokens comprometidos | HU-002 | CP-002: casos 4, 5, 7, 8, 12, 13; ajustes CP-002-01, CP-002-03 |
| Fallo de servicios externos | HU-003 | CP-003: casos 11, 12, 20; ajuste CP-003-03 |

---

## Conclusiones

### Calidad de la Generación por IA
- **Fortalezas:** SKAI generó una cobertura amplia de escenarios funcionales y de seguridad, incluyendo flujos alternativos y negativos. Los casos siguen una estructura Gherkin clara y cubren las reglas de negocio principales de cada HU. La generación fue especialmente fuerte en escenarios de validación de datos (Joi) y manejo de roles.
- **Debilidades:** La IA no diferenció casos de prueba según la HU correcta (los escenarios de middleware se generaron con contexto de amenazas y viceversa). No contempló escenarios de seguridad avanzados como inyección en headers, revocación de tokens o política deny-by-default. Faltó validar la integridad transaccional (ACID) y el cumplimiento GDPR en persistencia.
- **Áreas que requirieron más ajuste humano:** Validación de resiliencia end-to-end (DLX, rollback, reconexión WebSocket), escenarios de seguridad ofensiva (inyección, tokens revocados), rendimiento y paginación en replay de historial, y confirmación de respuesta al usuario tras reporte exitoso.

### Recomendaciones para Automatización
1. **Prioridad alta:** Automatizar los escenarios de autenticación (HU-001) y middleware (HU-002) con Serenity BDD + REST Assured, ya que son prerequisitos de seguridad para todo el sistema.
2. **Prioridad media:** Automatizar los flujos de reporte de amenazas (HU-003 casos 1-8) con validación de respuestas HTTP y verificación de persistencia en PostgreSQL.
3. **Prioridad baja:** Automatizar escenarios de integración con RabbitMQ y WebSocket usando contenedores Docker para simular fallos de infraestructura (DLX, reconexión).

### Próximos Pasos
- [ ] Implementar casos de prueba en Serenity BDD con patrón Screenplay
- [ ] Configurar ambiente de pruebas con datos de prueba
- [ ] Ejecutar suite completa y generar reporte
- [ ] Integrar con pipeline CI/CD
