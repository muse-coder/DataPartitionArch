//
//import CGRA.Crossbar.{Crossbar_Submodule, DataCrossbar}
////import CGRA.LSU_module.Crossbar
//import chisel3._
//import chiseltest._
//import org.scalatest.flatspec.AnyFlatSpec
//
//import java.io._
//
//
//// 重定向标准输出到文件
//
//class CrossbarTest extends AnyFlatSpec with ChiselScalatestTester {
//
//  "all Crossbar test" should "pass" in {
//
//    test(new DataCrossbar (dType = UInt(32.W))) { dut =>
//      val outputFile = new File("./print.txt")
////      val printStream = new PrintStream(new FileOutputStream(outputFile))
////      val outputFile = new File(s"output_$i.txt")
//      val printWriter = new PrintWriter(new FileWriter(outputFile))
////      System.setOut(printStream)
//      for (i<-0 until 10){
//        val random_store_sel = scala.util.Random.nextInt(2111)
//        val random_load_sel = scala.util.Random.nextInt(555)
//        for (j <- 0 until 8){
//          val store_sel =(random_store_sel+j)%8
//          dut.data_io.bank_store_sel(j).poke(store_sel.U)
//          val load_sel = (random_load_sel + j) % 8
//          dut.data_io.bank_load_sel(j).poke(load_sel.U)
//          dut.data_io.data_from_lsu(j).poke((j+100).U)
//          dut.data_io.data_from_bank(j).poke((j+200).U)
//        }
//
//        for (j<-0 until 8){
//          printWriter.println(s"***********bank $j ***********")
//          printWriter.println(s"bank_data : ${dut.data_io.data_from_bank(j).peek().litValue}; lsu_data : ${dut.data_io.data_from_lsu(j).peek().litValue} ")
//          printWriter.println(s"data to lsu $j from bank  ${dut.data_io.bank_load_sel(j).peek().litValue}  ; data to bank  from lsu: ${dut.data_io.bank_store_sel(j).peek().litValue} ")
//        }
//
//        for (j <- 0 until 8) {
//          printWriter.println(s"data to bank $j : ${dut.data_io.data_to_bank(j).peek().toString()} ; data to lsu $j : ${dut.data_io.data_to_lsu(j).peek().toString()} ")
//        }
//
//
//        dut.clock.step(1)
//      }
////      printStream.close()
//
//    }
//
//  }
//
//  " Crossbar submodule test" should "pass" in {
//
//    test(new Crossbar_Submodule(dType = UInt(32.W))) { dut =>
//      val outputFile = new File("./print.txt")
//      //      val printStream = new PrintStream(new FileOutputStream(outputFile))
//      //      val outputFile = new File(s"output_$i.txt")
//      val printWriter = new PrintWriter(new FileWriter(outputFile))
//      //      System.setOut(printStream)
//      for (i <- 0 until 10) {
////        val random_store_sel = scala.util.Random.nextInt(2111)
//        val random_sel = scala.util.Random.nextInt(555)
//        for (j <- 0 until 8) {
//          val sel = (random_sel + j+3) % 8
//          dut.io.sel(j).poke(sel.U)
//          dut.io.in(j).poke((j + 200).U)
//        }
//
//        for (j <- 0 until 8) {
//          printWriter.println(s"***********bank $j ***********")
//          //          printWriter.println(s"data load sel $j : ${dut.io.bank_load_sel(j).peek().toString()} ; data store sel $j : ${dut.io.bank_store_sel(j).peek().toString()} ")
//          printWriter.println(s"data  out_port $j from in_Port ${dut.io.sel(j).peek().litValue}  ")
//          printWriter.println(s"data in  : ${dut.io.in(j).peek().litValue} ")
//        }
//
//        for (j <- 0 until 8) {
//          printWriter.println(s"data out port $j : ${dut.io.out(j).peek().toString()} ")
//        }
//
//
//        dut.clock.step(1)
//      }
//      //      printStream.close()
//
//    }
//
//  }
//}