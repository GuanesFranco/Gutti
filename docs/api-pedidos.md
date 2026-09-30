# API de Pedidos

Todos los endpoints requieren un token JWT de un usuario con rol `ADMIN`.
Obtenerlo con `POST /api/v1/auth/iniciar-sesion` (admin de prueba: `admin@gutti.com` / `admin123`)
y enviarlo en el header `Authorization: Bearer <token>`. También se pueden probar desde Swagger:
`http://localhost:8080/swagger-ui/index.html` (botón **Authorize**).

## Pedidos — `/api/v1/pedidos`

| Método | Ruta | Historia de usuario | Respuesta |
|---|---|---|---|
| POST | `/api/v1/pedidos` | Registrar pedidos | 201 + pedido con total |

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

## Códigos de error
| Código | Cuándo |
|---|---|
| 400 | Datos inválidos (validaciones) |
| 401/403 | Sin token o usuario sin rol ADMIN |
| 404 | Producto inexistente |
| 409 | Stock insuficiente |
