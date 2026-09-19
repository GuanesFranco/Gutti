# Diagrama de secuencia 

​```mermaid
sequenceDiagram
    actor Usuario
    participant Pedido
    participant ItemPedido
    participant Producto

    Usuario->>Pedido: agregarItem(producto, cantidad)
    Pedido->>Producto: getStock()
    Producto-->>Pedido: stock

    alt stock >= cantidad
        Pedido->>ItemPedido: <<create>> crear(producto, cantidad)
        Pedido->>Producto: descontarStock(cantidad)
        Pedido->>Pedido: sumarItems()
        Pedido-->>Usuario: confirmacion("Item agregado", total)
    else stock < cantidad
        Pedido-->>Usuario: error("Stock insuficiente")
    end
​```
