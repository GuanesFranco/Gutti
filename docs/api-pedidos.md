# API de Pedidos

Todos los endpoints requieren un token JWT de un usuario con rol `ADMIN`.
Obtenerlo con `POST /api/v1/auth/iniciar-sesion` (admin de prueba: `admin@gutti.com` / `admin123`)
y enviarlo en el header `Authorization: Bearer <token>`. También se pueden probar desde Swagger:
`http://localhost:8080/swagger-ui/index.html` (botón **Authorize**).

## Pedidos — `/api/v1/pedidos`

| Método | Ruta | Historia de usuario | Respuesta |
|---|---|---|---|
| POST | `/api/v1/pedidos` | Registrar pedidos | 201 + pedido con total |
| GET | `/api/v1/pedidos?tipoDeEntrega=&desde=&hasta=` | Consultar pedidos | 200 + historial y recaudación |
| GET | `/api/v1/pedidos/{id}` | Consultar pedidos | 200 + detalle del pedido |
| PUT | `/api/v1/pedidos/{id}` | Modificar pedidos | 200 + pedido con total recalculado |
| PATCH | `/api/v1/pedidos/{id}/estado` | Modificar pedidos | 200 |
| DELETE | `/api/v1/pedidos/{id}` | Eliminar pedidos | 204 |

### Registrar pedido
```json
POST /api/v1/pedidos
{
  "tipoDeEntrega": "DELIVERY",
  "nombreCliente": "Ana",
  "telefono": "1122334455",
  "direccionEntrega": "Av. Calchaquí 6200",
  "items": [
    { "productoId": 1, "cantidad": 2 },
    { "productoId": 3, "cantidad": 1 }
  ]
}
```
- `tipoDeEntrega`: `MOSTRADOR` o `DELIVERY` (en `DELIVERY` la dirección es obligatoria).
- Descuenta el stock de cada producto y guarda el precio unitario del momento de la venta.
- Sin productos → **400** "El pedido debe tener al menos un producto".
- Cantidad mayor al stock → **409** "Stock insuficiente para el producto ...". No se registra nada.

### Consultar pedidos (historial)
`GET /api/v1/pedidos?tipoDeEntrega=MOSTRADOR&desde=2026-10-01&hasta=2026-10-05`
(todos los filtros son opcionales; las fechas son `AAAA-MM-DD` e incluyen el día `hasta` completo).
```json
{
  "pedidos": [ { "id": 1, "estado": "PENDIENTE", "total": 10800.0, "items": [ ... ] } ],
  "recaudacionTotal": 10800.0,
  "mensaje": null
}
```
Si no hay resultados: `"pedidos": []` y `"mensaje": "No se encontraron pedidos"`.

### Modificar pedido
- `PUT /api/v1/pedidos/{id}` con el mismo formato que el registro: reemplaza datos de entrega y productos,
  repone el stock anterior, descuenta el nuevo y recalcula el total.
  Cantidad 0 o negativa → **400** "Ingrese una cantidad válida (mayor a cero)".
  Un pedido `ENTREGADO` no se puede modificar → **409**.
- `PATCH /api/v1/pedidos/{id}/estado` con `{ "estado": "ENTREGADO" }`.

### Eliminar pedido
`DELETE /api/v1/pedidos/{id}` → repone el stock; al no existir más, deja de sumar en la recaudación.
Si está entregado → **409** "No se puede eliminar un pedido ya entregado."

## Códigos de error
| Código | Cuándo |
|---|---|
| 400 | Datos inválidos (validaciones) |
| 401/403 | Sin token o usuario sin rol ADMIN |
| 404 | Pedido o producto inexistente |
| 409 | Stock insuficiente o pedido ya entregado |
