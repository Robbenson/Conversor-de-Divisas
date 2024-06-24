import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverter {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    // Mapa estático para almacenar la información de las monedas y los países
    private static final Map<String, String> currencyInfo = new HashMap<>();

    static {
        currencyInfo.put("ARS", "Peso Argentino ");
        currencyInfo.put("USD", "Dolar Estadounidense ");
        currencyInfo.put("CLP", "Peso Chileno ");
        currencyInfo.put("BRL", "Real Brasil ");
        currencyInfo.put("COP", "Peso Colombiano ");
        currencyInfo.put("PEN", "Nuevo Sol ");
        currencyInfo.put("MXN", "Peso Mexicano");
        currencyInfo.put("EUR", "Euro");
        // Añadir más monedas y países según sea necesario
    }

    public static HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }

    public static String getResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static JsonObject parseJson(String jsonResponse) {
        return gson.fromJson(jsonResponse, JsonObject.class);
    }

    public static double getExchangeRate(JsonObject jsonObject, String currencyCode) {
        JsonElement rate = jsonObject.get("conversion_rates").getAsJsonObject().get(currencyCode);
        return rate != null ? rate.getAsDouble() : 0.0;
    }

    public static double convertCurrency(double amount, double rateFrom, double rateTo) {
        return amount * (rateTo / rateFrom);
    }

    public static void showCurrencyInfo() {
        System.out.println("Codigos Pais: \t ");//
        for (Map.Entry<String, String> entry : currencyInfo.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Bienvenido al Conversor de Divisas");
            showCurrencyInfo();  // Mostrar la información de las monedas y los países

            System.out.println("\n 1.- Ingrese el monto a convertir:");
            double amount = scanner.nextDouble();

            System.out.println(" 2.-Ingrese el código de la moneda de origen (ej. USD):");
            String fromCurrency = scanner.next().toUpperCase();

            System.out.println(" 3.-Ingrese el código de la moneda de destino (ej. EUR):");
            String toCurrency = scanner.next().toUpperCase();


            String apiKey = "8042e1fb0d06981217968210"; // Reemplaza con tu clave de API real
            String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + fromCurrency;
            HttpRequest request = createRequest(url);
            String jsonResponse = getResponse(request);
            JsonObject jsonObject = parseJson(jsonResponse);

            double rateTo = getExchangeRate(jsonObject, toCurrency);
            double convertedAmount = convertCurrency(amount, 1, rateTo); // El valor base es 1 ya que estamos usando USD

            System.out.printf("Monto convertido: %.2f %s%n", convertedAmount, toCurrency);

            System.out.println("¿Desea realizar otra conversión? (S/N):");
            String continueOption = scanner.next().toUpperCase();

            if (!continueOption.equals("S")) {
                System.out.println("Gracias por usar el conversor de monedas.");
                break;
            }
        }

        scanner.close();
    }
}

