package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {

        String result = "{}"; // default return value; replace later!
        JsonObject jsonResult = new JsonObject();
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(new StringReader(csvString)).withCSVParser(csvParser).build();

            List<String> headers = new ArrayList<>();
            List<JsonArray> data = new ArrayList<>();

            String[] csvHeaders = csvReader.readNext();
            if(csvHeaders != null){
                headers = List.of(csvHeaders);
            }
            String[] row;
            while ((row = csvReader.readNext()) != null){
                JsonArray rowData = new JsonArray();
                for(String value : row){
                    try{
                        int intValue = Integer.parseInt(value);
                    }
                    catch (NumberFormatException e) {
                        rowData.add(value);
                    }
                }
                data.add(rowData);
            }

            jsonResult.put("ProdNums", new JsonArray(headers));
            jsonResult.put("ColHeadings", new JsonArray(headers));
            jsonResult.put("Data", new JsonArray(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return jsonResult.toJson();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        StringBuilder csvResult = new StringBuilder();
        
        try {
            
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);

            JsonArray headersArray = (JsonArray) jsonObject.get("ColHeadings");

            List<String> headers = new ArrayList<>();

            for(Object header : headersArray){
                headers.add(header.toString());
            }
            csvResult.append(String.join(",", headers)).append("\n");

            JsonArray dataArray = (JsonArray) jsonObject.get("Data");
            for(Object dataRow : dataArray){
                JsonArray rowArray = (JsonArray) dataRow;
                List<String> row = new ArrayList<>();
                for(Object cell : rowArray){
                    row.add(cell.toString());
                }
                csvResult.append(String.join(",", row)).append("\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return csvResult.toString();
        
    }
    
}
