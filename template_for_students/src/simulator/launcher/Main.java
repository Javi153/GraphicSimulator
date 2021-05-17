package simulator.launcher;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.control.StateComparator;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.factories.*;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;

public class Main {

	// Valores por defecto para algunos parametros
	//
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static String _forceLawsDefaultValue = "nlug";
	private final static String _stateComparatorDefaultValue = "epseq";

	private final static Integer _stepsDefaultValue = 150;

	// atributos correspondientes a valores que se han de introducir por la linea de comandos
	//
	private static Double _dtime = null; 	//Tiempo que pasa en cada paso de la simulacion
	private static String _oFile = null;	//Archivo de salida
	private static String _eoFile = null;	//Achibo de salida experada
	private static String _inFile = null;	//Archivo de entrada
	private static String _mode = null;
	private static JSONObject _forceLawsInfo = null;	
	private static JSONObject _stateComparatorInfo = null;

	private static Integer _steps = null;

	// factorias
	private static Factory<Body> _bodyFactory;
	private static Factory<ForceLaws> _forceLawsFactory;
	private static Factory<StateComparator> _stateComparatorFactory;

	private static void init() {
		// inicializamos la factoria de cuerpos

		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>();

		bodyBuilders.add(new BasicBodyBuilder());
		bodyBuilders.add(new MassLosingBodyBuilder());

		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders);

		// inicializamos la factoria de leyes de fuerza

		ArrayList<Builder<ForceLaws>> lawBuilders = new ArrayList<>();

		lawBuilders.add(new NewtonUniversalGravitationBuilder());
		lawBuilders.add(new NoForceBuilder());
		lawBuilders.add(new MovingTowardsFixedPointBuilder());

		_forceLawsFactory = new BuilderBasedFactory<ForceLaws>(lawBuilders);

		// inicializamos la factoria de comparadores de estado

		ArrayList<Builder<StateComparator>> stateBuilders = new ArrayList<>();

		stateBuilders.add(new EpsilonEqualStatesBuilder());
		stateBuilders.add(new MassEqualStatesBuilder());

		_stateComparatorFactory = new BuilderBasedFactory<StateComparator>(stateBuilders);
	}

	private static void parseArgs(String[] args) {

		// definimos los valores validos para la linea de comandos
		//
		Options cmdLineOptions = buildOptions();

		// parseamos la entrada de la linea de comandos
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);

			parseHelpOption(line, cmdLineOptions);
			parseModeOption(line);
			parseInFileOption(line);
			// nuevos metodos de parseo anadidos(o, eo y s)
			if(_mode != "gui") {
				parseOutputOption(line);
			}
			parseExpectedOutputOption(line);
			if(_mode != "gui") {
				parseStepsOption(line);
			}
			
			parseDeltaTimeOption(line);
			parseForceLawsOption(line);
			parseStateComparatorOption(line);

			// Si quedan algunos argumentos restantes, entonces algo
			// esta mal en la entrada por la línea de comandos
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());
		
		/*Nuevos comandos anadidos*/
		// output
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
				.desc("Output file, where output is written.Default value: the standard output.").build());

		// expected output
		cmdLineOptions.addOption(Option.builder("eo").longOpt("expected-output").hasArg()
				.desc("The expected output file. If not provided no comparison is applied").build());

		// steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg()
				.desc("An integer representating the number of simulation steps. Default value: 150").build());
		
		//mode
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
				.desc("Execution Mode. Possible values: �batch� (Batch mode), �gui� (Graphical User Interface mode). Default value: �batch�.").build());
		
		/*Aqui acaban*/

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());

		// force laws
		cmdLineOptions.addOption(Option.builder("fl").longOpt("force-laws").hasArg()
				.desc("Force laws to be used in the simulator. Possible values: "
						+ factoryPossibleValues(_forceLawsFactory) + ". Default value: '" + _forceLawsDefaultValue
						+ "'.")
				.build());

		// gravity laws
		cmdLineOptions.addOption(Option.builder("cmp").longOpt("comparator").hasArg()
				.desc("State comparator to be used when comparing states. Possible values: "
						+ factoryPossibleValues(_stateComparatorFactory) + ". Default value: '"
						+ _stateComparatorDefaultValue + "'.")
				.build());

		return cmdLineOptions;
	}

	public static String factoryPossibleValues(Factory<?> factory) {
		if (factory == null)
			return "No values found (the factory is null)";

		String s = "";

		for (JSONObject fe : factory.getInfo()) {
			if (s.length() > 0) {
				s = s + ", ";
			}
			
			s = s + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
		}

		s = s + ". You can provide the 'data' json attaching :{...} to the tag, but without spaces.";
		return s;
	}

	
	/*Metodos nuevos*/
	/**
	 * Parsea la entrada de la línea de comandos para el valor 'o' 
	 * @param line	Línea de comandos
	 * @throws ParseException	Se devuelve esta excepción si el archivo de salida no se ha cargado bien
	 */
	private static void parseOutputOption(CommandLine line) throws ParseException{
			if(line.hasOption("o")) {
				_oFile = line.getOptionValue("o");
				if(_oFile == null) {
					throw new ParseException("The command -o, --output <arg> requires a valid file");
			}
		}
	}

	/**
	 * 
	 * Parsea la entrada de la línea de comandos par el valor "eo"
	 * @param line	Línea de comandos
	 * @throws ParseException	Se devuelve esta excepción si el archivo no se ha cargado bien
	 */
	private static void parseExpectedOutputOption(CommandLine line) throws ParseException {
		if(line.hasOption("eo")) {
			_eoFile = line.getOptionValue("eo");
			if(_eoFile == null) {
				throw new ParseException("The command -eo, --expected-output <arg> requires a valid file");
			}
		}
	}

	/**
	 * Carga los paso a ejcutar en el simulador
	 * @param line	Linea de comandos
	 */
	private static void parseStepsOption(CommandLine line) {
		String stps = line.getOptionValue("s", _stepsDefaultValue.toString());
		_steps = Integer.parseInt(stps);
	}
	/*Acaban metodos nuevos*/
	
	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null && _mode.equals("batch")) {
			throw new ParseException("In batch mode an input file of bodies is required");
		}
	}
	
	private static void parseModeOption(CommandLine line) throws ParseException{
		String mode = line.getOptionValue("m");
		if(mode == null) {_mode = "gui";}
		else if(!mode.equals("gui") && !mode.equals("batch")) { throw new ParseException("Invalid mode: " + mode); }
		else { _mode = mode; }
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static JSONObject parseWRTFactory(String v, Factory<?> factory) {

		// the value of v is either a tag for the type, or a tag:data where data is a
		// JSON structure corresponding to the data of that type. We split this
		// information
		// into variables 'type' and 'data'
		//
		int i = v.indexOf(":");
		String type = null;
		String data = null;
		if (i != -1) {
			type = v.substring(0, i);
			data = v.substring(i + 1);
		} else {
			type = v;
			data = "{}";
		}

		// look if the type is supported by the factory
		boolean found = false;
		for (JSONObject fe : factory.getInfo()) {
			if (type.equals(fe.getString("type"))) {
				found = true;
				break;
			}
		}

		// build a corresponding JSON for that data, if found
		JSONObject jo = null;
		if (found) {
			jo = new JSONObject();
			jo.put("type", type);
			jo.put("data", new JSONObject(data));
		}
		return jo;

	}

	private static void parseForceLawsOption(CommandLine line) throws ParseException {
		String fl = line.getOptionValue("fl", _forceLawsDefaultValue);
		_forceLawsInfo = parseWRTFactory(fl, _forceLawsFactory);
		if (_forceLawsInfo == null) {
			throw new ParseException("Invalid force laws: " + fl);
		}
	}

	private static void parseStateComparatorOption(CommandLine line) throws ParseException {
		String scmp = line.getOptionValue("cmp", _stateComparatorDefaultValue);
		_stateComparatorInfo = parseWRTFactory(scmp, _stateComparatorFactory);
		if (_stateComparatorInfo == null) {
			throw new ParseException("Invalid state comparator: " + scmp);
		}
	}

	private static void startBatchMode() throws Exception {

		PhysicsSimulator simulator = new PhysicsSimulator(_dtime, _forceLawsFactory.createInstance(_forceLawsInfo)); //Creamos el simulador

		InputStream is = new FileInputStream(new File(_inFile)); 		//Flujo de entrada
		InputStream eos = _eoFile == null ? null : new FileInputStream(new File(_eoFile));	//Flujo para comparar(salida esperada)
		OutputStream os = _oFile == null ? System.out : new FileOutputStream(new File(_oFile));	//Flujo de salida
		
		
		StateComparator cmp = eos == null ? null : _stateComparatorFactory.createInstance(_stateComparatorInfo);	//Comparador de estado

		Controller controller = new Controller(simulator, _bodyFactory, _forceLawsFactory);	//Creamos el controlador

		controller.loadBodies(is);	//Cargamos los cuerpos

		controller.run(_steps, os, eos, cmp); //Hacemos que se ejecuten los pasos correspondientes

	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		if(_mode.equals("batch")) {
			startBatchMode();
		}
		else {
			startGuiMode();
		}
	}
	
	private static void startGuiMode() throws InvocationTargetException, InterruptedException {
		PhysicsSimulator simulator = new PhysicsSimulator(_dtime, _forceLawsFactory.createInstance(_forceLawsInfo)); //Creamos el simulador
		Controller controller = new Controller(simulator, _bodyFactory, _forceLawsFactory);	//Creamos el controlador
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run() {
					new MainWindow(controller);
				}
			});
		}catch(InvocationTargetException e) {
			throw e;
		}catch(InterruptedException e) {
			throw e;
		}
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
