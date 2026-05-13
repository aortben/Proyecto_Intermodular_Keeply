package com.proyecto.keeply.config;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.Set;

/**
 * Configuración centralizada de validación de archivos.
 * Define tipos MIME permitidos, extensiones y tamaños máximos.
 */
public class FileValidationConfig {

    // Tamaños máximos en bytes
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;   // 5 MB
    public static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;  // 50 MB
    public static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024;  // 10 MB

    // Tipos MIME permitidos
    public static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    public static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/quicktime"
    );
    public static final Set<String> ALLOWED_AUDIO_TYPES = Set.of(
            "audio/mpeg", "audio/wav", "audio/ogg", "audio/mp4"
    );

    // Extensiones permitidas
    public static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp", ".gif",
            ".mp4", ".webm", ".mov",
            ".mp3", ".wav", ".ogg", ".m4a"
    );

    /**
     * Valida un archivo multimedia. Devuelve null si es válido, o un mensaje de error.
     */
    public static String validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "El archivo está vacío";
        }

        // Validar extensión
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "Nombre de archivo no válido";
        }
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                : "";
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "Extensión no permitida: " + extension + ". Extensiones válidas: " + ALLOWED_EXTENSIONS;
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null) {
            return "No se pudo determinar el tipo del archivo";
        }

        boolean isImage = ALLOWED_IMAGE_TYPES.contains(contentType);
        boolean isVideo = ALLOWED_VIDEO_TYPES.contains(contentType);
        boolean isAudio = ALLOWED_AUDIO_TYPES.contains(contentType);

        if (!isImage && !isVideo && !isAudio) {
            return "Tipo de archivo no permitido: " + contentType;
        }

        // Validar tamaño según tipo
        long size = file.getSize();
        if (isImage && size > MAX_IMAGE_SIZE) {
            return "La imagen supera el tamaño máximo de 5 MB (" + (size / 1024 / 1024) + " MB)";
        }
        if (isVideo && size > MAX_VIDEO_SIZE) {
            return "El video supera el tamaño máximo de 50 MB (" + (size / 1024 / 1024) + " MB)";
        }
        if (isAudio && size > MAX_AUDIO_SIZE) {
            return "El audio supera el tamaño máximo de 10 MB (" + (size / 1024 / 1024) + " MB)";
        }

        return null; // Válido
    }

    /**
     * Valida un archivo que debe ser exclusivamente una imagen.
     */
    public static String validateImageOnly(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "El archivo está vacío";
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return "Solo se permiten imágenes (JPEG, PNG, WebP, GIF)";
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            return "La imagen supera el tamaño máximo de 5 MB";
        }

        return null; // Válido
    }
}
