import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor esperando conexiones en el puerto 12345...");
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Solicitud recibida: " + inputLine);
                        
                        // Validamos que el mensaje tenga al menos un número de opción y un parámetro
                        String[] parts = inputLine.split(";");
                        if (parts.length < 2) {
                            out.println("Mensaje malformado. Asegúrese de que la solicitud tenga el formato adecuado.");
                            continue; // Continuar esperando una solicitud válida
                        }

                        int option = Integer.parseInt(parts[0]);
                        
                        switch (option) {
                            case 1: // Decimal → Binario
                                if (parts.length < 3) {
                                    out.println("Faltan parámetros. Se requiere el número y la longitud de bits.");
                                    break;
                                }
                                out.println(decimalToBinary(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                                break;
                            case 2: // Binario → Decimal
                                out.println(binaryToDecimal(parts[1]));
                                break;
                            case 3: // Decimal → Hexadecimal
                                if (parts.length < 3) {
                                    out.println("Faltan parámetros. Se requiere el número y la longitud en dígitos hexadecimales.");
                                    break;
                                }
                                out.println(decimalToHex(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                                break;
                            case 4: // Hexadecimal → Decimal
                                out.println(hexToDecimal(parts[1]));
                                break;
                            case 5: // Binario → Hexadecimal
                                if (parts.length < 3) {
                                    out.println("Faltan parámetros. Se requiere el número binario y la longitud en dígitos hexadecimales.");
                                    break;
                                }
                                out.println(binaryToHex(parts[1], Integer.parseInt(parts[2])));
                                break;
                            case 6: // Hexadecimal → Binario
                                out.println(hexToBinary(parts[1]));
                                break;
                            default:
                                out.println("Operación no válida");
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métodos de conversión
    private static String decimalToBinary(int decimal, int length) {
        String binary = Integer.toBinaryString(decimal);
        return String.format("%" + length + "s", binary).replace(' ', '0');
    }

    private static int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }

    private static String decimalToHex(int decimal, int length) {
        String hex = Integer.toHexString(decimal).toUpperCase();
        return String.format("%" + length + "s", hex).replace(' ', '0');
    }

    private static int hexToDecimal(String hex) {
        return Integer.parseInt(hex, 16);
    }

    private static String binaryToHex(String binary, int length) {
        int decimal = binaryToDecimal(binary);
        return decimalToHex(decimal, length);
    }

    private static String hexToBinary(String hex) {
        int decimal = hexToDecimal(hex);
        return decimalToBinary(decimal, 4); // Longitud por defecto 4 bits
    }
}
