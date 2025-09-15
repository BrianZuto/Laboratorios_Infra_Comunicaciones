import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Conectado al servidor.");
            
            while (true) {
                System.out.println("Selecciona una opción de conversión:");
                System.out.println("1) Decimal → Binario");
                System.out.println("2) Binario → Decimal");
                System.out.println("3) Decimal → Hexadecimal");
                System.out.println("4) Hexadecimal → Decimal");
                System.out.println("5) Binario → Hexadecimal");
                System.out.println("6) Hexadecimal → Binario");
                System.out.println("7) Salir");
                System.out.print("Opción: ");
                
                String option = inputReader.readLine().trim();
                
                if ("7".equals(option)) {
                    break;
                }

                String request = option; // Número de la operación
                
                if (option.equals("1") || option.equals("3")) { // Decimal → Binario o Decimal → Hexadecimal
                    System.out.print("Introduce el número a convertir: ");
                    String number = inputReader.readLine().trim();
                    request += ";" + number;  // Añadir número
                    
                    if (option.equals("1")) { // Decimal → Binario
                        System.out.print("Introduce la longitud de los bits: ");
                        String length = inputReader.readLine().trim();
                        request += ";" + length;  // Añadir longitud
                    } else if (option.equals("3")) { // Decimal → Hexadecimal
                        System.out.print("Introduce la longitud de los dígitos hexadecimales: ");
                        String length = inputReader.readLine().trim();
                        request += ";" + length;  // Añadir longitud
                    }
                } else if (option.equals("2") || option.equals("5") || option.equals("6")) { // Binario → Decimal, Binario → Hexadecimal, Hexadecimal → Binario
                    System.out.print("Introduce el número a convertir: ");
                    String number = inputReader.readLine().trim();
                    request += ";" + number;  // Añadir número
                } else if (option.equals("4")) { // Hexadecimal → Decimal
                    System.out.print("Introduce el número a convertir: ");
                    String number = inputReader.readLine().trim();
                    request += ";" + number;  // Añadir número
                }
                
                // Enviar la solicitud al servidor
                out.println(request);
                
                // Recibir la respuesta del servidor
                String response = in.readLine();
                System.out.println("Respuesta del servidor: " + response);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
