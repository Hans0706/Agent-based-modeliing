package models.trading;

import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.GlobalState;
import simudyne.core.abm.Group;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Input;
import simudyne.core.annotations.ModelSettings;
import simudyne.core.annotations.Variable;
import simudyne.core.rng.SeededRandom;

import java.util.Random;


@ModelSettings(macroStep = 40)
public class TradingModel extends AgentBasedModel<TradingModel.Globals> {

    @Constant(name = "Number of Traders")
    public int nbTraders = 50;

    public static final class Globals extends GlobalState {
        @Input(name = "Update Frequency")
        public double updateFrequency = 0.01;

        @Input(name = "gdp initial")
        public double gdpInitial = 81;

        @Input(name = "temperature initial")
        public double tempInitial = 20;

        @Input(name = "Lambda")
        public double lambda = 10;

        @Input(name = "Volatility of Information Signal")
        public double volatilityInfo = 1;

        public double informationSignal;

        public double enPrice=0.1;
        @Variable
        public double Temperature=15;
        public double TempChange;
        public double c_tax=0.0068;
        Random r = new Random(1234);
        public void step() {

            enPrice = 1.1 * enPrice;

            TempChange = Temperature * (0.1* (1 - 2 *r.nextDouble()));

            Temperature += TempChange;
        }


//        public List<String[]> temperature_list;
//
//        public List<String[]> readfile(String datafile) throws IOException {
//            FileReader fileReader = new FileReader(datafile);
//            // create csvReader object and skip first Line
//            CSVReader csvReader = new CSVReaderBuilder(fileReader)
//                    .build();
//            return csvReader.readAll();
//        }

//    File csv = new File('temperature.csv');
//
//    CSVReader csvReader = new CSVReader (new InputStreamReader(csv.getInputStream()));
//    while ((record = csvReader.readNext()) != null) {
//      String [] header = reader.readNext();
//      for (String s : header) {
//        System.out.print(s + ",");
//      }
//      System.out.println("");
//
//      List<String[]> list = reader.readAll(); //read the second line
//
//    }




    }

    @Override
    public void init() {
        createDoubleAccumulator("buys", "Number of buy orders");
        createDoubleAccumulator("sells", "Number of sell orders");
        createDoubleAccumulator("gdp", "GDP Value");
        createDoubleAccumulator("laborProductivity", "LP Value");


        registerAgentTypes(Country.class,CaFirm.class,CoFirm.class,EnproFirm.class,EntechFirm.class,Labor.class);

        registerLinkTypes(Links.CaFtoCoF.class);
        registerLinkTypes(Links.CaFtoEpF.class);
        registerLinkTypes(Links.CaFtoGovern.class);
        registerLinkTypes(Links.CoFtoEpF.class);
        registerLinkTypes(Links.CoFtoGovern.class);
        registerLinkTypes(Links.CoFtoLabor.class);
        registerLinkTypes(Links.EpFtoEtF.class);
        registerLinkTypes(Links.EpFtoGovern.class);
        registerLinkTypes(Links.EtFtoGovern.class);
        registerLinkTypes(Links.LabortoEpF.class);
        registerLinkTypes(Links.LabortoCaF.class);
        registerLinkTypes(Links.LabortoGovern.class);
        registerLinkTypes(Links.CaFtoLabor.class);
        registerLinkTypes(Links.EpFtoLabor.class);
        registerLinkTypes(Links.EpFtoCaF.class);
        registerLinkTypes(Links.EpFtoCoF.class);
    }

    /**
     * Gaussian random walk the information signal, with variance of the volatility input.
     */
    private void updateSignal() {
        getGlobals().informationSignal =
                getContext().getPrng().gaussian(0, getGlobals().volatilityInfo).sample();

    }

    @Override
    public void setup() {
        Random r = new Random();
        updateSignal();
//        getGlobals().temperature_list = getGlobals().readfile('D:\\simudyne\\stock-model\\stock_model\\src\\main\\java\\models\\trading\\temperature.csv');
        Group<Labor> laborGroup = generateGroup(Labor.class, 50);
//        Group<Market> marketGroup = generateGroup(Market.class, 1, market -> market.nbTraders = nbTraders);
        Group<CaFirm> caFirmGroup = generateGroup(CaFirm.class,10);
        Group<CoFirm> coFirmGroup = generateGroup(CoFirm.class,5);
        Group<EnproFirm> enproFirmGroup = generateGroup(EnproFirm.class,1);
        Group<EntechFirm> entechFirmGroup=generateGroup(EntechFirm.class,1);
        Group<Country> countryGroup = generateGroup(Country.class,1);

        caFirmGroup.fullyConnected(coFirmGroup, Links.CaFtoCoF.class);
        coFirmGroup.fullyConnected(caFirmGroup, Links.CaFtoCoF.class);

        laborGroup.fullyConnected(coFirmGroup, Links.CoFtoLabor.class);
        coFirmGroup.fullyConnected(laborGroup, Links.CoFtoLabor.class);

        laborGroup.fullyConnected(caFirmGroup, Links.LabortoCaF.class);
        caFirmGroup.fullyConnected(laborGroup, Links.CaFtoLabor.class);

        enproFirmGroup.fullyConnected(entechFirmGroup,Links.EpFtoEtF.class);
        entechFirmGroup.fullyConnected(enproFirmGroup,Links.EpFtoEtF.class);

        caFirmGroup.fullyConnected(countryGroup, Links.CaFtoGovern.class);
        countryGroup.fullyConnected(caFirmGroup, Links.CaFtoGovern.class);

        laborGroup.fullyConnected(countryGroup, Links.LabortoGovern.class);
        countryGroup.fullyConnected(laborGroup, Links.LabortoGovern.class);

        coFirmGroup.fullyConnected(countryGroup, Links.CoFtoGovern.class);
        countryGroup.fullyConnected(coFirmGroup, Links.CoFtoGovern.class);

        enproFirmGroup.fullyConnected(countryGroup,Links.EpFtoGovern.class);
        countryGroup.fullyConnected(enproFirmGroup,Links.EpFtoGovern.class);

        entechFirmGroup.fullyConnected(countryGroup,Links.EtFtoGovern.class);
        countryGroup.fullyConnected(entechFirmGroup,Links.EtFtoGovern.class);

        enproFirmGroup.fullyConnected(laborGroup, Links.EpFtoLabor.class);
        laborGroup.fullyConnected(enproFirmGroup,Links.EpFtoLabor.class);

        enproFirmGroup.fullyConnected(caFirmGroup, Links.EpFtoCaF.class);
        caFirmGroup.fullyConnected(enproFirmGroup,Links.EpFtoCaF.class);

        enproFirmGroup.fullyConnected(coFirmGroup, Links.EpFtoCoF.class);
        coFirmGroup.fullyConnected(enproFirmGroup,Links.EpFtoCoF.class);

        super.setup();

    }

    @Override
    public void step() {
        super.step();

        getGlobals().step();
        updateSignal();

//        run(Labor.updateWage(),CaFirm.conductBehavior,CoFirm.conductBehavior);
//        run(EntechFirm.calcCost,EnproFirm.conductBehavior);
        run(Labor.updateLP());
        run(Labor.updateWage());
//       CaFirm.conductBehavior,CoFirm.conductBehavior,Labor.processInformation());
//        run(Labor.processInformation(),Country.updateTax);
//        run(CoFirm.conductBehavior,Country.updateTax);
//        run(CaFirm.conductBehavior,Country.updateTax);
//        run(EntechFirm.calcCost,EnproFirm.conductBehavior,Country.updateTax);
//        run(EntechFirm.sendTax,Country.updateTax);
        run(Labor.processInformation());
        run(CoFirm.conductBehavior);
        run(CaFirm.conductBehavior);
        run(EntechFirm.calcCost,EnproFirm.conductBehavior);
        run(EntechFirm.sendTax);
        run(Country.updateTax);
//        run(EnproFirm.conductBehavior,Country.updateTax);




//        run(Trader.processInformation(), Market.calcPriceImpact(),Trader.updateTradevalue());
//        run(Temperature.calcTempchange,  Trader.updateTradevalue());

    }


}

