package org.eduardomango.authmicroservice.models.Enum;

public enum UserRole {
    ROLE_ADMIN,          // Administrador con todos los permisos
    ROLE_SELLER,         // Vendedor/Comerciante que gestiona productos y pedidos
    ROLE_CUSTOMER,       // Cliente que realiza compras y deja reseñas
    ROLE_DELIVERY_STAFF, // Personal de entrega
    ROLE_SUPPORT_AGENT,  // Agente de soporte que responde tickets

    ROLE_GUEST,          // Usuario invitado

    // Subroles administrativos
    ROLE_USER_MANAGER,   // Gestión de usuarios
    ROLE_ORDER_MANAGER,  // Gestión de pedidos
    ROLE_PRODUCT_MANAGER,// Gestión de productos e inventario
    ROLE_CONTENT_MODERATOR; // Moderador de contenido generado por usuarios


}
