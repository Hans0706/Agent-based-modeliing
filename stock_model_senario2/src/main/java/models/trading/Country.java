package models.trading;

import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;
import java.util.Random;
public class Country extends Agent<TradingModel.Globals> {

    @Variable(name = "GDP")
    public double gdp_value = 4039;
    @Variable
    public double taxValue;
    public double w;
    public double old_t;
    public double old_w;
    public double ePrice;
    public double emissions;

    public void init() {
        taxValue = 0;
        gdp_value = 40369;
        w = 2000;
    }

    public static Action<Country> receiveWage =

            Action.create(Country.class, country -> {
                country.getMessagesOfType(Messages.WageChange.class).forEach(mes -> {
                    country.w = mes.wage;
                });
            });

//    public static Action<Country> receiveTax =
//            Action.create(Country.class, country -> {
//
//            });

    public static Action<Country> receiveEnergyPrices =
            Action.create(Country.class, country -> {
                country.getMessagesOfType(Messages.SendenergyPrice.class).forEach(mes -> {
                    country.ePrice = mes.price;
                });
            });

    public static Action<Country> receiveEmissions =
            Action.create(Country.class, country -> {
                country.getMessagesOfType(Messages.sendEmissions.class).forEach(mes -> {
                    country.emissions = mes.emissions;
                });
               country.getDoubleAccumulator("emission").add(country.emissions);
            });

    public static Action<Country> updateTax =
            Action.create(Country.class, country -> {
                Random r = new Random();
                country.old_t = country.taxValue;
                country.old_w = country.w;
                double t_change = 1;
                double w_change = 1;
                double g_change = 1;
                country.getMessagesOfType(Messages.sendTax.class).forEach(mes -> {
                    country.taxValue += mes.CaF_tax + mes.CoF_tax + mes.EpF_tax + mes.EtF_tax + mes.Labor_tax;
                });


                if (country.old_t != 0 && country.taxValue != 0) {
                    t_change = (country.taxValue - country.old_t) / country.old_t;
                    w_change = (country.w - country.old_w) / country.old_w;
                } else {
                    t_change = w_change = 0;
                }
                if(country.getGlobals().mode==1){
                    country.gdp_value += 0.017 * country.gdp_value+ t_change + w_change-1000*r.nextDouble();
                }


                else if (country.getGlobals().mode==2){
                    country.gdp_value += -0.018 * country.gdp_value+ t_change + w_change+1000*r.nextDouble();
                }

                else{
                    if(country.getGlobals().year_num<30){
                        country.gdp_value += -0.018 * country.gdp_value+ t_change + w_change+1000*r.nextDouble();

                    }
                    else{
                        country.gdp_value += 0.013 * country.gdp_value+ t_change + w_change-500*r.nextDouble();

                    }
                }


                country.getDoubleAccumulator("gdp").add(country.gdp_value);
                System.out.println(country.gdp_value);

            });
// //scenario 2
//    //country.gdp_value += -0.015 * country.gdp_value+ t_change + w_change+1000*r.nextDouble();
//    public class text {
//        public static void main(String[] args) throws Exception {
//            //第一步：设置输出的文件路径
//            //如果该目录下不存在该文件，则文件会被创建到指定目录下。如果该目录有同名文件，那么该文件将被覆盖。
//            File writeFile = new File("G:\\FileSave\\dataFile\\write.csv");
//
//            try{
//                //第二步：通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
//                BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
//
//                //第三步：将文档的下一行数据赋值给lineData，并判断是否为空，若不为空则输出
//                for(int i=1;i<=10;i++){
//                    writeText.newLine();    //换行
//                    //调用write的方法将字符串写到流中
//                    writeText.write("新用户"+i+",男,"+(18+i));
//                }
//
//                //使用缓冲区的刷新方法将数据刷到目的地中
//                writeText.flush();
//                //关闭缓冲区，缓冲区没有调用系统底层资源，真正调用底层资源的是FileWriter对象，缓冲区仅仅是一个提高效率的作用
//                //因此，此处的close()方法关闭的是被缓存的流对象
//                writeText.close();
//            }catch (FileNotFoundException e){
//                System.out.println("没有找到指定文件");
//            }catch (IOException e){
//                System.out.println("文件读写出错");
//            }
//        }
//    }

}
