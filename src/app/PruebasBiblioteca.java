package app;

import enums.*;
import modelo.*;
import negocio.*;
import java.util.*;

/** Once escenarios obligatorios y ocho grupos adicionales con expectativas independientes. */
public final class PruebasBiblioteca {
    static int casos;
    static int extras;
    static int fallos;

    static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
    static Libro libro(int codigo) { return new Libro(codigo, "Libro " + codigo, "Autor", 2026); }
    static UnidadBiblioteca unidad(String codigo, TipoUnidad tipo) {
        return new UnidadBiblioteca(codigo, codigo, tipo);
    }
    static CatalogoABB catalogo(int... codigos) {
        CatalogoABB c = new CatalogoABB();
        for (int codigo : codigos) exigir(c.insertar(libro(codigo)), "Carga " + codigo);
        return c;
    }
    static CatalogoABB sw() { return catalogo(60, 35, 80, 20, 45, 70, 90, 40, 75); }
    static List<Integer> codigos(CatalogoABB c, TipoRecorrido tipo) {
        List<Integer> resultado = new ArrayList<>();
        for (Libro l : c.recorrer(tipo)) resultado.add(l.getCodigo());
        return resultado;
    }
    static void secuencia(CatalogoABB c, TipoRecorrido tipo, Integer... esperados) {
        exigir(codigos(c, tipo).equals(Arrays.asList(esperados)), "Recorrido " + tipo);
    }
    static void invalido(Runnable operacion) {
        try { operacion.run(); } catch (IllegalArgumentException e) { return; }
        throw new AssertionError("Falta IllegalArgumentException");
    }
    static void caso(String nombre, Runnable prueba) {
        casos++;
        try { prueba.run(); System.out.println("OK " + casos + ": " + nombre); }
        catch (AssertionError | RuntimeException e) { fallos++; System.out.println("FALLO " + casos + ": " + nombre + " - " + e.getMessage()); }
    }
    static void extra(String nombre, Runnable prueba) {
        extras++;
        try { prueba.run(); System.out.println("EXTRA OK: " + nombre); }
        catch (AssertionError | RuntimeException e) { fallos++; System.out.println("EXTRA FALLO: " + nombre + " - " + e.getMessage()); }
    }
    static ArbolBiblioteca inicial() {
        return DatosPrueba.crearBiblioteca();
    }

    public static void main(String[] args) {
        casos = 0; extras = 0; fallos = 0;
        caso("Solo raiz", () -> {
            ArbolBiblioteca a = new ArbolBiblioteca();
            exigir(a.altura() == 0 && a.contarHojas() == 1, "Raiz");
        });
        caso("Estructura inicial", () -> {
            ArbolBiblioteca a = inicial();
            exigir(a.altura() == 2 && a.contarHojas() == 4 && a.contarLibrosTotal() == 13, "Inicial");
        });
        caso("Niveles", () -> {
            ArbolBiblioteca a = inicial();
            exigir(a.nivel("BIB") == 0 && a.nivel("sw") == 1 && a.nivel("SW-E2") == 2
                    && a.nivel("XYZ") == -1, "Niveles");
        });
        caso("Relacion invalida", () -> exigir(!inicial().agregar("BIB", unidad("X", TipoUnidad.ESTANTE)), "Relacion"));
        caso("Tres recorridos", () -> {
            CatalogoABB c = sw();
            secuencia(c, TipoRecorrido.PREORDEN, 60, 35, 20, 45, 40, 80, 70, 75, 90);
            secuencia(c, TipoRecorrido.INORDEN, 20, 35, 40, 45, 60, 70, 75, 80, 90);
            secuencia(c, TipoRecorrido.POSORDEN, 20, 40, 45, 35, 75, 70, 90, 80, 60);
        });
        caso("Duplicado", () -> {
            CatalogoABB c = sw(); Libro original = c.buscar(60);
            exigir(!c.insertar(libro(60)) && c.contar() == 9 && c.buscar(60) == original, "Duplicado");
        });
        caso("Camino y rango", () -> {
            CatalogoABB c = sw();
            exigir(c.caminoBusqueda(42).equals(Arrays.asList(60,35,45,40)), "Camino");
            exigir(c.contarEnRango(40,75) == 5, "Rango");
        });
        caso("Insertar 42 y tres eliminaciones", () -> {
            CatalogoABB c = sw();
            exigir(c.insertar(libro(42)) && c.eliminar(20) && c.eliminar(70) && c.eliminar(60), "Eliminar");
            secuencia(c, TipoRecorrido.PREORDEN, 75,35,45,40,42,80,90);
            exigir(c.esABBValido(), "Validez final");
        });
        caso("Codigo inexistente", () -> {
            CatalogoABB c = sw();
            exigir(c.buscar(999) == null && !c.modificar(999,"Otro","Otro",2026) && !c.eliminar(999), "Ausente");
        });
        caso("Libro invalido", () -> {
            invalido(() -> libro(0)); invalido(() -> libro(-1));
            invalido(() -> new Libro(1,null,"Autor",2000));
            invalido(() -> new Libro(1,"  ","Autor",2000));
            invalido(() -> new Libro(1,"Titulo",null,2000));
            invalido(() -> new Libro(1,"Titulo"," ",2000));
            invalido(() -> new Libro(1,"Titulo","Autor",1899));
            invalido(() -> new Libro(1,"Titulo","Autor",2027));
        });
        caso("Nieto mal ubicado", () -> {
            CatalogoABB c = catalogo(60,35);
            c.getRaiz().getIzquierdo().setDerecho(new NodoLibro(libro(70)));
            exigir(!c.esABBValido(), "Limite del abuelo");
        });
        System.out.println((casos - fallos) + "/11 escenarios obligatorios OK");

        extra("Traza literal para los tres ordenes", () -> {
            CatalogoABB c = catalogo(2,1,3);
            String pre = "entra 2\nVISITA 2\n  entra 1\n  VISITA 1\n    null -> regresa\n    null -> regresa\n  entra 3\n  VISITA 3\n    null -> regresa\n    null -> regresa\nSecuencia PREORDEN: 2 1 3";
            String in = "entra 2\n  entra 1\n    null -> regresa\n  VISITA 1\n    null -> regresa\nVISITA 2\n  entra 3\n    null -> regresa\n  VISITA 3\n    null -> regresa\nSecuencia INORDEN: 1 2 3";
            String pos = "entra 2\n  entra 1\n    null -> regresa\n    null -> regresa\n  VISITA 1\n  entra 3\n    null -> regresa\n    null -> regresa\n  VISITA 3\nVISITA 2\nSecuencia POSORDEN: 1 3 2";
            exigir(c.trazaRecorrido(TipoRecorrido.PREORDEN).equals(pre), "Traza PRE");
            exigir(c.trazaRecorrido(TipoRecorrido.INORDEN).equals(in), "Traza IN");
            exigir(c.trazaRecorrido(TipoRecorrido.POSORDEN).equals(pos), "Traza POS");
        });
        extra("Catalogo vacio y entradas nulas", () -> {
            CatalogoABB c = new CatalogoABB();
            exigir(c.contar() == 0 && c.buscar(1) == null && !c.eliminar(1)
                    && c.esABBValido() && c.caminoBusqueda(1).isEmpty()
                    && c.recorrer(TipoRecorrido.INORDEN).isEmpty() && c.contarEnRango(1,9) == 0, "Vacio");
            exigir(c.trazaRecorrido(TipoRecorrido.PREORDEN).equals("null -> regresa\nSecuencia PREORDEN:"), "Traza vacia");
            invalido(() -> c.insertar(null)); invalido(() -> c.recorrer(null));
            invalido(() -> c.trazaRecorrido(null));
        });
        extra("Reconstruccion valida e invalida", () -> {
            CatalogoABB c = CatalogoABB.desdePreorden(new int[]{60,35,20,45,40,80,70,75,90});
            exigir(c.esABBValido() && c.contar() == 9, "Reconstruccion");
            secuencia(c, TipoRecorrido.PREORDEN,60,35,20,45,40,80,70,75,90);
            exigir(CatalogoABB.desdePreorden(new int[0]).contar() == 0, "Pre vacio");
            invalido(() -> CatalogoABB.desdePreorden(null));
            for (int[] p : new int[][]{{0},{-1},{2,2},{10,5,12,4},{60,35,80,20}}) {
                invalido(() -> CatalogoABB.desdePreorden(p));
            }
            CatalogoABB limite = CatalogoABB.desdePreorden(new int[]{Integer.MAX_VALUE,1});
            exigir(limite.esABBValido() && limite.contar() == 2, "Limite entero");
        });
        extra("Modificacion valida y atomica", () -> {
            CatalogoABB c = sw(); Libro l = c.buscar(35);
            exigir(c.modificar(35," Nuevo "," Autor nuevo ",1900), "Modificar");
            exigir(l == c.buscar(35) && l.getTitulo().equals("Nuevo") && l.getAnio() == 1900, "Identidad");
            invalido(() -> c.modificar(35,"No guardar","Autor",2027));
            exigir(l.getTitulo().equals("Nuevo") && l.getAutor().equals("Autor nuevo") && l.getAnio() == 1900, "Atomicidad");
        });
        extra("Eliminar raiz y sucesor con hijo derecho", () -> {
            CatalogoABB c = catalogo(5); exigir(c.eliminar(5) && c.getRaiz() == null, "Raiz hoja");
            c = catalogo(5,3); exigir(c.eliminar(5) && c.getRaiz().getDato().getCodigo() == 3, "Hijo izquierdo");
            c = catalogo(5,7); exigir(c.eliminar(5) && c.getRaiz().getDato().getCodigo() == 7, "Hijo derecho");
            c = catalogo(50,30,80,60,65,90); exigir(c.eliminar(50) && c.esABBValido(), "Sucesor");
            secuencia(c,TipoRecorrido.PREORDEN,60,30,80,65,90);
        });
        extra("Reglas, unicidad y jerarquia", () -> {
            ArbolBiblioteca a = inicial();
            exigir(!a.agregar("TI",unidad("sw-e1",TipoUnidad.ESTANTE)), "Duplicado global");
            exigir(!a.agregar("XYZ",unidad("E",TipoUnidad.ESTANTE)), "Padre ausente");
            exigir(!a.agregar("SW-E1",unidad("A",TipoUnidad.AREA)), "Hijo estante");
            exigir(!a.agregar(null,unidad("A",TipoUnidad.AREA)) && !a.agregar("BIB",null), "Nulos");
            exigir(a.buscar(null) == null && a.nivel(null) == -1 && a.buscar(" sw ") != null, "Busqueda");
            exigir(a.buscar("SW").getCatalogo() == null && a.buscar("BIB").getCatalogo() == null, "Catalogos");
            exigir(a.mostrarJerarquia().equals("BIB Biblioteca FISEI\n  SW Software\n    SW-E1 Estante 1 de Software\n    SW-E2 Estante 2 de Software\n  TI Tecnologías de la Información\n    TI-E1 Estante 1 de TI\n  TEL Telecomunicaciones\n"), "Jerarquia exacta");
            invalido(() -> new ArbolBiblioteca(unidad("A",TipoUnidad.AREA)));
            invalido(() -> new UnidadBiblioteca(" ","Nombre",TipoUnidad.AREA));
            invalido(() -> new UnidadBiblioteca("A"," ",TipoUnidad.AREA));
            invalido(() -> new UnidadBiblioteca("A","Nombre",null));
            for (TipoUnidad p : TipoUnidad.values()) for (TipoUnidad h : TipoUnidad.values())
                exigir(p.puedeTenerHijo(h) == ((p == TipoUnidad.BIBLIOTECA && h == TipoUnidad.AREA)
                        || (p == TipoUnidad.AREA && h == TipoUnidad.ESTANTE)), "Tipos");
        });
        extra("Rangos extremos y duplicado manual", () -> {
            CatalogoABB c = sw();
            exigir(c.contarEnRango(75,40) == 0 && c.contarEnRango(40,40) == 1
                    && c.contarEnRango(41,41) == 0 && c.contarEnRango(Integer.MIN_VALUE,Integer.MAX_VALUE) == 9, "Rangos");
            c.getRaiz().setDerecho(new NodoLibro(libro(60)));
            exigir(!c.esABBValido(), "Duplicado manual");
        });
        extra("Operaciones aleatorias contra modelo independiente", () -> {
            Random r = new Random(262);
            for (int ensayo=0; ensayo<50; ensayo++) {
                CatalogoABB c = new CatalogoABB(); boolean[] presente = new boolean[101];
                for (int paso=0; paso<200; paso++) {
                    int k=1+r.nextInt(100);
                    if (r.nextBoolean()) { exigir(c.insertar(libro(k)) == !presente[k], "Insert aleatorio"); presente[k]=true; }
                    else { exigir(c.eliminar(k) == presente[k], "Delete aleatorio"); presente[k]=false; }
                    List<Integer> esperado = new ArrayList<>();
                    for (int i=1;i<=100;i++) if(presente[i]) esperado.add(i);
                    exigir(c.esABBValido() && c.contar()==esperado.size()
                            && codigos(c,TipoRecorrido.INORDEN).equals(esperado), "Estado aleatorio");
                    int a=1+r.nextInt(100), b=1+r.nextInt(100), cantidad=0;
                    for(int i=a;i<=b;i++) if(presente[i]) cantidad++;
                    exigir(c.contarEnRango(a,b)==cantidad, "Rango aleatorio");
                }
                List<Integer> p = codigos(c,TipoRecorrido.PREORDEN); int[] valores=new int[p.size()];
                for(int i=0;i<valores.length;i++) valores[i]=p.get(i);
                CatalogoABB copia=CatalogoABB.desdePreorden(valores);
                for(TipoRecorrido t:TipoRecorrido.values()) exigir(codigos(c,t).equals(codigos(copia,t)), "Copia aleatoria");
            }
        });
        System.out.println((casos + extras - fallos) + "/" + (casos + extras) + " pruebas OK");
        if (fallos > 0) { throw new AssertionError("Pruebas fallidas: " + fallos); }
    }
}
