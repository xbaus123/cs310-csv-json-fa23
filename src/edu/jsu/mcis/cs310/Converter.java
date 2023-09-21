package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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



    public static String csvToJson(String csvString) {
        JsonObject jsonResult = new JsonObject();

        try {
            // Create a CSV parser and reader to process the input CSV string
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(new StringReader(csvString)).withCSVParser(csvParser).build();

            List<String> colHeaders = new ArrayList<>(); // List to store column headers
            List<JsonArray> data = new ArrayList<>(); // List to store data rows
            List<String> prodNums = new ArrayList<>(); // List to store ProdNum values

            // Read the first row of CSV as column headers
            String[] csvHeaders = csvReader.readNext();
            if (csvHeaders != null) {
                colHeaders = Arrays.asList(csvHeaders);
            }

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                JsonArray rowData = new JsonArray();
                prodNums.add(row[0]); // Extract and store the first value as ProdNum

                for (int i = 0; i < row.length; i++) {
                    String value = row[i];
                    try {
                        int intValue = Integer.parseInt(value);
                        rowData.add(intValue);
                    } catch (NumberFormatException e) {
                        rowData.add(value);
                    }
                }
                data.add(rowData); // Add the processed row data to the list
            }

            // Create a JSON object with the collected data
            jsonResult.put("ProdNums", new JsonArray(prodNums));
            jsonResult.put("ColHeadings", new JsonArray(colHeaders));
            jsonResult.put("Data", new JsonArray(data));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult.toJson(); // Convert the JSON object to a string and return it
    }






    public static String jsonToCsv(String jsonString) {
        StringBuilder csvResult = new StringBuilder();

        try {
            // Deserialize the input JSON string
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);

            // Extract column headers from the JSON
            JsonArray headersArray = (JsonArray) jsonObject.get("ColHeadings");
            List<String> headers = new ArrayList<>();
            for (Object header : headersArray) {
                headers.add(header.toString());
            }

            // Build the CSV header row
            csvResult.append("\"").append(String.join("\",\"", headers)).append("\"\n");

            // Extract data and ProdNums from the JSON
            JsonArray dataArray = (JsonArray) jsonObject.get("Data");
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");

            for (int i = 0; i < dataArray.size(); i++) {
                JsonArray rowArray = (JsonArray) dataArray.get(i);
                List<String> row = new ArrayList<>();

                // Add ProdNum as the first column in each row
                row.add("\"" + prodNums.get(i).toString() + "\"");

                // Add cell values to the row
                for (Object cell : rowArray) {
                    row.add("\"" + cell.toString() + "\"");
                }

                // Append the row to the CSV result
                csvResult.append(String.join(",", row)).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return csvResult.toString(); // Return the CSV result as a string
    }



}
