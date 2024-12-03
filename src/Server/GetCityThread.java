package Server;

import ModelClass.FileJsonOperations;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

/**
 * Classe responsável por obter a cidade na qual foi reportado um incidente
 */
public class GetCityThread extends Thread {

    private FileJsonOperations file;
    private String latitude, longitude;

    public GetCityThread(FileJsonOperations file, String latitude, String longitude) {
        this.file = file;
        this.latitude = longitude;
        this.longitude = latitude;
    }

    @Override
    public void run() {

        String command = "curl -i -H   \"api-key:  aQQxmQ4fR65lf4DVDKZzp7lV1dAwRnX5\" -X GET \"https://api.codezap.io/v1/reverse?lat=" + longitude + "&lng=" + latitude + "\"";
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            String s = null;
            String json = null;
            while ((s = stdInput.readLine()) != null) {
                json = s;
            }
            JSONParser jsonParser = new JSONParser();
            JSONObject json2 = new JSONObject();
            JSONObject json3 = new JSONObject();
            try {
                json2 = (JSONObject) jsonParser.parse(json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            json3 = (JSONObject) json2.get("address");
            LocalDate myObj = LocalDate.now();
            file.writeIncident(json3.get("county").toString(), myObj.toString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}
