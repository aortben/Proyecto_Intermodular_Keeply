package com.proyecto.keeply.controllers;

import com.proyecto.keeply.config.FileValidationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador REST encargado de gestionar la subida y visualización de archivos multimedia.
 * (Imágenes de perfil, banners de biblioteca, archivos adjuntos en las notas, etc.)
 */
@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    // Ruta del directorio donde se guardarán los archivos en el servidor (por defecto 'uploads')
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path uploadPath;

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * Se encarga de crear el directorio de subidas si este no existe.
     */
    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads", e);
        }
    }

    /**
     * Sube un archivo multimedia general validado y devuelve la URL para acceder a él.
     * @param file El archivo enviado en la petición (multipart/form-data).
     * @return ResponseEntity con la URL pública generada o un error si la validación falla.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // Valida el archivo usando la configuración centralizada (tamaño, tipo, extensión)
        String validationError = FileValidationConfig.validate(file);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        try {
            // Extrae la extensión y genera un nombre único basado en UUID para evitar colisiones
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Guarda el archivo en el sistema de ficheros del servidor
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Devuelve la URL relativa que el frontend puede usar para acceder al archivo
            String fileUrl = "/api/archivos/" + filename;

            return ResponseEntity.ok(Map.of(
                "url", fileUrl,
                "filename", filename
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al subir el archivo: " + e.getMessage()));
        }
    }

    /**
     * Sube un archivo destinado a ser imagen de perfil (avatar).
     * Aplica una validación más estricta (solo imágenes permitidas).
     * @param file La imagen a subir.
     * @return ResponseEntity con la URL pública generada.
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // Valida que sea estrictamente una imagen y no supere el tamaño límite
        String validationError = FileValidationConfig.validateImageOnly(file);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // Añade el prefijo 'avatar-' al nombre del archivo generado
            String filename = "avatar-" + UUID.randomUUID().toString() + extension;

            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/archivos/" + filename;
            return ResponseEntity.ok(Map.of("url", fileUrl, "filename", filename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al subir avatar: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para servir (visualizar o descargar) un archivo previamente subido.
     * Determina automáticamente el tipo de contenido (MIME type) para que el navegador lo renderice correctamente.
     * @param filename Nombre del archivo solicitado.
     * @return El recurso del archivo como ResponseEntity.
     */
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = uploadPath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Detecta el tipo MIME del archivo (ej. image/png, video/mp4)
            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
            } catch (IOException e) {
                contentType = "application/octet-stream"; // Tipo por defecto si no se puede identificar
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    // La cabecera "inline" indica al navegador que intente mostrar el archivo en lugar de forzar la descarga
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
