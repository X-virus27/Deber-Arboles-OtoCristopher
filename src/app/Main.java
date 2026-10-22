package app;

import enums.TipoRecorrido;
import enums.TipoUnidad;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;
import negocio.NodoLibro;
import negocio.NodoUnidad;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Interfaz de consola. Los bucles de interacción no implementan operaciones de árboles. */
public final class Main {
    private final Scanner entrada;
    private ArbolBiblioteca biblioteca = new ArbolBiblioteca();

    private Main(Scanner entrada) { this.entrada = entrada; }

    public static void main(String[] args) {
        if (args.length == 1 && "--trazas".equals(args[0])) { mostrarTrazasPersonales(); return; }
        if (args.length == 1 && "--pruebas".equals(args[0])) { PruebasBiblioteca.main(new String[0]); return; }
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            new Main(scanner).ejecutar();
        }
    }

    private void ejecutar() {
        System.out.println("======Biblioteca FISEI======");
        boolean continuar = true;
        while (continuar) {
            menu();
            try {
                switch (entero("Opción: ")) {
                    case 0: continuar = false; break;
                    case 1:
                        if (texto("Se reemplazarán los datos actuales. ¿Cargar? (s/n): ").equalsIgnoreCase("s")) {
                            biblioteca = DatosPrueba.crearBiblioteca();
                            System.out.println("Estructura inicial cargada: 7 unidades y 13 libros.");
                        }
                        break;
                    case 2: System.out.print(biblioteca.mostrarJerarquia()); break;
                    case 3: agregarUnidad(); break;
                    case 4:
                        NodoUnidad unidad = biblioteca.buscar(texto("Código de unidad: "));
                        System.out.println(unidad == null ? "Unidad inexistente." : unidad.getDato());
                        break;
                    case 5:
                        System.out.println("Altura (aristas): " + biblioteca.altura());
                        System.out.println("Hojas: " + biblioteca.contarHojas());
                        System.out.println("Total de libros: " + biblioteca.contarLibrosTotal());
                        break;
                    case 6: System.out.println("Nivel: " + biblioteca.nivel(texto("Código de unidad: "))); break;
                    case 7: insertarLibro(); break;
                    case 8:
                        Libro libro = estante().buscar(entero("Código del libro: "));
                        System.out.println(libro == null ? "Libro inexistente." : libro);
                        break;
                    case 9: modificarLibro(); break;
                    case 10:
                        System.out.println(estante().eliminar(entero("Código a eliminar: "))
                                ? "Libro eliminado." : "Libro inexistente."); break;
                    case 11:
                        CatalogoABB catalogo = estante();
                        for (TipoRecorrido tipo : TipoRecorrido.values()) {
                            System.out.println(tipo + ": " + secuencia(catalogo, tipo));
                            System.out.println(catalogo.trazaRecorrido(tipo));
                        }
                        break;
                    case 12: retos(); break;
                    default: System.out.println("Opción inexistente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Dato inválido: " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Fin de la entrada.");
                continuar = false;
            }
        }
        System.out.println("Programa finalizado.");
    }

    private void menu() {
        System.out.println("\n1. Cargar estructura inicial\n2. Mostrar jerarquía\n3. Agregar unidad"
                + "\n4. Buscar unidad\n5. Altura, hojas y total de libros\n6. Nivel de unidad"
                + "\n7. Insertar libro\n8. Buscar libro\n9. Modificar libro\n10. Eliminar libro"
                + "\n11. Tres recorridos y trazas\n12. Retos"
                + "\n0. Salir");
    }

    private String texto(String mensaje) { System.out.print(mensaje); return entrada.nextLine().trim(); }

    /** Lee líneas completas para evitar el salto pendiente de nextInt(). */
    private int entero(String mensaje) {
        while (true) {
            try { return Integer.parseInt(texto(mensaje)); }
            catch (NumberFormatException e) { System.out.println("Escriba un entero válido."); }
        }
    }

    private CatalogoABB estante() {
        NodoUnidad nodo = biblioteca.buscar(texto("Código del estante: "));
        if (nodo == null || nodo.getDato().getTipo() != TipoUnidad.ESTANTE) {
            throw new IllegalArgumentException("La unidad no existe o no es un ESTANTE.");
        }
        return nodo.getCatalogo();
    }

    private void agregarUnidad() {
        String padre = texto("Código del padre: ");
        String codigo = texto("Código nuevo: ");
        String nombre = texto("Nombre: ");
        TipoUnidad tipo = TipoUnidad.valueOf(texto("Tipo (AREA/ESTANTE): ").toUpperCase(Locale.ROOT));
        System.out.println(biblioteca.agregar(padre, new UnidadBiblioteca(codigo, nombre, tipo))
                ? "Unidad agregada." : "No se agregó: padre inexistente, duplicado o relación inválida.");
    }

    private void insertarLibro() {
        CatalogoABB c = estante();
        Libro nuevo = new Libro(entero("Código: "), texto("Título: "), texto("Autor: "), entero("Año (1900-2026): "));
        System.out.println(c.insertar(nuevo) ? "Libro insertado." : "Código repetido: no se insertó.");
    }

    private void modificarLibro() {
        CatalogoABB c = estante();
        int codigo = entero("Código a modificar: ");
        if (c.buscar(codigo) == null) { System.out.println("Libro inexistente."); return; }
        c.modificar(codigo, texto("Nuevo título: "), texto("Nuevo autor: "), entero("Nuevo año: "));
        System.out.println("Libro modificado.");
    }

    private void retos() {
        System.out.println("1. Contar en rango\n2. Camino de búsqueda\n3. Validar ABB\n4. Reconstruir desde preorden");
        int opcion = entero("Reto: ");
        if (opcion == 4) {
            String linea = texto("Preorden separado por espacios (vacío = árbol vacío): ");
            String[] partes = linea.isEmpty() ? new String[0] : linea.split("\\s+");
            int[] codigos = new int[partes.length];
            for (int i = 0; i < partes.length; i++) { codigos[i] = Integer.parseInt(partes[i]); }
            CatalogoABB reconstruido = CatalogoABB.desdePreorden(codigos);
            System.out.println("Catálogo independiente reconstruido; libros con metadatos de ejemplo.");
            for (TipoRecorrido t : TipoRecorrido.values()) { System.out.println(t + ": " + secuencia(reconstruido, t)); }
        } else if (opcion >= 1 && opcion <= 3) {
            CatalogoABB c = estante();
            if (opcion == 1) { System.out.println("Cantidad: " + c.contarEnRango(entero("Desde: "), entero("Hasta: "))); }
            if (opcion == 2) { System.out.println("Camino: " + c.caminoBusqueda(entero("Código: "))); }
            if (opcion == 3) { System.out.println("ABB válido: " + c.esABBValido()); }
        } else { System.out.println("Reto inexistente."); }
    }

    public static String secuencia(CatalogoABB c, TipoRecorrido tipo) {
        StringBuilder salida = new StringBuilder();
        secuencia(c.recorrer(tipo), 0, salida);
        return salida.toString();
    }

    private static void secuencia(List<Libro> libros, int i, StringBuilder salida) {
        if (i == libros.size()) { return; }
        if (i > 0) { salida.append(' '); }
        salida.append(libros.get(i).getCodigo());
        secuencia(libros, i + 1, salida);
    }

    /** Demostración reproducible para comparar TRAZAS.md */
    public static void mostrarTrazasPersonales() {
        CatalogoABB c = DatosPrueba.crearCatalogoPersonal();
        System.out.println("Dígitos: 0262; códigos personales: 2 26 62");
        System.out.println("ÁRBOL PERSONAL");
        diagrama(c);
        for (TipoRecorrido t : TipoRecorrido.values()) { System.out.println(t + ": " + secuencia(c,t)); }
        System.out.println("TRAZA PREORDEN");
        System.out.println(c.trazaRecorrido(TipoRecorrido.PREORDEN));
        for (int codigo : new int[]{2,45,60}) {
            System.out.println("Eliminar " + codigo + ": " + c.eliminar(codigo));
            System.out.println("PREORDEN: " + secuencia(c,TipoRecorrido.PREORDEN));
            System.out.println("ABB válido: " + c.esABBValido());
            diagrama(c);
        }
    }

    private static void diagrama(CatalogoABB c) {
        System.out.println("```mermaid\nflowchart TD");
        diagrama(c.getRaiz());
        System.out.println("```");
    }

    private static void diagrama(NodoLibro nodo) {
        if (nodo == null) { return; }
        int codigo = nodo.getDato().getCodigo();
        System.out.println("  n" + codigo + "[\"" + codigo + "\"]");
        if (nodo.getIzquierdo() != null) { System.out.println("  n" + codigo + " -->|I| n" + nodo.getIzquierdo().getDato().getCodigo()); }
        if (nodo.getDerecho() != null) { System.out.println("  n" + codigo + " -->|D| n" + nodo.getDerecho().getDato().getCodigo()); }
        diagrama(nodo.getIzquierdo());
        diagrama(nodo.getDerecho());
    }
}
