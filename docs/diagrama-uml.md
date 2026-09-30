# Diagrama de clases (UML)

​```mermaid
---
config:
  layout: dagre
  class:
    hideEmptyMembersBox: false
---
classDiagram
direction TB
    class Producto {
	    -Long id
	    -String nombre
	    -String descripcion
	    -String categoria
	    -Double precio
	    -Integer stock
	    -boolean estadoActivo
	    +descontarStock(int cantidad) void
	    +tieneStockSuficiente(int cantidad) boolean
    }

    class Pedido {
	    -Long id
	    -LocalDateTime fechaHora
	    -EstadoPedido estado
	    -String nombreCliente
	    -String telefono
	    -String direccionEntrega
	    -TipoDeEntrega tipoDeEntrega
	    +agregarItem(Producto producto, int cantidad) void
	    +sumarItems() int
	    +calcularCostoTotal() Double
    }

    class ItemPedido {
	    -Long id
	    -Integer cantidad
	    -Double precioUnitario
	    +calcularSubtotal() Double
    }

    class Usuario {
	    -uuid Id
	    -string Email
	    -string Nombre
	    -string Rol
	    -string PasswordHash
    }

    class Cliente {
	    -string Dirección
	    -string Telefono
    }

  
	

    class EstadoPedido {
	    <<enumeration>>
	    PENDIENTE
	    ENTREGADO
    }

    class TipoDeEntrega {
	    <<enumeration>>
	    MOSTRADOR
	    DELIVERY
    }

    Usuario "1" --* "1" Cliente : tiene
    Pedido --> EstadoPedido
    Pedido --> TipoDeEntrega
    Pedido "1" *-- "*" ItemPedido : contiene
    ItemPedido "*" --> "1" Producto : asocia
  
    Pedido "*" <-- "1" Cliente : tiene
​```
