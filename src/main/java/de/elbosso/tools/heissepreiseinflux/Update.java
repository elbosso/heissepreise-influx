package de.elbosso.tools.heissepreiseinflux;

import de.elbosso.util.io.LineProtocolWriter;
import de.elbosso.util.lowlevel.ProcessCapture;
import de.netsysit.ui.dialog.LoginDialog;
import org.json.JSONException;
import org.slf4j.event.Level;
import picocli.CommandLine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Locale;

@CommandLine.Command(name = "Update", mixinStandardHelpOptions = true, version = "HeissePreiseInfluxdbInterface 0.1.0",
        description = "Aktualisiert Preisdaten von Supermärkten.")
public class Update extends java.lang.Object implements java.lang.Runnable
{
    private final static org.slf4j.Logger CLASS_LOGGER =org.slf4j.LoggerFactory.getLogger(Update.class);
    private final static org.slf4j.Logger EXCEPTION_LOGGER =org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    @CommandLine.Option(names = {"-s", "--storeNumber"}, description = "storeNumber (default: ${DEFAULT-VALUE})", defaultValue = "440405")
    private String storeNumber;
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

    private de.elbosso.util.io.InfluxDBHttpInterface influxDBHttpInterface;
    private LineProtocolWriter lineProtocolWriter;
    private long counter;
    private static java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(Locale.GERMAN);
    private static java.util.regex.Pattern fullPattern = java.util.regex.Pattern.compile("(\\d+)\\s*?(\\S+)\\s*?.*?\\((.*?)=(.*?)\\)");
    private static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*?(\\S+)\\s*?.*?");

    public void run()
    {
        de.elbosso.util.Utilities.configureBasicStdoutLogging(logLevel);
        CLASS_LOGGER.trace("starting up");
        try
        {
            doUpdate();
        } catch (Throwable t)
        {
            EXCEPTION_LOGGER.error(t.getMessage(), t);
        }
    }
    private java.net.URL makeUrl(int pageNumber,java.lang.String marketCode) throws MalformedURLException
    {
        java.lang.String serviceTypes="PICKUP";
        java.lang.String searchTerm="*";
        int perPage=250;
        java.lang.String endpoint="https://mobile-api.rewe.de/api/v3/product-search?searchTerm={0}&page={1}&sorting=RELEVANCE_DESC&objectsPerPage={2}&marketCode={3}&serviceTypes={4}";
        java.lang.String request=java.text.MessageFormat.format(endpoint,searchTerm,pageNumber,perPage,marketCode,serviceTypes);
        CLASS_LOGGER.debug(request);
        return new java.net.URL(request);
    }
    private java.lang.String getPageUsingCurl(int pageNumber) throws IOException, InterruptedException
    {
        java.lang.String marketCode=storeNumber;
        java.lang.String serviceTypes="PICKUP";
        java.lang.String[] commands=new java.lang.String[]{
                "curl",
                makeUrl(pageNumber,marketCode).toString(),
                "-H",
                "Rd-Service-Types: "+serviceTypes,
                "-H",
                "Rd-Market-Id: "+marketCode,
        };
        ProcessCapture processCapture=new ProcessCapture(commands);
        java.lang.Thread t=new java.lang.Thread(processCapture);
        t.start();
        t.join();
        java.lang.String jsonAsString=processCapture.getStdout();
        return jsonAsString;
    }
    private void handle(org.json.JSONObject json, java.lang.String store) throws JSONException, ParseException, IOException
    {
        org.json.JSONArray products = json.getJSONArray("products");
        CLASS_LOGGER.debug("products {}", products.length());
        java.util.Map<String, String> tags = null;
        java.util.Map<String, Object> fields = null;
        for (int i = 0; i < products.length(); ++i)
        {
            tags = new java.util.HashMap();
            fields = new java.util.HashMap();
            org.json.JSONObject product = products.getJSONObject(i);
            tags.put("store", store);
            tags.put("hasBioCide", java.lang.Boolean.toString(product.getBoolean("hasBioCide")));
            tags.put("name", product.getString("name"));
            tags.put("id", product.getString("id"));
            tags.put("listingId", product.getString("listingId"));
            if (product.has("tags"))
            {
                org.json.JSONArray jtags = product.getJSONArray("tags");
                for (int j = 0; j < jtags.length(); ++j)
                {
                    tags.put(jtags.getString(j), "true");
                }
            }
            java.lang.String currentPrice = product.getString("currentPrice");
            currentPrice = currentPrice.substring(0, currentPrice.indexOf(' '));
            CLASS_LOGGER.trace("{} {}", currentPrice, nf.parse(currentPrice));
            fields.put("currentPrice", nf.parse(currentPrice));
            if(product.has("grammage"))
            {
                java.lang.String input = product.getString("grammage");
                java.util.regex.Matcher matcher = fullPattern.matcher(input);
                if (matcher.matches())
                {
                    CLASS_LOGGER.trace(input);
                    tags.put("amount", matcher.group(1));
                    tags.put("unit", matcher.group(2));
                    tags.put("relativeAmount", matcher.group(3));
                    java.lang.String relativePrice = matcher.group(4).trim();
                    relativePrice = relativePrice.substring(0, relativePrice.indexOf(' '));
                    CLASS_LOGGER.trace("{} {}", relativePrice, nf.parse(relativePrice));
                    fields.put("relativePrice", nf.parse(relativePrice));
                }
                else
                {
                    matcher = pattern.matcher(input);
                    if (matcher.matches())
                    {
                        CLASS_LOGGER.trace(input);
                        fields.put("amount", java.lang.Integer.parseInt(matcher.group(1)));
                        tags.put("unit", matcher.group(2));
                    }
                }
            }
            java.lang.String lineFormat = lineProtocolWriter.write(tags,fields);
            CLASS_LOGGER.debug(lineFormat);
            influxDBHttpInterface.send(lineFormat);
            ++counter;
            if (counter % 1000 == 0)
                CLASS_LOGGER.info("processed item {}", counter);
        }
    }
    private void doUpdate() throws JSONException, IOException, ParseException, InterruptedException
    {
        CLASS_LOGGER.trace("doInitialImport");
        lineProtocolWriter = new LineProtocolWriter(measurementName);
        LoginDialog.BasicCredentials bc = null;
        if ((influxDbUsername != null) && (influxDbPassword != null))
            bc = new LoginDialog.BasicCredentials(influxDbUsername, influxDbPassword);
        influxDBHttpInterface = new de.elbosso.util.io.InfluxDBHttpInterface(influxDbScheme.name(), influxDbHost, influxDbPort, influxDbDatabasename, bc);
        int pageNumber=1;
        java.lang.String jsonAsString=getPageUsingCurl(pageNumber);
        org.json.JSONObject json=new org.json.JSONObject(jsonAsString);
        handle(json,"reweDe");
        int totalPages=json.getInt("totalPages");
        CLASS_LOGGER.debug(json.getString("page"));
        java.util.List<java.lang.String> lineFormats=new java.util.LinkedList();
        while(totalPages>pageNumber)
        {
            ++pageNumber;
            jsonAsString=getPageUsingCurl(pageNumber);
            json=new org.json.JSONObject(jsonAsString);
            CLASS_LOGGER.debug(json.getString("page"));
            handle(json,"reweDe");
            totalPages=json.getInt("totalPages");
            CLASS_LOGGER.debug(jsonAsString);
        }
        influxDBHttpInterface.flush();

    }

    enum Market
    {
        rewede;
    }
}
