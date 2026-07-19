package com.aleslul.biblioteca.scheduler;

import com.aleslul.biblioteca.model.Libro;
import com.aleslul.biblioteca.model.Prestamo;
import com.aleslul.biblioteca.model.Reserva;
import com.aleslul.biblioteca.model.enums.EstadoPrestamo;
import com.aleslul.biblioteca.model.enums.EstadoReserva;
import com.aleslul.biblioteca.model.enums.TipoNotificacion;
import com.aleslul.biblioteca.repository.LibroRepository;
import com.aleslul.biblioteca.repository.PrestamoRepository;

import com.aleslul.biblioteca.model.enums.TipoAccion;
import com.aleslul.biblioteca.repository.ReservaRepository;
import com.aleslul.biblioteca.service.LogSistemaService;
import com.aleslul.biblioteca.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// RF10: revisa diariamente préstamos por vencer y libros disponibles con reservas pendientes,
// y persiste las notificaciones correspondientes (el envío real de push/email queda fuera de alcance).
@Component
public class NotificacionScheduler {
    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private LogSistemaService logSistemaService;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Value("${app.notificaciones.horas-anticipacion-vencimiento}")
    private long horasAnticipacionVencimiento;

    @Scheduled(cron = "${app.notificaciones.cron-revision-diaria}")
    public void revisionDiaria() {
        notificarPrestamosPorVencer();
        notificarLibrosDisponibles();
    }

    private void notificarPrestamosPorVencer() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(horasAnticipacionVencimiento);

        List<Prestamo> prestamosPorVencer = prestamoRepository.findByEstadoInAndFechaVencimientoBetween(
                List.of(EstadoPrestamo.ACTIVO, EstadoPrestamo.RENOVADO), ahora, limite);

        for (Prestamo prestamo : prestamosPorVencer) {
            String mensaje = "Tu préstamo #" + prestamo.getId() + " vence el " + prestamo.getFechaVencimiento() +
                    ". Recuerda devolverlo o renovarlo a tiempo.";
            notificacionService.crearSiNoExiste(
                    prestamo.getUsuario().getId(),
                    TipoNotificacion.PRESTAMO_POR_VENCER,
                    mensaje,
                    prestamo.getId());

            logSistemaService.registrarAccion(
                    prestamo.getUsuario().getId(),
                    TipoAccion.NOTIFICACION_VENCIMIENTO,
                    "Notificación de vencimiento generada para el préstamo ID " + prestamo.getId());
        }
    }

    private void notificarLibrosDisponibles() {
        List<Libro> librosDisponibles = libroRepository.findByCopiesAvailableGreaterThan(0);

        for (Libro libro : librosDisponibles) {
            List<Reserva> reservasActivas = reservaRepository
                    .findByLibroIdAndEstadoOrderByFechaReservaAsc(libro.getId(), EstadoReserva.ACTIVA);

            if (reservasActivas.isEmpty()) {
                continue;
            }

            // Se notifica a quien reservó primero (orden de llegada).
            Reserva primeraReserva = reservasActivas.get(0);
            String mensaje = "El libro '" + libro.getTitulo() + "' que reservaste ya está disponible.";
            notificacionService.crearSiNoExiste(
                    primeraReserva.getUsuario().getId(),
                    TipoNotificacion.LIBRO_DISPONIBLE,
                    mensaje,
                    libro.getId());

            logSistemaService.registrarAccion(
                    primeraReserva.getUsuario().getId(),
                    TipoAccion.NOTIFICACION_LIBRO_DISPONIBLE,
                    "Notificación de disponibilidad generada para el libro '" + libro.getTitulo() + "'");
        }
    }
}