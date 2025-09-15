import java.math.BigInteger;
import java.util.Locale;
import java.util.Scanner;


public class Ejercicio1 {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarTitulo();
        while (true) {
            switch (menu()) {
                case 1 -> convertirEntreBases();
                case 2 -> convertirConPrefijo();
                case 3 -> mostrarTabla();
                case 4 -> {
                    System.out.println("\n¡Hasta luego!");
                    return;
                }
                default -> System.out.println("\nOpción no válida. Intenta de nuevo.\n");
            }
        }
    }

    private static void mostrarTitulo() {
        System.out.println("===============================================");
        System.out.println("   CONVERSOR DE SISTEMAS NUMÉRICOS EN JAVA");
        System.out.println("===============================================\n");
    }

    private static int menu() {
        System.out.println("Selecciona una opción:");
        System.out.println("  1) Convertir entre dos bases (2..36)");
        System.out.println("  2) Convertir detectando prefijo (0b, 0o, 0x, decimal)");
        System.out.println("  3) Ver tabla de representaciones (bin/oct/dec/hex)");
        System.out.println("  4) Salir");
        System.out.print("Opción: ");
        String line = in.nextLine().trim();
        System.out.println();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // --------- Opción 1: Conversión arbitraria entre bases ---------
    private static void convertirEntreBases() {
        int baseOrigen = leerBase("base de origen (2..36)");
        int baseDestino = leerBase("base de destino (2..36)");
        String numero = leerNumero("número en base " + baseOrigen + " (se permiten signos +/-, sin prefijos)");

        if (!valida(numero, baseOrigen)) {
            System.out.println("Entrada inválida para la base especificada.\n");
            return;
        }

        try {
            String convertido = convertir(numero, baseOrigen, baseDestino);
            System.out.printf("%s (base %d) = %s (base %d)%n", numero, baseOrigen, convertido, baseDestino);

            Integer bits = leerBitsOpcional();
            if (bits != null && bits > 0) {
                BigInteger valor = new BigInteger(normalizarSigno(numero), baseOrigen);
                String conBits = formatear(valor, baseDestino, bits);
                System.out.printf("Con longitud de %d bits -> %s (base %d)%n", bits, conBits, baseDestino);
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error al convertir: " + e.getMessage() + "\n");
        }
    }

    // --------- Opción 2: Detección por prefijo ---------
    private static void convertirConPrefijo() {
        System.out.println("Introduce un número con prefijo (0b, 0o, 0x) o sin prefijo (decimal). Ejemplos:");
        System.out.println("  0b101101, 0o755, 0x1A3F, 2024, -0xFF, +42\n");
        System.out.print("Número: ");
        String entrada = in.nextLine().trim();
        if (entrada.isEmpty()) {
            System.out.println("\nEntrada vacía.\n");
            return;
        }

        try {
            Detectado det = detectarPrefijo(entrada);
            if (!valida(det.valor, det.base)) {
                System.out.println("Entrada inválida para la base detectada.\n");
                return;
            }

            Integer bits = leerBitsOpcional();
            imprimirTabla(det.valor, det.base, bits);
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }

    // --------- Opción 3: Tabla de representaciones ---------
    private static void mostrarTabla() {
        int baseOrigen = leerBase("base de origen (2..36)");
        String numero = leerNumero("número en base " + baseOrigen);

        if (!valida(numero, baseOrigen)) {
            System.out.println("Entrada inválida para la base especificada.\n");
            return;
        }
        Integer bits = leerBitsOpcional();
        imprimirTabla(numero, baseOrigen, bits);
        System.out.println();
    }

    // --------- Utilidades ---------
    private static record Detectado(String valor, int base) {
    }

    private static Detectado detectarPrefijo(String s) {
        s = s.replace(" ", "");
        if (s.isEmpty())
            throw new IllegalArgumentException("Cadena vacía");

        char sign = '+';
        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
            sign = s.charAt(0);
            s = s.substring(1);
        }

        String lower = s.toLowerCase(Locale.ROOT);
        int base;
        if (lower.startsWith("0b")) {
            base = 2;
            s = s.substring(2);
        } else if (lower.startsWith("0o")) {
            base = 8;
            s = s.substring(2);
        } else if (lower.startsWith("0x")) {
            base = 16;
            s = s.substring(2);
        } else {
            base = 10;
        }

        if (sign == '-')
            s = "-" + s;
        else if (sign == '+')
            s = "+" + s;
        return new Detectado(s, base);
    }

    private static void imprimirTabla(String numero, int baseOrigen) {
        imprimirTabla(numero, baseOrigen, null);
    }

    private static void imprimirTabla(String numero, int baseOrigen, Integer bits) {
        BigInteger valor = new BigInteger(normalizarSigno(numero), baseOrigen);
        System.out.println("Representaciones equivalentes:\n");
        System.out.printf("  Binario   (2) : %s%n", formatear(valor, 2, bits));
        System.out.printf("  Octal     (8) : %s%n", formatear(valor, 8, bits));
        System.out.printf("  Decimal  (10) : %s%n", formatear(valor, 10, bits));
        System.out.printf("  Hex      (16) : %s%n", formatear(valor, 16, bits).toUpperCase(Locale.ROOT));
    }

    private static String convertir(String numero, int baseOrigen, int baseDestino) {
        BigInteger valor = new BigInteger(normalizarSigno(numero), baseOrigen);
        return aBase(valor, baseDestino);
    }

    private static String aBase(BigInteger valor, int baseDestino) {
        boolean negativo = valor.signum() < 0;
        String mag = valor.abs().toString(baseDestino);
        return (negativo ? "-" : "") + mag.toUpperCase(Locale.ROOT);
    }

    private static String formatear(BigInteger valor, int baseDestino, Integer bits) {
        if (bits == null || bits <= 0) {
            return aBase(valor, baseDestino);
        }
        int requiredBits = valor.abs().equals(BigInteger.ZERO) ? 1 : valor.abs().bitLength();
        if (requiredBits > bits) {
            // No cabe en el ancho solicitado: informamos sin rellenar para evitar confusión
            return aBase(valor, baseDestino) + " [excede " + bits + " bits]";
        }
        boolean negativo = valor.signum() < 0;
        String mag = valor.abs().toString(baseDestino).toUpperCase(Locale.ROOT);

        int digitosNecesarios = digitosParaBits(baseDestino, bits);
        if (mag.length() < digitosNecesarios) {
            mag = "0".repeat(digitosNecesarios - mag.length()) + mag;
        }
        return (negativo ? "-" : "") + mag;
    }

    private static String normalizarSigno(String s) {
        if (s.startsWith("+"))
            return s.substring(1);
        return s;
    }

    private static int digitosParaBits(int base, int bits) {
        if (base == 2)  return (int) Math.ceil(bits / 1.0);
        if (base == 8)  return (int) Math.ceil(bits / 3.0);
        if (base == 16) return (int) Math.ceil(bits / 4.0);
        double log2b = Math.log(base) / Math.log(2);
        return (int) Math.ceil(bits / log2b);
    }

    private static int leerBase(String etiqueta) {
        while (true) {
            System.out.print("Ingresa la " + etiqueta + ": ");
            String line = in.nextLine().trim();
            try {
                int base = Integer.parseInt(line);
                if (base < 2 || base > 36)
                    throw new NumberFormatException();
                return base;
            } catch (NumberFormatException e) {
                System.out.println("Base inválida. Debe ser un entero entre 2 y 36.\n");
            }
        }
    }

    private static String leerNumero(String etiqueta) {
        System.out.print("Ingresa el " + etiqueta + ": ");
        return in.nextLine().trim();
    }

    private static Integer leerBitsOpcional() {
        System.out.print("Longitud en bits (opcional, presiona Enter para omitir): ");
        String s = in.nextLine().trim();
        if (s.isEmpty()) return null;
        try {
            int b = Integer.parseInt(s);
            if (b <= 0) throw new NumberFormatException();
            return b;
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido de bits. Se ignorará el formato fijo.\n");
            return null;
        }
    }

    private static boolean valida(String s, int base) {
        if (s == null || s.isEmpty())
            return false;
        int i = 0;
        if (s.charAt(0) == '+' || s.charAt(0) == '-') {
            if (s.length() == 1)
                return false;
            i = 1;
        }
        String digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (; i < s.length(); i++) {
            char c = Character.toUpperCase(s.charAt(i));
            int val = digits.indexOf(c);
            if (val < 0 || val >= base)
                return false;
        }
        return true;
    }
}
