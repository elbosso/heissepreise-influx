package de.elbosso.tools.heissepreiseinflux;

import picocli.CommandLine;

@CommandLine.Command(name = "HeissePreiseInfluxdbInterface", mixinStandardHelpOptions = true, version = "HeissePreiseInfluxdbInterface 0.1.0",
        description = "Importiert historische Preisdaten von Supermärkten und aktualisiert diese mit aktuellen Informationen."
,subcommands = {Import.class, Update.class})
public class HeissePreiseInfluxdbInterface extends java.lang.Object
{
	private final static org.slf4j.Logger CLASS_LOGGER =org.slf4j.LoggerFactory.getLogger(HeissePreiseInfluxdbInterface.class);
	private final static org.slf4j.Logger EXCEPTION_LOGGER =org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");

    private HeissePreiseInfluxdbInterface()
    {
        super();
    }


    public static void main(java.lang.String[] args)
    {
        CommandLine commandLine=new CommandLine(new HeissePreiseInfluxdbInterface());
        commandLine.execute(args);
    }
/*    enum Mode
    {
        initialImport,
        updateRewe;
    }
*/    enum Scheme
    {
        HTTP,
        HTTPS;
    }

}
