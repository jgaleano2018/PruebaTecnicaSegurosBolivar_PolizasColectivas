package com.segurosbolivar.polizas.colectivas.shared.domain.util;

/** Enmascaramiento de datos personales para logs (no registrar PII completa). */
public final class DatoSensible {

    private DatoSensible() {
    }

    public static String email(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] partes = email.split("@", 2);
        String usuario = partes[0];
        String visible = usuario.length() <= 2 ? usuario.substring(0, 1) : usuario.substring(0, 2);
        return visible + "***@" + partes[1];
    }

    public static String telefono(String telefono) {
        if (telefono == null || telefono.length() < 4) {
            return "***";
        }
        return "*".repeat(telefono.length() - 4) + telefono.substring(telefono.length() - 4);
    }

    public static String documento(String documento) {
        return telefono(documento);
    }
}
