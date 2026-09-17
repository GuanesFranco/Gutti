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
    }

    class Pedido {
	    -Long id
	    -LocalDateTime fechaHora
	    -String estado
	    -String nombreCliente
	    -String telefono
		-String tipo_de_entrega
	    +sumarItems() Double
	    +calcularCostoTotal() Double
    }

    class ItemPedido {
	    -Long id
	    -Integer cantidad
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

  
	

    Usuario "1" --* "1" Cliente : tiene
    Pedido "1" *-- "*" ItemPedido : contiene
    ItemPedido "*" --> "1" Producto : asocia
  
    Pedido "*" <-- "1" Cliente : tiene
​```
