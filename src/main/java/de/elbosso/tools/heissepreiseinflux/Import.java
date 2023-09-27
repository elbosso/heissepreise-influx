package de.elbosso.tools.heissepreiseinflux;

import de.elbosso.util.io.LineProtocolWriter;
import de.netsysit.ui.dialog.LoginDialog;
import org.json.JSONException;
import org.slf4j.event.Level;
import picocli.CommandLine;

import java.io.IOException;
import java.text.ParseException;

@CommandLine.Command(name = "Import", mixinStandardHelpOptions = true, version = "HeissePreiseInfluxdbInterface 0.1.0",
        description = "Importiert historische Preisdaten von Supermärkten.")
public class Import extends java.lang.Object implements Runnable
{
    private final static org.slf4j.Logger CLASS_LOGGER =org.slf4j.LoggerFactory.getLogger(Import.class);
    private final static org.slf4j.Logger EXCEPTION_LOGGER =org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    @CommandLine.Option(names = {"-S", "--influxDbScheme"}, description = "influxDbScheme (default: ${DEFAULT-VALUE})", defaultValue = "HTTP")
    private HeissePreiseInfluxdbInterface.Scheme influxDbScheme;
    @CommandLine.Option(names = {"-H", "--influxDbHost"}, description = "influxDbHost (default: ${DEFAULT-VALUE})", defaultValue = "localhost")
    private java.lang.String influxDbHost;
    @CommandLine.Option(names = {"-P", "--influxDbPort"}, description = "influxDbPort (default: ${DEFAULT-VALUE})", defaultValue = "8086")
    private int influxDbPort;
    @CommandLine.Option(names = {"-u", "--influxDbUsername"}, description = "influxDbUsername (default: ${DEFAULT-VALUE})", defaultValue = CommandLine.Option.NULL_VALUE)
    private java.lang.String influxDbUsername;
    @CommandLine.Option(names = {"-p", "--influxDbPassword"}, description = "influxDbPassword (default: ${DEFAULT-VALUE})", defaultValue = CommandLine.Option.NULL_VALUE)
    private java.lang.String influxDbPassword;
    @CommandLine.Option(names = {"-d", "--influxDbDatabasename"}, description = "influxDbDatabasename", required = true)
    private java.lang.String influxDbDatabasename;
    //    @CommandLine.Option(names = {"-m", "--mode"}, description = "mode (default: ${DEFAULT-VALUE})", defaultValue = "initialImport")
//    private Mode mode;
    @CommandLine.Option(names = {"-l", "--logLevel"}, description = "logLevel (default: ${DEFAULT-VALUE})", defaultValue = "WARN")
    private Level logLevel;
    @CommandLine.Option(names = {"-a", "-measurementName-"}, description = "measurementName (default: ${DEFAULT-VALUE})", defaultValue = "Product")
    private java.lang.String measurementName;
    @CommandLine.Parameters(index = "0", description = "File containing the historic data in JSON format")
    private java.io.File jsonFile;

    private static java.text.DateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
    private de.elbosso.util.io.InfluxDBHttpInterface influxDBHttpInterface;
    private LineProtocolWriter lineProtocolWriter;
    private long counter;
    @Override
    public void run()
    {
        de.elbosso.util.Utilities.configureBasicStdoutLogging(logLevel);
        CLASS_LOGGER.trace("starting up");
        try
        {
            doInitialImport();
        } catch (Throwable t)
        {
            EXCEPTION_LOGGER.error(t.getMessage(), t);
        }
    }

    private void handle(org.json.JSONObject product) throws JSONException, ParseException, IOException
    {
        org.json.JSONArray pHistory = product.getJSONArray("priceHistory");
        String lineFormat = null;
        for (int i = 0; i < pHistory.length(); ++i)
        {
            org.json.JSONObject price = pHistory.getJSONObject(i);
            java.util.Map<String, String> tags = new java.util.HashMap();
            java.util.Map<String, Object> fields = new java.util.HashMap();
            tags.put("store", product.getString("store"));
            tags.put("name", product.getString("name"));
            tags.put("id", product.getString("id"));
            if (product.has("unit"))
                tags.put("unit", product.getString("unit"));
            try
            {
                if (product.has("quantity"))
                    fields.put("amount", product.getInt("quantity"));
                fields.put("currentPrice", price.getDouble("price"));
                String timestampS = price.getString("date");
                java.util.Date timestamp = df.parse(timestampS);
                lineFormat = lineProtocolWriter.write(timestamp, tags, fields);
                CLASS_LOGGER.debug(lineFormat);
                influxDBHttpInterface.send(lineFormat);
                ++counter;
                if (counter % 100000 == 0)
                    CLASS_LOGGER.info("processed item {}", counter);
            } catch (Throwable t)
            {
                EXCEPTION_LOGGER.warn(t.getMessage() + "\n" + product.toString(2));
            }
        }
    }

    private void doInitialImport() throws JSONException, IOException, ParseException
    {
        CLASS_LOGGER.trace("doInitialImport");
        lineProtocolWriter = new LineProtocolWriter(measurementName);
        LoginDialog.BasicCredentials bc = null;
        if ((influxDbUsername != null) && (influxDbPassword != null))
            bc = new LoginDialog.BasicCredentials(influxDbUsername, influxDbPassword);
        influxDBHttpInterface = new de.elbosso.util.io.InfluxDBHttpInterface(influxDbScheme.name(), influxDbHost, influxDbPort, influxDbDatabasename, bc);
        java.io.FileInputStream fis = new java.io.FileInputStream(jsonFile);
        java.io.InputStreamReader isr = new java.io.InputStreamReader(fis);
        java.io.BufferedReader br = new java.io.BufferedReader(isr);
        org.json.JSONTokener tok = new org.json.JSONTokener(br);
        org.json.JSONArray history = new org.json.JSONArray(tok);
        java.util.List<String> lineFormats = new java.util.LinkedList();
        for (int i = 0; i < history.length(); ++i)
        {
            org.json.JSONObject obj = history.getJSONObject(i);
            if (obj.getString("store").equalsIgnoreCase("penny") == false)
            {
                if (obj.has("priceHistory"))
                {
                    CLASS_LOGGER.debug("{} {}", i, obj.getString("name"));
                    handle(obj);
                }
            }
        }
        influxDBHttpInterface.flush();
    }

}
