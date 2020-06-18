import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SheetsAndJava {
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Google sheets texas";
    private static String Spreadsheet_ID = "1ISJKQ0G1GPphdE5HJPxTCGmdwSdYnZXOzeVTKfErPtM";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static Credential authorize() throws IOException, GeneralSecurityException {


        InputStream in = SheetsAndJava.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        ;
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), clientSecrets, scopes).setDataStoreFactory(new FileDataStoreFactory(
                new java.io.File("tokens"))).setAccessType("offline").build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

   /* public static void main(String[] args) throws IOException, GeneralSecurityException {
        JFrame frame = new POS("Texas point of sales");
        frame.setVisible(true);
        SheetsAndJava sj = new SheetsAndJava();
        sj.FindRow("000003",10);
        sj.updateTransactions("000003",10.5,50.00,"Hussan","cash");
    } */
    public void FindRow(String material_id,double amount) throws IOException, GeneralSecurityException
    {
        sheetsService = getSheetsService();
        String range = "Rolls!A2:C";
        ValueRange resposnse = sheetsService.spreadsheets().values().get(Spreadsheet_ID,range).execute();

        List<List<Object>> values = resposnse.getValues();
        int i =2;
        for(List row : values)
        {
            if (row.get(0).equals(material_id))
            {
                ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(Double.parseDouble(row.get(2) + "") - amount )));
                UpdateValuesResponse update = sheetsService.spreadsheets().values().update(Spreadsheet_ID
                ,"Rolls!C"+i,body).setValueInputOption("USER_ENTERED")
                        .setIncludeValuesInResponse(false)
                        .execute();


            }
            else{
                i++;
            }
        }

    }
    public void updateTransactions(String materialID, Double length, Double sellingPrice,String customer, String paymentType) throws IOException, GeneralSecurityException
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        ValueRange appendTrans = new ValueRange().setValues(Arrays.asList(
                Arrays.asList(materialID,length,formatter.format(date),sellingPrice,customer,paymentType)
        ));
                AppendValuesResponse appebdResult = sheetsService.spreadsheets().values().append(Spreadsheet_ID
                ,"Transaction wholesale",appendTrans)
                        .setValueInputOption("USER_ENTERED")
                        .setInsertDataOption("INSERT_ROWS")
                        .setIncludeValuesInResponse(true)
                        .execute();
    }


}