package me.flyray.bsin.server;

import com.alibaba.excel.EasyExcel;
import me.flyray.bsin.server.curveexcel.CurveData;
import org.apache.commons.math3.analysis.function.Pow;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class BondingCureTest {

  String EXCELPATH = "./src/test/java/me/flyray/bsin/server/";
  List<CurveData> curveDataList = new ArrayList<CurveData>();

  double decimal = 18;

  BigDecimal bigDecimal = new BigDecimal("1000000000000000000");
  //sigmoid曲线参数
  BigDecimal cap = bigDecimal.multiply(BigDecimal.valueOf(21000000)); // 供应量2100万,TVL，项目估值，价格接近s曲线最高点时的代币供应量
  double initialPrice = 0.01;      //起始价格：￥0.01
  double finalPrice = 1;    //最终价格 ￥10.0



  //BigDecimal 浮点建议使用string来初始化
  BigDecimal flexible = new BigDecimal(3.0);        //曲线斜率

  BigDecimal currentSupply = BigDecimal.valueOf(0); // 当前供应量
  double currentPrice = 0.0; //当前价格



  @Test
  public void hello() throws ClassNotFoundException {
    System.out.println("bonding curve test..........");
  }

  /**
   * 基于联合曲线积分铸造测试：Bancor曲线的数学模型
   * @throws Exception
   */
  @Test
  public void bondingCurveBancorTest() throws Exception {

  }
  /**
   * 基于联合曲线积分铸造测试：sigmoid曲线的数学模型
   * @throws Exception
   */
  @Test
  public void bondingCurveSigmoidMintTest() throws Exception {

    BigDecimal mintAmount = BigDecimal.valueOf(10000);
    BigDecimal mintReturn = BigDecimal.valueOf(0);
    for (int i = 0; i <2100; i ++){
      mintAmount =BigDecimal.valueOf(10000.0*i);
      CurveData curveData = new CurveData();
      curveData.setNum(String.valueOf(i));
      curveData.setDate(new Date());
//      currentPrice = bondingCurve(mintAmount);
      currentPrice = bondingCurve(mintAmount);
      currentSupply = mintAmount;
      curveData.setMintAmount(mintAmount);
      curveData.setCurrentSupply(currentSupply);
      curveData.setCurrentPrice(currentPrice);
      curveDataList.add(curveData);
    }
    String fileName = EXCELPATH + "sigmoidMint.xlsx";
    EasyExcel.write(fileName, CurveData.class).sheet("Mint").doWrite(curveDataList);

  }

  /**
   * 基于联合曲线积分铸造测试：sigmoid曲线的数学模型，18位精度
   * @throws Exception
   */
  @Test
  public void bondingCurveSigmoid18MintTest() throws Exception {

    BigDecimal mintAmount = BigDecimal.valueOf(10000);
    BigDecimal mintReturn = BigDecimal.valueOf(0);
    for (int i = 0; i <2100; i ++){
      mintAmount =BigDecimal.valueOf(10000*Math.pow(10,decimal)*i);
      CurveData curveData = new CurveData();
      curveData.setNum(String.valueOf(i));
      curveData.setDate(new Date());
//      currentPrice = bondingCurve(mintAmount);
      currentPrice = bondingCurve(mintAmount);
      currentSupply = mintAmount;
      curveData.setMintAmount(mintAmount);
      curveData.setCurrentSupply(currentSupply);
      curveData.setCurrentPrice(currentPrice);
      curveDataList.add(curveData);
    }
    String fileName = EXCELPATH + "sigmoid18Mint.xlsx";
    EasyExcel.write(fileName, CurveData.class).sheet("Mint").doWrite(curveDataList);

  }

  /**
   * 基于联合曲线积分铸造测试：捕获劳动价值，获得的积分记录
   * @throws Exception
   */
  @Test
  public void bondingCurveSigmoidPurchaseTest() throws Exception {

    // 铸造的劳动价值
    BigDecimal depositAmount = BigDecimal.valueOf(10000);
    BigDecimal mintReturn = BigDecimal.valueOf(0);
    for (int i = 0; i <21000; i ++){
      CurveData curveData = new CurveData();
      curveData.setNum(String.valueOf(i));
      curveData.setDate(new Date());
      currentPrice = bondingCurve(currentSupply);
      BigDecimal rewardAmount = bondingCurveMintPrice(currentSupply,depositAmount);
      currentSupply =  currentSupply.add(rewardAmount);
      curveData.setMintAmount(depositAmount);
      curveData.setMintReturn(rewardAmount);
      curveData.setCurrentSupply(currentSupply);
      curveData.setCurrentPrice(currentPrice);
      curveDataList.add(curveData);
    }
    String fileName = EXCELPATH + "sigmoidPurchase.xlsx";
    EasyExcel.write(fileName, CurveData.class).sheet("Purchase").doWrite(curveDataList);

  }


  /**
   * 基于联合曲线积分铸造测试：捕获劳动价值，获得的积分记录,带18位小数点
   * @throws Exception
   */
  @Test
  public void bondingCurveSigmoidPurchase18Test() throws Exception {
    BigDecimal depositAmount = BigDecimal.valueOf(100000);
    BigDecimal mintReturn = BigDecimal.valueOf(0);
    for (int i = 0; i <10000; i ++){
      CurveData curveData = new CurveData();
      curveData.setNum(String.valueOf(i));
      curveData.setDate(new Date());
      currentPrice = bondingCurve(currentSupply);
      BigDecimal rewardAmount = bondingCurveMintPrice(currentSupply,depositAmount);
      currentSupply = currentSupply.add(rewardAmount);
      curveData.setMintAmount(depositAmount);
      curveData.setMintReturn(rewardAmount);
      curveData.setCurrentSupply(currentSupply);
      curveData.setCurrentPrice(currentPrice);
      curveDataList.add(curveData);
    }
    String fileName = EXCELPATH + "sigmoidPurchase18.xlsx";
    EasyExcel.write(fileName, CurveData.class).sheet("Purchase").doWrite(curveDataList);

  }



  /**
   * @description: sigmoid 曲线生成，当fStart<fStop时，曲线为上升曲线，反之为下降曲线，相等时为平行直线
   * @param len:S曲线的长度，即采样点个数
   * @param fStart：曲线的起始值
   * @param fStop：曲线的结束值
   * @param flexible：曲线的拉伸变换，越大代表压缩的最厉害，中间（x坐标0点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。
   * @param index：曲线索引点，区间[0,len]
   * @return fCurrent：索引点对应的曲线幅值
   */
  double motorPower_PowerSLine(int len, double fStart, double fStop, double flexible, int index) {
    double deno;
    double melo;
    int num;
    double fCurrent;
    if (index > len) index = len;
    num = len / 2;
    melo = flexible * (index - num) / num;
    deno = 1.0 / (1 + Math.exp(-melo));
    fCurrent = fStart - (fStart - fStop) * deno;
    return fCurrent;
  }

  /**
   * @description: 基于sigmoid曲线公式的联合曲线积分铸造
   * @param cap: token理论上限值，基于联合曲线铸造的积分为增发不限量模型，cap为理论上限值，当流通量为此数值时稳定为 finalPrice 价格
   * @param initialPrice：初始定价：0.01
   * @param finalPrice：稳定定价-可以对标法币进行劳动价值捕获，当前任务价值￥100，则按照当前价格铸造出相应的积分
   * @param flexible：曲线的拉伸变换，越大代表压缩的最厉害，中间（x坐标cap/2点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。
   * @param currentSupply：当前代币供应量，区间[0,cap]
   * @return currentPrice：当前mint价格
   */
  double bondingCurve(BigDecimal currentSupply) {
    BigDecimal num = cap.divide(BigDecimal.valueOf(2.0),5,BigDecimal.ROUND_HALF_UP);
    double melo = currentSupply.subtract(num).divide(num,5,BigDecimal.ROUND_HALF_UP).multiply(flexible).doubleValue();
    double deno = 1.0 / (1 + Math.exp(-melo));
    double currentPrice = initialPrice - (initialPrice - finalPrice) * deno;
    return (double)currentPrice;
  }

  /**
    *@description: 铸造一个花费的法币
   **/
  double bondingCurveMintPrice(BigDecimal currentSupply) {
    return bondingCurve(currentSupply.add(BigDecimal.valueOf(1.0)))-bondingCurve(currentSupply);
  }


  /**
   *@description: 铸造劳动价值为amount，获得的代币数量
   **/
  BigDecimal bondingCurveMintPrice(BigDecimal currentSupply,BigDecimal depositAmount) {
    BigDecimal rewardAmount=new BigDecimal("0");
    BigDecimal loop = depositAmount.divide(BigDecimal.valueOf(1000),5,BigDecimal.ROUND_HALF_UP);
    BigDecimal remainder = depositAmount.remainder(BigDecimal.valueOf(100));
    BigDecimal initialSupply = currentSupply;
    // 每10元重新计算一次
    for (int i = 0; i < loop.intValue(); i++) {
     double unitPrice = bondingCurve(initialSupply);
      rewardAmount =BigDecimal.valueOf(1000.0 /unitPrice) .multiply(bigDecimal);
      initialSupply = initialSupply.add(rewardAmount);
    }
    if (remainder.compareTo(BigDecimal.valueOf(0)) == 1) {
      rewardAmount =rewardAmount.add(BigDecimal.valueOf((long)(remainder.doubleValue() / bondingCurve(initialSupply))));
    }
    return rewardAmount;
  }


  /**
   *@description: 铸造劳动价值为amount，获得的代币数量，18位小数点
   **/
  BigDecimal bondingCurve18MintPrice(BigDecimal currentSupply, BigDecimal depositAmount) {
    BigDecimal rewardAmount=new BigDecimal("0");
    BigDecimal loop = depositAmount.divide(BigDecimal.valueOf(100.0),5,BigDecimal.ROUND_HALF_UP);
    BigDecimal remainder = depositAmount.remainder(BigDecimal.valueOf(100));
    BigDecimal initialSupply = currentSupply;
    // 每10元重新计算一次
    for (int i = 0; i < loop.intValue(); i++) {
      double unitPrice = bondingCurve(initialSupply);
      rewardAmount =rewardAmount.add (BigDecimal.valueOf((long)(100.0/unitPrice))) ;
      initialSupply = currentSupply.add(rewardAmount);
    }
    if (remainder.compareTo(BigDecimal.valueOf(0)) == 1) {
      rewardAmount =rewardAmount.add(BigDecimal.valueOf((long)(remainder.doubleValue() / bondingCurve(initialSupply))));
    }
    return rewardAmount;
  }



}
