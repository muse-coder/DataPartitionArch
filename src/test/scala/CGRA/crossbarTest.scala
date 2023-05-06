
import CGRA.Crossbar.CrossbarTop

import scala.util.Random
//import CGRA.LSU_module.Crossbar
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import java.io._


// 重定向标准输出到文件

class CrossbarTest extends AnyFlatSpec with ChiselScalatestTester {

  "all Crossbar test" should "pass" in {

    test(new CrossbarTop (addrWidth = 8,dataWidth = 32  )) { dut =>
      val outputFile = new File("./print.txt")
//      val printStream = new PrintStream(new FileOutputStream(outputFile))
//      val outputFile = new File(s"output_$i.txt")
      val printWriter = new PrintWriter(new FileWriter(outputFile))
//      System.setOut(printStream)
      var address = 10
      var dataFromCrossbar = 100
      var dataFromBank = 500
      import scala.util.Random


        for (i<-0 until 10) {
          val random = new Random()
          val randomSeq = Random.shuffle(Seq(0, 1, 2, 3, 4, 5, 6, 7))
          address = address + 10
          dataFromCrossbar = dataFromCrossbar + 100
          dataFromBank = dataFromBank + 100
          printWriter.println(s"***********Input ***********")

          for (j <- 0 until 8) {

            val bankSel = randomSeq(j)
            dut.crossbar_lsu_io(j).bankID.poke(bankSel.U)
            dut.crossbar_lsu_io(j).addresToCrossbar.poke((address + j).U)
            dut.crossbar_lsu_io(j).dataToCrossbar.poke((dataFromCrossbar + j).U)
            dut.crossbar_lsu_io(j).dataOutValid.poke(1.U)
            dut.crossbar_lsu_io(j).readOrWrite.poke(1.U)
            dut.bank_crossbar_io(j).dataFromBank.poke((dataFromBank+j).U)
            dut.bank_crossbar_io(j).dataInValid.poke(1.U)
            printWriter.println(s"***********crossbar $i ***********")
            printWriter.print(s"addresIn : ${dut.crossbar_lsu_io(j).addresToCrossbar.peek().litValue}; dataFromLsu : ${dut.crossbar_lsu_io(j).dataToCrossbar.peek().litValue} ,sel :${dut.crossbar_lsu_io(j).bankID.peek().litValue} ")
            printWriter.print(s"  dataFromBank : ${dut.bank_crossbar_io(j).dataFromBank.peek().litValue} \n")
          }


          printWriter.println(s"***********Output ***********")
          for (j <- 0 until 8) {
            printWriter.println(s"***********crossbar $j ***********")
            printWriter.print(s"dataToLsu : ${dut.crossbar_lsu_io(j).dataFromCrossbar.peek().litValue}; ")
            printWriter.print(s"   dataToBank : ${dut.bank_crossbar_io(j).dataToBank.peek().litValue} ")
            printWriter.print(s"   address : ${dut.bank_crossbar_io(j).addressToBank.peek().litValue} \n")

          }

          dut.clock.step(1)

        }



      }
    }
}