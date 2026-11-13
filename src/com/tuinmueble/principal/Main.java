package com.tuinmueble.principal;

import com.tuinmueble.dao.InmuebleDAO;
import com.tuinmueble.dao.InmuebleDAOImpl;
import com.tuinmueble.modelo.Inmueble;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Programa de consola para probar el módulo de inmuebles (CRUD con JDBC).
 *
 * Uso:
 *   java ... com.tuinmueble.principal.Main          -> menú interactivo
 *   java ... com.tuinmueble.principal.Main --demo   -> prueba automática del CRUD
 *
 * @author Ometh Martinez Potes
 */
public class Main {

    private static final InmuebleDAO INMUEBLE_DAO = new InmuebleDAOImpl();
    private static final Scanner LECTOR = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            if (args.length > 0 && "--demo".equals(args[0])) {
                ejecutarDemostracion();
            } else {
                mostrarMenu();
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }

    /** Ejecuta las cuatro operaciones del CRUD de forma automática. */
    private static void ejecutarDemostracion() throws SQLException {
        System.out.println("=== MODULO DE INMUEBLES - TU INMUEBLE SUROESTE (JDBC + MySQL) ===\n");

        System.out.println("1. CONSULTAR: inmuebles registrados");
        imprimirLista(INMUEBLE_DAO.listarTodos());

        System.out.println("\n2. INSERTAR: nuevo inmueble");
        Inmueble nuevoInmueble = new Inmueble(1, "Finca cafetera en Andes",
                "Finca de 5 hectareas con casa y beneficiadero.", "finca", "venta",
                new BigDecimal("780000000.00"), "Vereda San Jose", "Andes", "disponible");
        int idNuevo = INMUEBLE_DAO.insertar(nuevoInmueble);
        System.out.println("   Insertado con id " + idNuevo);
        imprimirLista(INMUEBLE_DAO.listarTodos());

        System.out.println("\n3. ACTUALIZAR: cambiar precio y estado del inmueble " + idNuevo);
        Inmueble inmuebleEditado = INMUEBLE_DAO.consultarPorId(idNuevo).orElseThrow();
        inmuebleEditado.setPrecio(new BigDecimal("750000000.00"));
        inmuebleEditado.setEstado("reservado");
        System.out.println("   Actualizado: " + INMUEBLE_DAO.actualizar(inmuebleEditado));
        System.out.println("   " + INMUEBLE_DAO.consultarPorId(idNuevo).orElseThrow());

        System.out.println("\n4. CONSULTAR POR MUNICIPIO: Andes");
        imprimirLista(INMUEBLE_DAO.listarPorMunicipio("Andes"));

        System.out.println("\n5. ELIMINAR: inmueble " + idNuevo);
        System.out.println("   Eliminado: " + INMUEBLE_DAO.eliminar(idNuevo));
        imprimirLista(INMUEBLE_DAO.listarTodos());

        System.out.println("\n6. REGISTRO INEXISTENTE: consultar, actualizar y eliminar el id " + idNuevo);
        System.out.println("   Consulta: " + INMUEBLE_DAO.consultarPorId(idNuevo)
                .map(Inmueble::toString).orElse("no existe"));
        System.out.println("   Actualizado: " + INMUEBLE_DAO.actualizar(inmuebleEditado));
        System.out.println("   Eliminado: " + INMUEBLE_DAO.eliminar(idNuevo));

        System.out.println("\n7. DATO INVALIDO: insertar un inmueble con precio negativo");
        Inmueble inmuebleInvalido = new Inmueble(1, "Lote de prueba invalido", null, "lote",
                "venta", new BigDecimal("-100"), null, "Jardin", "disponible");
        try {
            INMUEBLE_DAO.insertar(inmuebleInvalido);
            System.out.println("   ERROR: la base de datos acepto un precio negativo");
        } catch (SQLException e) {
            System.out.println("   Rechazado por la base de datos (chk_inmuebles_precio): " + e.getMessage());
        }
        System.out.println("   Total de inmuebles: " + INMUEBLE_DAO.listarTodos().size());

        System.out.println("\n=== CRUD completado correctamente ===");
    }

    private static void mostrarMenu() throws SQLException {
        int opcion;
        do {
            System.out.println("\n=== MODULO DE INMUEBLES - TU INMUEBLE SUROESTE ===");
            System.out.println("1. Registrar inmueble");
            System.out.println("2. Listar inmuebles");
            System.out.println("3. Consultar inmueble por id");
            System.out.println("4. Actualizar inmueble");
            System.out.println("5. Eliminar inmueble");
            System.out.println("0. Salir");
            opcion = leerEntero("Opcion: ");

            // Un error en una operacion no cierra el programa: se informa y se vuelve al menu.
            try {
                switch (opcion) {
                    case 1 -> registrarInmueble();
                    case 2 -> imprimirLista(INMUEBLE_DAO.listarTodos());
                    case 3 -> consultarInmueble();
                    case 4 -> actualizarInmueble();
                    case 5 -> eliminarInmueble();
                    case 0 -> System.out.println("Hasta luego.");
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (SQLException e) {
                System.out.println("No se pudo completar la operacion: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void registrarInmueble() throws SQLException {
        Inmueble inmueble = new Inmueble();
        inmueble.setIdUsuario(leerEntero("Id del propietario: "));
        llenarDatos(inmueble);
        int idGenerado = INMUEBLE_DAO.insertar(inmueble);
        System.out.println("Inmueble registrado con id " + idGenerado);
    }

    private static void consultarInmueble() throws SQLException {
        int idInmueble = leerEntero("Id del inmueble: ");
        Optional<Inmueble> inmueble = INMUEBLE_DAO.consultarPorId(idInmueble);
        System.out.println(inmueble.map(Inmueble::toString).orElse("No existe ese inmueble."));
    }

    private static void actualizarInmueble() throws SQLException {
        int idInmueble = leerEntero("Id del inmueble a actualizar: ");
        Optional<Inmueble> encontrado = INMUEBLE_DAO.consultarPorId(idInmueble);
        if (encontrado.isEmpty()) {
            System.out.println("No existe ese inmueble.");
            return;
        }
        Inmueble inmueble = encontrado.get();
        System.out.println("Actual: " + inmueble);
        llenarDatos(inmueble);
        System.out.println(INMUEBLE_DAO.actualizar(inmueble) ? "Inmueble actualizado." : "No se actualizo.");
    }

    private static void eliminarInmueble() throws SQLException {
        int idInmueble = leerEntero("Id del inmueble a eliminar: ");
        System.out.println(INMUEBLE_DAO.eliminar(idInmueble) ? "Inmueble eliminado." : "No existe ese inmueble.");
    }

    private static void llenarDatos(Inmueble inmueble) {
        inmueble.setTitulo(leerTexto("Titulo: "));
        inmueble.setDescripcion(leerTexto("Descripcion: "));
        inmueble.setTipo(leerTexto("Tipo (casa, apartamento, finca, lote, local): "));
        inmueble.setOperacion(leerTexto("Operacion (venta / arriendo): "));
        inmueble.setPrecio(leerPrecio("Precio: "));
        inmueble.setDireccion(leerTexto("Direccion: "));
        inmueble.setMunicipio(leerTexto("Municipio: "));
        inmueble.setEstado(leerTexto("Estado (disponible, reservado, vendido, arrendado): "));
    }

    private static void imprimirLista(List<Inmueble> inmuebles) {
        if (inmuebles.isEmpty()) {
            System.out.println("   (sin inmuebles registrados)");
        }
        inmuebles.forEach(inmueble -> System.out.println("   " + inmueble));
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return LECTOR.nextLine().trim();
    }

    private static BigDecimal leerPrecio(String mensaje) {
        while (true) {
            try {
                BigDecimal precio = new BigDecimal(leerTexto(mensaje));
                if (precio.signum() > 0) {
                    return precio;
                }
                System.out.println("El precio debe ser mayor que cero.");
            } catch (NumberFormatException e) {
                System.out.println("Escriba un precio valido, por ejemplo 350000000.");
            }
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Escriba un numero valido.");
            }
        }
    }
}
