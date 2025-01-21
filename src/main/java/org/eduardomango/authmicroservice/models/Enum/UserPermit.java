package org.eduardomango.authmicroservice.models.Enum;

public enum UserPermit {
    // Gestión de usuarios
    USER_READ("user:read"),
    USER_READ_SELF("user:read_self"),
    USER_UPDATE("user:update"),         // Actualizar información de usuarios
    USER_DELETE("user:delete"),         // Eliminar cuentas de usuario

    // Gestión de productos
    PRODUCT_CREATE("product:create"),   // Crear productos
    PRODUCT_READ("product:read"),       // Ver productos
    PRODUCT_UPDATE("product:update"),   // Actualizar productos
    PRODUCT_DELETE("product:delete"),   // Eliminar productos

    // Gestión de pedidos
    ORDER_CREATE("order:create"),       // Crear pedidos
    ORDER_READ("order:read"),           // Ver pedidos
    ORDER_UPDATE("order:update"),       // Actualizar pedidos
    ORDER_DELETE("order:delete"),       // Cancelar pedidos

    // Gestión de inventario
    INVENTORY_READ("inventory:read"),   // Ver inventario
    INVENTORY_UPDATE("inventory:update"), // Actualizar inventario

    // Gestión de reseñas y disputas
    REVIEW_MODERATE("review:moderate"), // Moderar reseñas
    DISPUTE_MANAGE("dispute:manage"),   // Gestionar disputas

    // Notificaciones y soporte
    NOTIFICATION_SEND("notification:send"), // Enviar notificaciones
    SUPPORT_RESPOND("support:respond"), // Responder a tickets de soporte

    // Gestión de contenido general
    CONTENT_CREATE("content:create"),   // Crear contenido (banners, páginas, etc.)
    CONTENT_READ("content:read"),       // Leer contenido
    CONTENT_UPDATE("content:update"),   // Actualizar contenido
    CONTENT_DELETE("content:delete");   // Eliminar contenido

    private final String code;

    UserPermit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
