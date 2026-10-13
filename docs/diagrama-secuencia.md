# Diagrama de secuencia 

​```mermaid
sequenceDiagram
    actor Usuario
    participant PedidoService
    participant Producto
    participant Pedido

    Usuario->>PedidoService: registrar(datos del pedido)
    loop por cada producto pedido
        PedidoService->>Producto: descontarStock(cantidad)
        alt stock >= cantidad
            Producto-->>PedidoService: stock descontado
            PedidoService->>Pedido: agregar ItemPedido(producto, cantidad, precioUnitario)
        else stock < cantidad
            Producto-->>PedidoService: StockInsuficienteException
            PedidoService-->>Usuario: error 409 "Stock insuficiente"
        end
    end
    PedidoService->>Pedido: calcularCostoTotal()
    PedidoService-->>Usuario: 201 pedido registrado con su total
​```
